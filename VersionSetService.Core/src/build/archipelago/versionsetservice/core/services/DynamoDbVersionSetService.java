package build.archipelago.versionsetservice.core.services;

import build.archipelago.common.ArchipelagoBuiltPackage;
import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.common.dynamodb.AV;
import build.archipelago.common.exceptions.VersionSetDoseNotExistsException;
import build.archipelago.versionsetservice.core.models.Revision;
import build.archipelago.versionsetservice.core.models.VersionSet;
import build.archipelago.versionsetservice.core.models.VersionSetRevision;
import build.archipelago.versionsetservice.core.utils.RevisionUtil;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class DynamoDbVersionSetService implements VersionSetService {

    private final AmazonDynamoDB dynamoDB;
    private final DynamoDbVersionSetServiceConfig config;

    public DynamoDbVersionSetService(final AmazonDynamoDB dynamoDB,
                                     final DynamoDbVersionSetServiceConfig config) {
        this.dynamoDB = dynamoDB;
        this.config = config;
    }

    @Override
    public VersionSet get(String versionSetName) {
        GetItemResult result = dynamoDB.getItem(new GetItemRequest(config.getVersionSetTable(),
                ImmutableMap.<String, AttributeValue>builder()
                        .put(Constants.ATTRIBUTE_NAME, AV.of(sanitizeName(versionSetName)))
                        .build()));

        if (result.getItem() == null) {
            return null;
        }
        Map<String, AttributeValue> dbItem = result.getItem();

        List<ArchipelagoPackage> targets = new ArrayList<>();
        if (dbItem.get(Constants.ATTRIBUTE_TARGETS) != null) {
            targets.addAll(dbItem.get(Constants.ATTRIBUTE_TARGETS).getSS().stream()
                    .map(ArchipelagoPackage::parse).collect(Collectors.toList()));
        }

        Optional<String> parent = Optional.empty();
        if (dbItem.get(Constants.ATTRIBUTE_PARENT) != null) {
            parent = Optional.of(dbItem.get(Constants.ATTRIBUTE_PARENT).getS());
        }

        Optional<String> latestRevision = Optional.empty();
        Optional<Instant> latestRevisionCreated = Optional.empty();
        if (dbItem.get(Constants.ATTRIBUTE_REVISION_LATEST) != null) {
            latestRevision = Optional.of(dbItem.get(Constants.ATTRIBUTE_REVISION_LATEST).getS());
            latestRevisionCreated = Optional.of(AV.toInstant(dbItem.get(Constants.ATTRIBUTE_REVISION_CREATED)));
        }

        List<Revision> revisions = getRevisions(versionSetName);

        return VersionSet.builder()
                .name(dbItem.get(Constants.ATTRIBUTE_DISPLAY_NAME).getS())
                .created(Instant.ofEpochMilli(Long.parseLong(dbItem.get(Constants.ATTRIBUTE_CREATED).getN())))
                .parent(parent)
                .targets(targets)
                .revisions(revisions)
                .latestRevision(latestRevision)
                .latestRevisionCreated(latestRevisionCreated)
                .build();
    }

    private List<Revision> getRevisions(String versionSetName) {
        QueryRequest queryRequest = new QueryRequest()
                .withTableName(config.getVersionSetRevisionTable())
                .withProjectionExpression("#revision, #created")
                .withKeyConditionExpression("#name = :nameValue")
                .withExpressionAttributeNames(ImmutableMap.of(
                        "#name", Constants.ATTRIBUTE_NAME,
                        "#revision", Constants.ATTRIBUTE_REVISION,
                        "#created", Constants.ATTRIBUTE_CREATED
                        ))
                .withExpressionAttributeValues(ImmutableMap.of(":nameValue",
                        new AttributeValue().withS(sanitizeName(versionSetName))));

        QueryResult result = dynamoDB.query(queryRequest);
        if (result.getItems() == null || result.getItems().size() == 0) {
            return new ArrayList<>();
        }
        return result.getItems().stream().map(x -> Revision.builder()
            .revisionId(x.get(Constants.ATTRIBUTE_REVISION).getS())
            .created(AV.toInstant(x.get(Constants.ATTRIBUTE_CREATED)))
            .build()).collect(Collectors.toList());
    }

    @Override
    public VersionSetRevision getRevision(String versionSetName, String revision) throws VersionSetDoseNotExistsException {
        Preconditions.checkNotNull(versionSetName);
        Preconditions.checkNotNull(revision);

        GetItemResult result = dynamoDB.getItem(new GetItemRequest(config.getVersionSetRevisionTable(),
                ImmutableMap.<String, AttributeValue>builder()
                .put(Constants.ATTRIBUTE_NAME, AV.of(sanitizeName(versionSetName)))
                .put(Constants.ATTRIBUTE_REVISION, AV.of(revision.toLowerCase()))
                .build()));
        if (result.getItem() == null) {
            throw new VersionSetDoseNotExistsException(versionSetName, revision);
        }

        return VersionSetRevision.builder()
                .created(AV.toInstant(result.getItem().get(Constants.ATTRIBUTE_CREATED)))
                .packages(result.getItem().get(Constants.ATTRIBUTE_PACKAGES).getSS().stream()
                        .map(ArchipelagoBuiltPackage::parse).collect(Collectors.toList()))
                .build();
    }

    @Override
    public void create(final String name, final List<ArchipelagoPackage> targets, final Optional<String> parent) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(targets);
        for (ArchipelagoPackage t : targets) {
            if (t instanceof ArchipelagoBuiltPackage) {
                throw new IllegalArgumentException(t.toString() + " can not have a build version");
            }
        }

        ImmutableMap.Builder<String, AttributeValue> map = ImmutableMap.<String, AttributeValue>builder()
                .put(Constants.ATTRIBUTE_NAME, AV.of(sanitizeName(name)))
                .put(Constants.ATTRIBUTE_DISPLAY_NAME, AV.of(name))
                .put(Constants.ATTRIBUTE_CREATED, AV.of(Instant.now()))
                .put(Constants.ATTRIBUTE_TARGETS,
                    AV.of(targets.stream().map(x -> ((ArchipelagoPackage)x).toString()).collect(Collectors.toList())));
        if (parent != null && parent.isPresent()) {
            map.put(Constants.ATTRIBUTE_PARENT, AV.of(sanitizeName(parent.get())));
        }

        dynamoDB.putItem(new PutItemRequest(config.getVersionSetTable(), map.build()));
    }

    @Override
    public String createRevision(String versionSetName, List<ArchipelagoBuiltPackage> packages)
            throws VersionSetDoseNotExistsException {
        Preconditions.checkNotNull(versionSetName);
        Preconditions.checkNotNull(packages);
        Preconditions.checkArgument(packages.size() > 0);

        String revisionId = RevisionUtil.getRandomRevisionId();

        Instant now = Instant.now();

        // Update the latest revision table
        try {
            UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                    .withTableName(config.getVersionSetTable())
                    .addKeyEntry(Constants.ATTRIBUTE_NAME, AV.of(sanitizeName(versionSetName)))
                    .withUpdateExpression("set #revisionLatest = :revisionLatest, #revisionCreated = :revisionCreated")
                    .withConditionExpression("attribute_exists(#name)")
                    .withReturnItemCollectionMetrics(ReturnItemCollectionMetrics.SIZE)
                    .withExpressionAttributeNames(ImmutableMap.of(
                            "#revisionLatest", Constants.ATTRIBUTE_REVISION_LATEST,
                            "#revisionCreated", Constants.ATTRIBUTE_REVISION_CREATED,
                            "#name", Constants.ATTRIBUTE_NAME))
                    .withExpressionAttributeValues(ImmutableMap.of(
                            ":revisionLatest", AV.of(revisionId),
                            ":revisionCreated", AV.of(now)));
            dynamoDB.updateItem(updateItemRequest);
        } catch (ConditionalCheckFailedException exp) {
            throw new VersionSetDoseNotExistsException(versionSetName, exp);
        }


        dynamoDB.putItem(new PutItemRequest(config.getVersionSetRevisionTable(), ImmutableMap.<String, AttributeValue>builder()
                .put(Constants.ATTRIBUTE_NAME, AV.of(sanitizeName(versionSetName)))
                .put(Constants.ATTRIBUTE_REVISION, AV.of(revisionId))
                .put(Constants.ATTRIBUTE_CREATED, AV.of(now))
                .put(Constants.ATTRIBUTE_PACKAGES, AV.of(
                        packages.stream().map(ArchipelagoBuiltPackage::toString).collect(Collectors.toList())))
                .build()));


        return revisionId;
    }

    // DynamoDB is case sensitive, the name we get may be entered by a human so we need to sanitize it
    protected String sanitizeName(final String n) {
        return n.trim().toLowerCase();
    }
}
