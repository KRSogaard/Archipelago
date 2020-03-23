package build.archipelago.versionsetservice.client.rest;

import build.archipelago.versionsetservice.client.VersionServiceClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RestVersionSetServiceClientTest {

    private VersionServiceClient client;

    @Before
    public void setUp() throws Exception {
        client = new RestVersionSetServiceClient("http://localhost:8081");
    }

    @Test
    public void testEmptyTest() {
        Assert.assertTrue(true);
    }

//    @Test
//    public void testCreateVersionSet() throws VersionSetExistsException, VersionSetDoseNotExistsException,
//            PackageNotFoundException {
//        String name = "TestVersionSet-" + UUID.randomUUID().toString().split("-")[0];
//        client.createVersionSet(CreateVersionSetRequest.builder()
//                .name(name)
//                .targets(ImmutableList.of(new ArchipelagoPackage("testpackage", "1.0")))
//                .build());
//    }
//
//    @Test
//    public void testCreateVersionSetRevision() throws MissingTargetPackageException, VersionSetDoseNotExistsException, PackageNotFoundException {
//        String name = "testversionset-0c5a5ff9";
//        String revision = client.createVersionRevision(name,
//                ImmutableList.of(new ArchipelagoBuiltPackage("testpackage", "1.0", "9109e71e")));
//
//        Assert.assertNotNull(revision);
//    }
//
//    @Test
//    public void testGetVersionSet() throws VersionSetDoseNotExistsException {
//        String name = "testversionset-4f1b92d2";
//        VersionSet vs = client.getVersionSet(name);
//
//        Assert.assertNotNull(vs);
//    }
//
//    @Test
//    public void testGetVersionSetRevision() throws VersionSetDoseNotExistsException {
//        String name = "testversionset-0c5a5ff9";
//        String revision = "3494d4f0";
//
//        var revisionObj = client.getVersionSetPackages(name, revision);
//        Assert.assertNotNull(revisionObj);
//    }
}