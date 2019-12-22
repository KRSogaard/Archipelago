package build.archipelago.kauai.core.data;

import build.archipelago.common.PackageNameVersion;
import build.archipelago.kauai.core.data.models.CreatePackageModel;
import build.archipelago.kauai.core.data.models.PackageBuiltDataModel;
import build.archipelago.kauai.core.data.models.PackageDataModel;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import lombok.extern.slf4j.Slf4j;
import utils.Constants;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class DynamoDBPackageData implements PackageData {

    private AmazonDynamoDB dynamoDB;
    private DynamoDBPackageConfig settings;

    public DynamoDBPackageData(AmazonDynamoDB dynamoDB,
                               DynamoDBPackageConfig settings) {
        this.dynamoDB = dynamoDB;
        this.settings = settings;
    }

    @Override
    public boolean buildExists(PackageNameVersion nameVersion, String hash) {
        return getBuild(nameVersion, hash).isPresent();
    }

    @Override
    public boolean buildExists(PackageNameVersion nameVersion) {
        return getBuild(nameVersion).isPresent();
    }

    @Override
    public boolean packageExists(String name) {
        return getPackage(name).isPresent();
    }

    @Override
    public Optional<PackageBuiltDataModel> getBuild(final PackageNameVersion nameVersion, String hash) {
        String latestHash = null;
        if (Constants.LATEST.equalsIgnoreCase(hash)) {
            Optional<String> newHash = getLatestHash(nameVersion);
            if (!newHash.isPresent()) {
                log.warn("No latest hash was found for {}", nameVersion.getConcatenated());
                return Optional.empty();
            }
            log.info("Found hash \"{}\" as latest for \"{}\"", nameVersion.getConcatenated(), newHash);
            latestHash = newHash.get();
        }
        final String packageHash = latestHash != null ? latestHash : hash;

        GetItemResult result = dynamoDB.getItem(new GetItemRequest(settings.getPackagesBuildsTableName(),
                new HashMap<String, AttributeValue>() {{
                    put(DynamoDBKeys.NAME, new AttributeValue(searchNameVersion(nameVersion)));
                    put(DynamoDBKeys.HASH, new AttributeValue(searchHash(packageHash)));
                }}));

        Map<String, AttributeValue> item = result.getItem();
        if (item == null) {
            return Optional.empty();
        }
        return Optional.of(parse(item));
    }

    @Override
    public Optional<PackageBuiltDataModel> getBuild(PackageNameVersion nameVersion) {
        return getBuild(nameVersion, Constants.LATEST);
    }

    @Override
    public Optional<PackageDataModel> getPackage(String name) {
        GetItemResult result = dynamoDB.getItem(settings.getPackagesTableName(), new HashMap<String, AttributeValue>() {{
            put(DynamoDBKeys.SEARCH_NAME, new AttributeValue(searchName(name)));
        }});
        Map<String, AttributeValue> item = result.getItem();
        if (item == null) {
            return Optional.empty();
        }
        return Optional.of(new PackageDataModel(
                item.get(DynamoDBKeys.NAME).getS(),
                item.get(DynamoDBKeys.DESCRIPTION).getS(),
                Instant.ofEpochMilli(Long.parseLong(item.get(DynamoDBKeys.CREATED).getS()))
        ));
    }

    @Override
    public void createBuild(PackageNameVersion nameVersion, String hash, String config) {
        dynamoDB.putItem(new PutItemRequest(settings.getPackagesBuildsTableName(),
                new HashMap<String, AttributeValue>() {{
                    put(DynamoDBKeys.NAME, new AttributeValue(searchNameVersion(nameVersion)));
                    put(DynamoDBKeys.HASH, new AttributeValue(searchHash(hash)));
                    put(DynamoDBKeys.CONFIG, new AttributeValue(config));
                    put(DynamoDBKeys.UPLOADED, new AttributeValue(String.valueOf(Instant.now().toEpochMilli())));
                }}));

        dynamoDB.putItem(new PutItemRequest(settings.getPackagesLatestTableName(),
                new HashMap<String, AttributeValue>() {{
                    put(DynamoDBKeys.NAME, new AttributeValue(searchNameVersion(nameVersion)));
                    put(DynamoDBKeys.HASH, new AttributeValue(searchHash(hash)));
                }}));
    }

    @Override
    public void createPackage(CreatePackageModel model) {
        model.validate();

        dynamoDB.putItem(new PutItemRequest(settings.getPackagesTableName(),
                new HashMap<String, AttributeValue>() {{
                    put(DynamoDBKeys.SEARCH_NAME, new AttributeValue(searchName(model.getName())));
                    put(DynamoDBKeys.NAME, new AttributeValue(model.getName()));
                    put(DynamoDBKeys.DESCRIPTION, new AttributeValue(model.getDescription()));
                    put(DynamoDBKeys.CREATED, new AttributeValue(String.valueOf(Instant.now().toEpochMilli())));
                }}));
    }

    public Optional<String> getLatestHash(PackageNameVersion nameVersion) {
        GetItemResult result = dynamoDB.getItem(new GetItemRequest(settings.getPackagesLatestTableName(),
                new HashMap<String, AttributeValue>() {{
                    put(DynamoDBKeys.NAME, new AttributeValue(nameVersion.getConcatenated()));
                }}));
        Map<String, AttributeValue> item = result.getItem();
        if (item == null) {
            return Optional.empty();
        }
        return Optional.of(item.get(DynamoDBKeys.HASH).getN());
    }

    private PackageBuiltDataModel parse(Map<String, AttributeValue> item) {
        return new PackageBuiltDataModel(
                item.get(DynamoDBKeys.NAME).getS(),
                item.get(DynamoDBKeys.HASH).getS(),
                Instant.ofEpochMilli(Long.parseLong(item.get(DynamoDBKeys.UPLOADED).getS())),
                item.get(DynamoDBKeys.CONFIG).getS()
        );
    }

    private static String searchNameVersion(PackageNameVersion nameVersion) {
        return searchName(nameVersion.getName()) + "-" + searchVersion(nameVersion.getVersion());
    }
    private static String searchName(String name) {
        return name.toLowerCase();
    }
    private static String searchVersion(String version) {
        return version.toLowerCase();
    }
    private static String searchHash(String hash) {
        return hash.toLowerCase();
    }
}
