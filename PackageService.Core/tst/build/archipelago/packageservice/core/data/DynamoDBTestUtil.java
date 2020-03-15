package build.archipelago.packageservice.core.data;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

import java.util.Arrays;

public class DynamoDBTestUtil {

    public static final String PACKAGES_TABLE = "packages";
    public static final String PACKAGES_VERSIONS_TABLE = "packages-versions";
    public static final String PACKAGES_BUILDS_TABLE = "packages-builds";

    public static void createTables(AmazonDynamoDB amazonDynamoDB) {
        createPackagesTable(amazonDynamoDB);
        createPackagesVersionsTable(amazonDynamoDB);
        createPackagesBuildsTable(amazonDynamoDB);
    }

    private static void createPackagesTable(AmazonDynamoDB dynamoDB) {
        if (dynamoDB.listTables().getTableNames().stream().anyMatch(x -> x.equalsIgnoreCase(PACKAGES_TABLE))) {
            return;
        }
        CreateTableRequest request = new CreateTableRequest()
                .withTableName(PACKAGES_TABLE)
                .withAttributeDefinitions(
                        Arrays.asList(
                                new AttributeDefinition(DynamoDBKeys.PACKAGE_NAME, ScalarAttributeType.S)
                        ))
                .withKeySchema(Arrays.asList(
                        new KeySchemaElement(DynamoDBKeys.PACKAGE_NAME, KeyType.HASH)
                ))
                .withProvisionedThroughput(new ProvisionedThroughput(5l,5l));
        dynamoDB.createTable(request);
    }

    private static void createPackagesVersionsTable(AmazonDynamoDB dynamoDB) {
        if (dynamoDB.listTables().getTableNames().stream().anyMatch(x -> x.equalsIgnoreCase(PACKAGES_VERSIONS_TABLE))) {
            return;
        }
        CreateTableRequest request = new CreateTableRequest()
                .withTableName(PACKAGES_VERSIONS_TABLE)
                .withAttributeDefinitions(
                        Arrays.asList(
                                new AttributeDefinition(DynamoDBKeys.PACKAGE_NAME, ScalarAttributeType.S),
                                new AttributeDefinition(DynamoDBKeys.VERSION, ScalarAttributeType.S)
                        ))
                .withKeySchema(Arrays.asList(
                        new KeySchemaElement(DynamoDBKeys.PACKAGE_NAME, KeyType.HASH),
                        new KeySchemaElement(DynamoDBKeys.VERSION, KeyType.RANGE)
                ))
                .withProvisionedThroughput(new ProvisionedThroughput(5l,5l));
        dynamoDB.createTable(request);
    }

    private static void createPackagesBuildsTable(AmazonDynamoDB dynamoDB) {
        if (dynamoDB.listTables().getTableNames().stream().anyMatch(x -> x.equalsIgnoreCase(PACKAGES_BUILDS_TABLE))) {
            return;
        }
        CreateTableRequest request = new CreateTableRequest()
                .withTableName(PACKAGES_BUILDS_TABLE)
                .withAttributeDefinitions(
                        Arrays.asList(
                                new AttributeDefinition(DynamoDBKeys.NAME_VERSION, ScalarAttributeType.S),
                                new AttributeDefinition(DynamoDBKeys.HASH, ScalarAttributeType.S)
                        ))
                .withKeySchema(Arrays.asList(
                        new KeySchemaElement(DynamoDBKeys.NAME_VERSION, KeyType.HASH),
                        new KeySchemaElement(DynamoDBKeys.HASH, KeyType.RANGE)
                ))
                .withProvisionedThroughput(new ProvisionedThroughput(5l,5l));
        dynamoDB.createTable(request);
    }
}
