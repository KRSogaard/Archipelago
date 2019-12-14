package build.archipelago.kauai.core.data;

import build.archipelago.common.PackageNameVersion;
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
    private String packagesTableName;
    private String packagesLatestTableName;

    public DynamoDBPackageData(AmazonDynamoDB dynamoDB,
                               String packagesTableName,
                               String packagesLatestTableName) {
        this.dynamoDB = dynamoDB;
        this.packagesTableName = packagesTableName;
        this.packagesLatestTableName = packagesLatestTableName;

        // ZonedDateTime.now(ZoneOffset.UTC).toString();
    }

    @Override
    public boolean exists(PackageNameVersion nameVersion, String hash) {
        return get(nameVersion, hash).isPresent();
    }

    @Override
    public boolean exists(PackageNameVersion nameVersion) {
        return get(nameVersion).isPresent();
    }

    @Override
    public Optional<PackageDataModel> get(final PackageNameVersion nameVersion, String hash) {
        String latestHash = null;
        if (Constants.LATEST.equalsIgnoreCase(hash)) {
            Optional<String> newHash = getLatestHash(nameVersion);
            if (!newHash.isPresent()) {
                log.warn("No latest hash was found for {}", nameVersion.getNameVersion());
                return Optional.empty();
            }
            log.info("Found hash \"{}\" as latest for \"{}\"", nameVersion.getNameVersion(), newHash);
            latestHash = newHash.get();
        }
        final String packageHash = latestHash != null ? latestHash : hash;

        GetItemResult result = dynamoDB.getItem(new GetItemRequest(packagesTableName,
                new HashMap<String, AttributeValue>() {{
                    put(DynamoDBKeys.NAME, new AttributeValue(nameVersion.getNameVersion()));
                    put(DynamoDBKeys.HASH, new AttributeValue(packageHash));
                }}));

        Map<String, AttributeValue> item = result.getItem();
        if (item == null) {
            return Optional.empty();
        }
        return Optional.of(parse(item));
    }

    @Override
    public Optional<PackageDataModel> get(PackageNameVersion nameVersion) {
        return get(nameVersion, Constants.LATEST);
    }

    @Override
    public void create(PackageNameVersion nameVersion, String hash, String config) {
        dynamoDB.putItem(new PutItemRequest(packagesTableName,
                new HashMap<String, AttributeValue>() {{
                    put(DynamoDBKeys.NAME, new AttributeValue(nameVersion.getNameVersion()));
                    put(DynamoDBKeys.HASH, new AttributeValue(hash));
                    put(DynamoDBKeys.CONFIG, new AttributeValue(config));
                    put(DynamoDBKeys.UPLOADED, new AttributeValue(String.valueOf(Instant.now().toEpochMilli())));
                }}));

        dynamoDB.putItem(new PutItemRequest(packagesLatestTableName,
                new HashMap<String, AttributeValue>() {{
                    put(DynamoDBKeys.NAME, new AttributeValue(nameVersion.getNameVersion()));
                    put(DynamoDBKeys.HASH, new AttributeValue(hash));
                }}));
    }

    private Optional<String> getLatestHash(PackageNameVersion nameVersion) {
        GetItemResult result = dynamoDB.getItem(new GetItemRequest(packagesLatestTableName,
                new HashMap<String, AttributeValue>() {{
                    put(DynamoDBKeys.NAME, new AttributeValue(nameVersion.getNameVersion()));
                }}));
        Map<String, AttributeValue> item = result.getItem();
        if (item == null) {
            return Optional.empty();
        }
        return Optional.of(item.get(DynamoDBKeys.HASH).getN());
    }

    private PackageDataModel parse(Map<String, AttributeValue> item) {
        return new PackageDataModel(
                item.get(DynamoDBKeys.NAME).getS(),
                item.get(DynamoDBKeys.HASH).getS(),
                Instant.ofEpochMilli(Long.parseLong(item.get(DynamoDBKeys.UPLOADED).getS())),
                item.get(DynamoDBKeys.CONFIG).getS()
        );
    }
}
