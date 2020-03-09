package build.archipelago.versionsetservice.core.services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

import java.util.Arrays;

public class DynamoDBTestUtil {
    public static final String VERSION_SET_TABLE = "version-sets";
    public static final String VERSION_SET_REVISION_TABLE = "version-set-revisions";

    public static void createTables(AmazonDynamoDB dynamoDB) {
        createVersionSetTable(dynamoDB);
        createVersionSetRevisions(dynamoDB);
    }

    private static void createVersionSetRevisions(AmazonDynamoDB dynamoDB) {
        if (dynamoDB.listTables().getTableNames().stream().anyMatch(x -> x.equalsIgnoreCase(VERSION_SET_REVISION_TABLE))) {
            return;
        }
        CreateTableRequest request = new CreateTableRequest()
                .withTableName(VERSION_SET_REVISION_TABLE)
                .withAttributeDefinitions(
                        Arrays.asList(
                                new AttributeDefinition(Constants.ATTRIBUTE_NAME, ScalarAttributeType.S),
                                new AttributeDefinition(Constants.ATTRIBUTE_REVISION, ScalarAttributeType.S)
                                ))
                .withKeySchema(Arrays.asList(
                        new KeySchemaElement(Constants.ATTRIBUTE_NAME, KeyType.HASH),
                        new KeySchemaElement(Constants.ATTRIBUTE_REVISION, KeyType.RANGE)
                ))
                .withProvisionedThroughput(new ProvisionedThroughput(5l,5l));
        dynamoDB.createTable(request);
    }

    private static void createVersionSetTable(AmazonDynamoDB dynamoDB) {
        if (dynamoDB.listTables().getTableNames().stream().anyMatch(x -> x.equalsIgnoreCase(VERSION_SET_TABLE))) {
            return;
        }
        CreateTableRequest request = new CreateTableRequest()
                .withTableName(VERSION_SET_TABLE)
                .withAttributeDefinitions(
                        Arrays.asList(new AttributeDefinition(Constants.ATTRIBUTE_NAME, ScalarAttributeType.S)))
                .withKeySchema(Arrays.asList(new KeySchemaElement(Constants.ATTRIBUTE_NAME, KeyType.HASH)))
                .withProvisionedThroughput(new ProvisionedThroughput(5l,5l));
        dynamoDB.createTable(request);
    }
}
