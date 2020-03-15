package build.archipelago.packageservice.core.data;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

public class DynamoDBPackageDataTest {

    private PackageData packageData;
    private AmazonDynamoDB amazonDynamoDB;
//
//    private static final String PACKAGE_NAME = "PackageA1";
//    private static final String PACKAGE_VERSION = "PackageA1";
//    private static final String PACKAGE_BUILD = UUID.randomUUID().toString();
//    private static final String PACKAGE_DESC = UUID.randomUUID().toString().repeat(100);
//
//    @Before
//    public void setUp() throws Exception {
//        amazonDynamoDB = DynamoDBEmbedded.create().amazonDynamoDB();
//        DynamoDBTestUtil.createTables(amazonDynamoDB);
//
//        packageData = new DynamoDBPackageData(amazonDynamoDB,
//                DynamoDBPackageConfig.builder()
//                        .packagesTableName(DynamoDBTestUtil.PACKAGES_TABLE)
//                        .packagesVersionsTableName(DynamoDBTestUtil.PACKAGES_VERSIONS_TABLE)
//                        .packagesBuildsTableName(DynamoDBTestUtil.PACKAGES_BUILDS_TABLE)
//                        .build());
//    }
//
//    @Test
//    public void testCreateAndGetValidPackage() throws PackageExistsException, PackageNotFoundException {
//        Instant start = Instant.now();
//        packageData.createPackage(CreatePackageModel.builder()
//                .name(PACKAGE_NAME)
//                .description(PACKAGE_DESC)
//                .build());
//        Instant end = Instant.now();
//
//        var pkg = packageData.getPackageDetails(PACKAGE_NAME);
//
//        Assert.assertEquals(PACKAGE_NAME, pkg.getName());
//        Assert.assertEquals(PACKAGE_DESC, pkg.getDescription());
//        Assert.assertTrue(start.toEpochMilli() <= pkg.getCreated().toEpochMilli());
//        Assert.assertTrue(end.toEpochMilli() >= pkg.getCreated().toEpochMilli());
//        Assert.assertEquals(0, pkg.getVersions().size());
//    }
}