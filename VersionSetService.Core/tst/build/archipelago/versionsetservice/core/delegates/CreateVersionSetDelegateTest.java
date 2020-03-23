package build.archipelago.versionsetservice.core.delegates;

import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.packageservice.client.PackageServiceClient;
import build.archipelago.packageservice.client.models.PackageVerificationResult;
import build.archipelago.common.exceptions.PackageNotFoundException;
import build.archipelago.common.exceptions.VersionSetDoseNotExistsException;
import build.archipelago.common.exceptions.VersionSetExistsException;
import build.archipelago.versionsetservice.core.models.Revision;
import build.archipelago.versionsetservice.core.models.VersionSet;
import build.archipelago.versionsetservice.core.services.VersionSetService;
import build.archipelago.versionsetservice.core.utils.RevisionUtil;
import org.junit.Before;
import org.junit.Test;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateVersionSetDelegateTest {

    private CreateVersionSetDelegate delegate;
    private VersionSetService versionSetService;
    private PackageServiceClient packageServiceClient;

    private String testVSName;
    private String parentVSName;
    private static ArchipelagoPackage pA = ArchipelagoPackage.parse("TestPackageA-1.0");
    private static ArchipelagoPackage pB = ArchipelagoPackage.parse("TestPackageB-1.0");
    private static ArchipelagoPackage pC = ArchipelagoPackage.parse("TestPackageC-1.0");

    @Before
    public void setUp() {
        testVSName = "TestVS-" + RevisionUtil.getRandomRevisionId();
        parentVSName = "parent-" + RevisionUtil.getRandomRevisionId();

        versionSetService = mock(VersionSetService.class);
        packageServiceClient = mock(PackageServiceClient.class);
        delegate = new CreateVersionSetDelegate(versionSetService, packageServiceClient);
    }

    @Test
    public void testCreateValidVersionSet() throws VersionSetExistsException,
            VersionSetDoseNotExistsException, PackageNotFoundException {
        when(packageServiceClient.verifyPackagesExists(any())).thenReturn(
                PackageVerificationResult.<ArchipelagoPackage>builder().missingPackages(new ArrayList<>()).build());
        when(versionSetService.get(eq(parentVSName))).thenReturn(createVS(parentVSName, List.of(pA)));

        delegate.create(testVSName, List.of(pA, pB), Optional.of(parentVSName));
    }

    @Test
    public void testCreateValidVersionSetWithoutParent() throws VersionSetExistsException,
            VersionSetDoseNotExistsException, PackageNotFoundException {
        when(packageServiceClient.verifyPackagesExists(any())).thenReturn(
                PackageVerificationResult.<ArchipelagoPackage>builder().missingPackages(new ArrayList<>()).build());
        delegate.create(testVSName, List.of(pA, pB), Optional.empty());
    }

    @Test(expected = VersionSetExistsException.class)
    public void testCreateVersionSetWithNameThatAlreadyExistsShouldFail() throws VersionSetExistsException,
            VersionSetDoseNotExistsException, PackageNotFoundException {
        when(packageServiceClient.verifyPackagesExists(any())).thenReturn(
                PackageVerificationResult.<ArchipelagoPackage>builder().missingPackages(new ArrayList<>()).build());
        when(versionSetService.get(eq(testVSName))).thenReturn(createVS(testVSName, List.of(pA, pB)));
        delegate.create(testVSName, List.of(pA, pB), Optional.empty());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateVersionWithoutTargetsShouldFail() throws VersionSetExistsException,
            VersionSetDoseNotExistsException, PackageNotFoundException  {
        delegate.create(testVSName, new ArrayList<>(), Optional.empty());
    }

    @Test(expected = PackageNotFoundException.class)
    public void testCreateVersionWithTargetThatDoseNotExistsShouldFail() throws VersionSetExistsException,
            VersionSetDoseNotExistsException, PackageNotFoundException  {
        String packageName = "DoseNotExists-1.0";
        when(packageServiceClient.verifyPackagesExists(any())).thenReturn(
                PackageVerificationResult.<ArchipelagoPackage>builder().missingPackages(
                        List.of(ArchipelagoPackage.parse(packageName))).build());
        when(versionSetService.get(eq(testVSName))).thenReturn(null);

        delegate.create(testVSName, List.of(ArchipelagoPackage.parse(packageName)), Optional.empty());
    }


    private VersionSet createVS(String vsName, List<ArchipelagoPackage> targets) {
        Instant created = Instant.now();
        String vsParentName = "parent-master";
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