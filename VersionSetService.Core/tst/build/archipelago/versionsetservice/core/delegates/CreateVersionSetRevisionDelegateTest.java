package build.archipelago.versionsetservice.core.delegates;

import build.archipelago.common.ArchipelagoBuiltPackage;
import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.packageservice.client.PackageServiceClient;
import build.archipelago.packageservice.client.models.PackageVerificationResult;
import build.archipelago.packageservice.common.exceptions.PackageNotFoundException;
import build.archipelago.versionsetservice.core.exceptions.MissingTargetPackageException;
import build.archipelago.versionsetservice.core.exceptions.VersionSetDoseNotExistsException;
import build.archipelago.versionsetservice.core.models.Revision;
import build.archipelago.versionsetservice.core.models.VersionSet;
import build.archipelago.versionsetservice.core.services.VersionSetService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateVersionSetRevisionDelegateTest {

    private CreateVersionSetRevisionDelegate delegate;
    private VersionSetService versionSetService;
    private PackageServiceClient packageServiceClient;

    private static ArchipelagoBuiltPackage pbA = ArchipelagoBuiltPackage.parse("TestPackageA-1.0:abc");
    private static ArchipelagoBuiltPackage pbB = ArchipelagoBuiltPackage.parse("TestPackageB-1.0:fgh");
    private static ArchipelagoBuiltPackage pbC = ArchipelagoBuiltPackage.parse("TestPackageC-1.0:gt3");
    private static ArchipelagoPackage pA = ArchipelagoPackage.parse("TestPackageA-1.0");
    private static ArchipelagoPackage pB = ArchipelagoPackage.parse("TestPackageB-1.0");
    private static ArchipelagoPackage pC = ArchipelagoPackage.parse("TestPackageC-1.0");

    private String testVSName;
    private String testRevisionId;

    @Before
    public void setUp() throws VersionSetDoseNotExistsException {
        versionSetService = mock(VersionSetService.class);
        packageServiceClient = mock(PackageServiceClient.class);
        delegate = new CreateVersionSetRevisionDelegate(versionSetService, packageServiceClient);

        testVSName = UUID.randomUUID().toString().split("-", 2)[0];
        List<ArchipelagoPackage> targets = List.of(pA, pB);
        VersionSet vs = createVS(testVSName, targets);
        when(versionSetService.get(eq(testVSName))).thenReturn(vs);
    }

    @Test
    public void testCreateValidRevision() throws VersionSetDoseNotExistsException, MissingTargetPackageException,
            PackageNotFoundException {
        String vsName = "TestVS\\master";
        String revisionId = "12345";
        List<ArchipelagoPackage> targets = List.of(pA, pB);

        VersionSet vs = createVS(vsName, targets);

        when(versionSetService.get(eq(vsName))).thenReturn(vs);
        when(versionSetService.createRevision(eq(vsName), any())).thenReturn(revisionId);
        when(packageServiceClient.verifyBuildsExists(any())).thenReturn(
                PackageVerificationResult.<ArchipelagoBuiltPackage>builder().missingPackages(new ArrayList<>()).build());

        String result = delegate.createRevision(vsName, List.of(pbA, pbB, pbC));
        Assert.assertNotNull(result);
        Assert.assertEquals(revisionId, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateRevisionWithEmptyVSName() throws MissingTargetPackageException,
            VersionSetDoseNotExistsException, PackageNotFoundException {
        delegate.createRevision("", List.of(pbA, pbB));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateRevisionWithNullVSName() throws MissingTargetPackageException,
            VersionSetDoseNotExistsException, PackageNotFoundException {
        delegate.createRevision(null, List.of(pbA, pbB));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateRevisionWithoutPackages() throws MissingTargetPackageException,
            VersionSetDoseNotExistsException, PackageNotFoundException {
        delegate.createRevision(testVSName, new ArrayList<>());
    }

    @Test(expected = MissingTargetPackageException.class)
    public void testCreateRevisionWithMissingTarget() throws MissingTargetPackageException,
            VersionSetDoseNotExistsException, PackageNotFoundException {
        delegate.createRevision(testVSName, List.of(pbC));
    }

    @Test(expected = PackageNotFoundException.class)
    public void testCreateRevisionWithPackageThatDoseNotExits() throws MissingTargetPackageException,
            VersionSetDoseNotExistsException, PackageNotFoundException {
        when(packageServiceClient.verifyBuildsExists(any())).thenReturn(
                PackageVerificationResult.<ArchipelagoBuiltPackage>builder().missingPackages(List.of(pbC)).build());
        delegate.createRevision(testVSName, List.of(pbA, pbB, pbC));

    }

    private VersionSet createVS(String vsName, List<ArchipelagoPackage> targets) {
        Instant created = Instant.now();
        String vsParentName = "parent/master";
        String revisionId = "123";
        Instant revisionDate = Instant.now();
        Revision revisionA = Revision.builder()
                .revisionId(revisionId)
                .created(revisionDate)
                .build();
        return VersionSet.builder()
                .name(vsName)
                .created(created)
                .parent(Optional.of(vsParentName))
                .targets(targets)
                .revisions(List.of(revisionA))
                .latestRevisionCreated(Optional.of(revisionDate))
                .latestRevision(Optional.of(revisionId))
                .build();
    }
}