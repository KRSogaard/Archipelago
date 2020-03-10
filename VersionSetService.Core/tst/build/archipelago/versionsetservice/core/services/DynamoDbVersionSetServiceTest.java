package build.archipelago.versionsetservice.core.services;

import build.archipelago.common.ArchipelagoBuiltPackage;
import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.versionsetservice.core.exceptions.VersionSetDoseNotExistsException;
import build.archipelago.versionsetservice.core.utils.RevisionUtil;
import build.archipelago.versionsetservice.core.utils.TestConstants;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class DynamoDbVersionSetServiceTest {
    private AmazonDynamoDB amazonDynamoDB;
    private DynamoDbVersionSetService vsService;
    private String prefix;

    @Before
    public void setUp() {
        amazonDynamoDB = DynamoDBEmbedded.create().amazonDynamoDB();
        DynamoDBTestUtil.createTables(amazonDynamoDB);
        prefix = RevisionUtil.getRandomRevisionId();

        vsService = new DynamoDbVersionSetService(amazonDynamoDB, DynamoDbVersionSetServiceConfig.builder()
                .versionSetTable(DynamoDBTestUtil.VERSION_SET_TABLE)
                .versionSetRevisionTable(DynamoDBTestUtil.VERSION_SET_REVISION_TABLE)
                .build());
    }

    @Test
    public void testGetVersionSetThatDoNotExists() throws VersionSetDoseNotExistsException {
        String name = "kasperTest-" + prefix;
        Assert.assertNull(vsService.get(name));
    }

    @Test
    public void testCreateVersionSetAndGet() throws VersionSetDoseNotExistsException {
        Instant start = Instant.now();
        String name = "kasperTest-" + prefix;
        List<ArchipelagoPackage> targets = ImmutableList.of(TestConstants.pkgA);
        String parent = "master";
        vsService.create(name, targets, Optional.of(parent));
        Instant end = Instant.now();

        var vs = vsService.get(name);
        Assert.assertNotNull(vs);
        Assert.assertTrue(vs.getParent().isPresent());
        Assert.assertEquals(parent, vs.getParent().get());
        Assert.assertEquals(name, vs.getName());
        assertDateRange(start, end, vs.getCreated());
        Assert.assertTrue(vs.getLatestRevision().isEmpty());
        Assert.assertTrue(vs.getLatestRevisionCreated().isEmpty());
    }

    @Test
    public void testCreateVSRevision() throws VersionSetDoseNotExistsException {
        Instant start = Instant.now();
        String name = "kasperTest-" + prefix;

        String parent = "master";
        vsService.create(name, ImmutableList.of(TestConstants.pkgA), Optional.of(parent));
        String revision = vsService.createRevision(name, ImmutableList.of(TestConstants.pkgABuild,
                TestConstants.pkgBBuild));
        Instant end = Instant.now();

        var vs = vsService.get(name);
        Assert.assertNotNull(vs);
        Assert.assertTrue(vs.getParent().isPresent());
        Assert.assertEquals(parent, vs.getParent().get());
        Assert.assertEquals(name, vs.getName());
        assertDateRange(start, end, vs.getCreated());
        Assert.assertTrue(vs.getLatestRevision().isPresent());
        Assert.assertEquals(revision, vs.getLatestRevision().get());
        Assert.assertTrue(vs.getLatestRevisionCreated().isPresent());
        assertDateRange(start, end, vs.getLatestRevisionCreated().get());
    }

    @Test
    public void testCreateMultipleVSRevision() throws VersionSetDoseNotExistsException {
        Instant start = Instant.now();
        String name = "kasperTest-" + prefix;
        String parent = "master";
        vsService.create(name, ImmutableList.of(TestConstants.pkgA), Optional.of(parent));
        vsService.createRevision(name, ImmutableList.of(
                TestConstants.pkgABuild,
                TestConstants.pkgBBuild));
        vsService.createRevision(name, ImmutableList.of(
                TestConstants.pkgABuild,
                TestConstants.pkgBBuild));
        String revision = vsService.createRevision(name, ImmutableList.of(
                TestConstants.pkgABuild,
                TestConstants.pkgBBuild));
        Instant end = Instant.now();

        var vs = vsService.get(name);
        Assert.assertNotNull(vs);
        Assert.assertTrue(vs.getParent().isPresent());
        Assert.assertEquals(parent, vs.getParent().get());
        Assert.assertEquals(name, vs.getName());
        assertDateRange(start, end, vs.getCreated());
        Assert.assertTrue(vs.getLatestRevision().isPresent());
        Assert.assertEquals(revision, vs.getLatestRevision().get());
        Assert.assertTrue(vs.getLatestRevisionCreated().isPresent());
        assertDateRange(start, end, vs.getLatestRevisionCreated().get());
    }

    @Test
    public void testGetRevisions() throws VersionSetDoseNotExistsException {
        String name = "kasperTest-" + prefix;
        String parent = "master";
        vsService.create(name, ImmutableList.of(TestConstants.pkgA), Optional.of(parent));
        String revision1 = vsService.createRevision(name, ImmutableList.of(
                TestConstants.pkgABuild,
                TestConstants.pkgBBuild));
        String revision2 = vsService.createRevision(name, ImmutableList.of(
                TestConstants.pkgABuild,
                TestConstants.pkgBBuild));
        String revision3 = vsService.createRevision(name, ImmutableList.of(
                TestConstants.pkgABuild,
                TestConstants.pkgBBuild));

        var vs = vsService.get(name);
        Assert.assertNotNull(vs);
        Assert.assertEquals(name, vs.getName());
        Assert.assertEquals(3, vs.getRevisions().size());
        Assert.assertTrue(vs.getRevisions().stream().anyMatch(x -> revision1.equals(x.getRevisionId())));
        Assert.assertTrue(vs.getRevisions().stream().anyMatch(x -> revision2.equals(x.getRevisionId())));
        Assert.assertTrue(vs.getRevisions().stream().anyMatch(x -> revision3.equals(x.getRevisionId())));
    }

    @Test(expected = VersionSetDoseNotExistsException.class)
    public void testCreateRevisionForNonExsistingVS() throws VersionSetDoseNotExistsException {
        String name = "kasperTest-" + prefix;
        String packageA = "TestPackageA";
        String packageB = "TestPackageB";

        vsService.createRevision(name, ImmutableList.of(
                TestConstants.pkgABuild,
                TestConstants.pkgBBuild));
    }

    @Test
    public void testGetPackagesForVersionSetRevision() throws VersionSetDoseNotExistsException {
        String name = "kasperTest-" + prefix;
        String packageA = "TestPackageA"; ;
        String packageB = "TestPackageB";
        String parent = "master";
        vsService.create(name, ImmutableList.of(
                TestConstants.pkgA), Optional.of(parent));
        String revision1 = vsService.createRevision(name, ImmutableList.of(
                new ArchipelagoBuiltPackage(packageA, "1.0", "1"),
                new ArchipelagoBuiltPackage(packageB, "1.0", "1")));

        var vs = vsService.get(name);
        Assert.assertNotNull(vs);
        var revision = vsService.getRevision(name, revision1);
        Assert.assertNotNull(revision);
        var pkgs = revision.getPackages();
        Assert.assertNotNull(pkgs);
        Assert.assertEquals(2, pkgs.size());
        Assert.assertTrue(pkgs.stream().anyMatch(x -> packageA.equals(x.getName()) &&
                "1.0".equals(x.getVersion()) && "1".equals(x.getHash())));
        Assert.assertTrue(pkgs.stream().anyMatch(x -> packageB.equals(x.getName()) &&
                "1.0".equals(x.getVersion()) && "1".equals(x.getHash())));
    }

    @Test
    public void testGetPackagesForVersionSetRevisionEnsureRightVersion() throws VersionSetDoseNotExistsException {
        String name = "kasperTest-" + prefix;
        String parent = "master";
        vsService.create(name, ImmutableList.of(TestConstants.pkgA), Optional.of(parent));
        vsService.createRevision(name, ImmutableList.of(
                new ArchipelagoBuiltPackage(TestConstants.pkgA.getName(), "1.0", "1"),
                new ArchipelagoBuiltPackage(TestConstants.pkgB.getName(), "1.0", "1")));
        String revision2 = vsService.createRevision(name, ImmutableList.of(
                new ArchipelagoBuiltPackage(TestConstants.pkgA.getName(), "1.1", "2"),
                new ArchipelagoBuiltPackage(TestConstants.pkgB.getName(), "1.1", "2")));

        var vs = vsService.get(name);
        Assert.assertNotNull(vs);

        Assert.assertTrue(vs.getLatestRevision().isPresent());
        Assert.assertEquals(revision2, vs.getLatestRevision().get());

        var revision = vsService.getRevision(name, vs.getLatestRevision().get());
        Assert.assertNotNull(revision);
        var pkgs = revision.getPackages();
        Assert.assertNotNull(pkgs);
        Assert.assertEquals(2, pkgs.size());
        Assert.assertTrue(pkgs.stream().anyMatch(x -> TestConstants.pkgA.getName().equals(x.getName()) &&
                "1.1".equals(x.getVersion()) && "2".equals(x.getHash())));
        Assert.assertTrue(pkgs.stream().anyMatch(x -> TestConstants.pkgB.getName().equals(x.getName()) &&
                "1.1".equals(x.getVersion()) && "2".equals(x.getHash())));
    }

    @Test
    public void testGetPackagesForVersionSetRevisionGetEarlierVersion() throws VersionSetDoseNotExistsException {
        String name = "kasperTest-" + prefix;
        String parent = "master";
        vsService.create(name, ImmutableList.of(
                TestConstants.pkgA), Optional.of(parent));
        String revision1 = vsService.createRevision(name, ImmutableList.of(
                new ArchipelagoBuiltPackage(TestConstants.pkgA.getName(), "1.0", "1"),
                new ArchipelagoBuiltPackage(TestConstants.pkgB.getName(), "1.0", "1")));
        String revision2 = vsService.createRevision(name, ImmutableList.of(
                new ArchipelagoBuiltPackage(TestConstants.pkgA.getName(), "1.1", "2"),
                new ArchipelagoBuiltPackage(TestConstants.pkgB.getName(), "1.1", "2")));

        var vs = vsService.get(name);
        Assert.assertNotNull(vs);

        Assert.assertTrue(vs.getLatestRevision().isPresent());
        var revision = vsService.getRevision(name, revision1);
        Assert.assertNotNull(revision);
        var pkgs = revision.getPackages();
        Assert.assertNotNull(pkgs);
        Assert.assertEquals(2, pkgs.size());
        Assert.assertTrue(pkgs.stream().anyMatch(x -> TestConstants.pkgA.getName().equals(x.getName()) &&
                "1.0".equals(x.getVersion()) && "1".equals(x.getHash())));
        Assert.assertTrue(pkgs.stream().anyMatch(x -> TestConstants.pkgB.getName().equals(x.getName()) &&
                "1.0".equals(x.getVersion()) && "1".equals(x.getHash())));
    }

    @Test(expected = VersionSetDoseNotExistsException.class)
    public void testGetPackagesForVersionSetThatDoseNotExists() throws VersionSetDoseNotExistsException {
        String name = "kasperTest-" + prefix;
        vsService.getRevision(name, "1234");
    }

    @Test(expected = VersionSetDoseNotExistsException.class)
    public void testGetPackagesForVersionSetRevisionThatDoseNotExists() throws VersionSetDoseNotExistsException {
        String name = "kasperTest/" + prefix;
        String parent = "master";
        vsService.create(name, ImmutableList.of(
                TestConstants.pkgA), Optional.of(parent));
        vsService.createRevision(name, ImmutableList.of(
                new ArchipelagoBuiltPackage(TestConstants.pkgA.getName(), "1.0", "1"),
                new ArchipelagoBuiltPackage(TestConstants.pkgB.getName(), "1.0", "1")));

        vsService.getRevision(name, getRandomHash());
    }

    private String getRandomHash() {
        return RevisionUtil.getRandomRevisionId();
    }

    private void assertDateRange(Instant start, Instant end, Instant date) {
        Long dateMS = date.toEpochMilli();
        Long startMS = start.toEpochMilli();
        Long endMS = end.toEpochMilli();

        if (dateMS < startMS) {
            throw new AssertionError(String.format("%s was before %s, it was expected to be equal or after",
                    date.toString(), start.toString()));
        }
        if (dateMS > endMS) {
            throw new AssertionError(String.format("%s was after %s, it was expected to be equal or after",
                    date.toString(), end.toString()));
        }
    }
}
