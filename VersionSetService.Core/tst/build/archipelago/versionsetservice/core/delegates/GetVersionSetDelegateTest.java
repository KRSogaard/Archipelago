package build.archipelago.versionsetservice.core.delegates;

import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.common.exceptions.VersionSetDoseNotExistsException;
import build.archipelago.versionsetservice.core.models.Revision;
import build.archipelago.versionsetservice.core.models.VersionSet;
import build.archipelago.versionsetservice.core.services.VersionSetService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetVersionSetDelegateTest {

    private GetVersionSetDelegate delegate;
    private VersionSetService versionSetService;

    @Before
    public void setUp() {
        versionSetService = mock(VersionSetService.class);
        delegate = new GetVersionSetDelegate(versionSetService);
    }

    @Test
    public void getVersionSetThatExists() throws VersionSetDoseNotExistsException {
        String vsName = "TestVS-master";
        Instant created = Instant.now();
        String vsParentName = "TestParentVS/master";
        ArchipelagoPackage pA = ArchipelagoPackage.parse("TestPackageA-1.0");
        ArchipelagoPackage pB = ArchipelagoPackage.parse("TestPackageB-1.0");
        ArchipelagoPackage pC = ArchipelagoPackage.parse("TestPackageC-1.0");
        List<ArchipelagoPackage> targets = List.of(pA, pB, pC);
        String revisionId = "123";
        Instant revisionDate = Instant.now();
        Revision revisionA = Revision.builder()
                .revisionId(revisionId)
                .created(revisionDate)
                .build();
        VersionSet vs = VersionSet.builder()
                .name(vsName)
                .created(created)
                .parent(Optional.of(vsParentName))
                .targets(targets)
                .revisions(List.of(revisionA))
                .latestRevisionCreated(Optional.of(revisionDate))
                .latestRevision(Optional.of(revisionId))
                .build();
        when(versionSetService.get(eq(vsName))).thenReturn(vs);
        VersionSet r = delegate.getVersionSet(vsName);
        // We validate the data instead of the VS ref as we want to allow the delegate to create a new object

        Assert.assertNotNull(r);
        Assert.assertEquals(vsName, r.getName());
        Assert.assertEquals(created, r.getCreated());
        Assert.assertTrue(r.getParent().isPresent());
        Assert.assertEquals(vsParentName, r.getParent().get());
        Assert.assertNotNull(r.getTargets());
        Assert.assertEquals(3, r.getTargets().size());
        Assert.assertTrue(r.getTargets().stream().anyMatch(x -> pA.toString().equals(x.toString())));
        Assert.assertTrue(r.getTargets().stream().anyMatch(x -> pB.toString().equals(x.toString())));
        Assert.assertTrue(r.getTargets().stream().anyMatch(x -> pC.toString().equals(x.toString())));
        Assert.assertNotNull(r.getRevisions());
        Assert.assertEquals(1, r.getRevisions().size());
        Assert.assertTrue(r.getRevisions().stream().anyMatch(x -> revisionA.getRevisionId().equals(x.getRevisionId())));
        Assert.assertTrue(r.getLatestRevisionCreated().isPresent());
        Assert.assertEquals(revisionDate, r.getLatestRevisionCreated().get());
        Assert.assertTrue(r.getLatestRevision().isPresent());
        Assert.assertEquals(revisionId, r.getLatestRevision().get());
    }

    @Test(expected = VersionSetDoseNotExistsException.class)
    public void getVersionSetThatDoseNotExists() throws VersionSetDoseNotExistsException {
        String vsName = "TestVS-master";
        when(versionSetService.get(eq(vsName))).thenReturn(null);
        delegate.getVersionSet(vsName);
    }
}