package build.archipelago.packageservice.client;

import org.junit.Assert;

public class RestPackageServiceClientTest {

    private PackageServiceClient client;

    public void testEmptyTest() {
        Assert.assertTrue(true);
    }
//
//    @Before
//    public void setUp() {
//        client = new RestPackageServiceClient("http://localhost:8080");
//    }
//
//    @Test
//    public void testCreate() throws PackageExistsException {
//        String UUID = java.util.UUID.randomUUID().toString().split("-")[0];
//
//        client.createPackage(CreatePackageRequest.builder()
//                .name("KasperTestPackage-" + UUID)
//                .description("This is a description")
//                .build());
//    }
//
//    @Test
//    public void testGet() throws PackageExistsException, PackageNotFoundException {
//        String UUID = java.util.UUID.randomUUID().toString().split("-")[0];
//        String name = "KasperTestPackage-" + UUID;
//
////        client.createPackage(CreatePackageRequest.builder()
////                .name(name)
////                .description("This is a description")
////                .build());
//
//        GetPackageResponse response = client.getPackage("testpackage");
//        Assert.assertEquals("testpackage", response.getName());
//    }
//
//    @Test
//    public void testGetBuilds() throws PackageNotFoundException {
//        var response = client.getPackageBuilds(new ArchipelagoPackage("TestPackage", "1.0"));
//        Assert.assertTrue(response.getBuilds().size() > 0);
//    }
//
//    @Test
//    public void testGetBuild() throws PackageNotFoundException {
//        var res = client.getPackageBuild(new ArchipelagoBuiltPackage("TestPackage", "1.0", "9109e71e"));
//        Assert.assertNotNull(res);
//    }
//
//    @Test
//    public void testVerifyPackages() {
//        var res = client.verifyPackagesExists(List.of(
//                new ArchipelagoPackage("TestPackage", "1.0"),
//                new ArchipelagoPackage("NotExistsTestPackage", "1.0")
//        ));
//        Assert.assertNotNull(res);
//    }
//
//    @Test
//    public void testVerifyBuilds() {
//        var res = client.verifyBuildsExists(List.of(
//                new ArchipelagoBuiltPackage("TestPackage", "1.0", "9109e71e"),
//                new ArchipelagoBuiltPackage("NotExistsTestPackage", "1.0", "blah")
//        ));
//        Assert.assertNotNull(res);
//    }
//
//    @Test
//    public void testUploadBuild() throws PackageNotFoundException {
//        var res = client.uploadBuiltArtifact(UploadPackageRequest.builder()
//                .pkg(new ArchipelagoPackage("TestPackage", "1.1"))
//                .config("this is a nice config").build(), Path.of("C:\\Users\\aoyin\\Downloads\\testZip.zip"));
//        Assert.assertFalse(Strings.isNullOrEmpty(res));
//    }
//
//    @Test
//    public void testDownloadBuildArtifact() throws PackageNotFoundException, IOException {
//        Path base = Paths.get("C:\\Users\\aoyin\\Downloads\\downloadTest");
//        var res = client.getBuildArtifact(
//                new ArchipelagoBuiltPackage("TestPackage", "1.1", "7342ab62"),
//                base);
//        Assert.assertNotNull(res);
//        Assert.assertTrue(Files.exists(res));
//    }
}