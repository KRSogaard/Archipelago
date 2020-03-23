package build.archipelago.versionsetservice.controllers;

import build.archipelago.common.ArchipelagoBuiltPackage;
import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.versionsetservice.core.delegates.CreateVersionSetDelegate;
import build.archipelago.versionsetservice.core.delegates.CreateVersionSetRevisionDelegate;
import build.archipelago.versionsetservice.core.delegates.GetVersionSetDelegate;
import build.archipelago.versionsetservice.core.delegates.GetVersionSetPackagesDelegate;
import build.archipelago.common.exceptions.VersionSetDoseNotExistsException;
import build.archipelago.common.exceptions.VersionSetExistsException;
import build.archipelago.versionsetservice.core.models.Revision;
import build.archipelago.versionsetservice.core.models.VersionSet;
import build.archipelago.versionsetservice.core.models.VersionSetRevision;
import build.archipelago.versionsetservice.core.utils.RevisionUtil;
import build.archipelago.versionsetservice.models.CreateVersionSetRevisionResponse;
import build.archipelago.versionsetservice.models.VersionSetResponse;
import build.archipelago.versionsetservice.models.VersionSetRevisionResponse;
import build.archipelago.versionsetservice.utils.TestConstants;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dongliu.gson.GsonJava8TypeAdapterFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(VersionSetController.class)
public class VersionSetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateVersionSetDelegate createVersionSetDelegate;
    @MockBean
    private CreateVersionSetRevisionDelegate createVersionSetRevisionDelegate;
    @MockBean
    private GetVersionSetDelegate getVersionSetDelegate;
    @MockBean
    private GetVersionSetPackagesDelegate getVersionSetPackagesDelegate;

    private Gson gson;

    @Before
    public void setUp() {
        gson = new GsonBuilder().registerTypeAdapterFactory(new GsonJava8TypeAdapterFactory()).create();
    }

    @Test
    public void testGetValidVersionSet() throws VersionSetDoseNotExistsException, Exception {
        String vsName = "TestVS-" + RevisionUtil.getRandomRevisionId();
        VersionSet vs = createValidVS(vsName);
        when(getVersionSetDelegate.getVersionSet(eq(vsName))).thenReturn(vs);

        MvcResult result = mockMvc.perform(get("/version-sets/" + vsName))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertFalse(Strings.isNullOrEmpty(content));

        VersionSetResponse response = gson.fromJson(content, VersionSetResponse.class);
        assertEquals(vsName, response.getName());
    }

    @Test
    public void testGetValidVersionSetWithRevision() throws VersionSetDoseNotExistsException, Exception {
        String vsName = "TestVS-" + RevisionUtil.getRandomRevisionId();
        String revisionId = RevisionUtil.getRandomRevisionId();
        Instant created = Instant.now();
        VersionSet vs = VersionSet.builder()
                .name(vsName)
                .created(Instant.now())
                .targets(List.of(TestConstants.pkgA))
                .revisions(List.of(Revision.builder()
                        .revisionId(revisionId)
                        .created(created)
                        .build()))
                .latestRevisionCreated(Optional.of(created))
                .latestRevision(Optional.of(revisionId))
                .build();
        when(getVersionSetDelegate.getVersionSet(eq(vsName))).thenReturn(vs);

        MvcResult result = mockMvc.perform(get("/version-sets/" + vsName))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertFalse(Strings.isNullOrEmpty(content));

        VersionSetResponse response = gson.fromJson(content, VersionSetResponse.class);
        assertEquals(vsName, response.getName());
        assertTrue(response.getLatestRevision().isPresent());
        assertEquals(revisionId, response.getLatestRevision().get());
    }

    @Test
    public void testGetValidVersionSetWithDifferentVSNames() throws VersionSetDoseNotExistsException, Exception {
        testVSName(RevisionUtil.getRandomRevisionId());
        testVSName(RevisionUtil.getRandomRevisionId() + "-");
        testVSName(RevisionUtil.getRandomRevisionId() + "_");
        testVSName(RevisionUtil.getRandomRevisionId() + "-" + RevisionUtil.getRandomRevisionId());
        testVSName(RevisionUtil.getRandomRevisionId() + "_" + RevisionUtil.getRandomRevisionId());
    }

    @Test
    public void testCreatedValidVersionSetWith1Target() throws VersionSetExistsException,
            VersionSetDoseNotExistsException, Exception {
        String vsName = "TestVS-" + RevisionUtil.getRandomRevisionId();
        List<ArchipelagoPackage> targets = List.of(TestConstants.pkgA);
        mockMvc.perform(post("/version-sets/")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(String.format("{\n" +
                        "                        \"name\": \"%s\",\n" +
                        "                        \"targets\": [\n" +
                        "                            \"%s\"\n" +
                        "                        ]\n" +
                        "                    }", vsName, targets.get(0).toString())))
                .andDo(print())
                .andExpect(status().isOk());

        verify(createVersionSetDelegate, times(1)).create(
                eq(vsName),
                argThat(given -> {
                    for (ArchipelagoPackage p : targets) {
                        if (given.stream().noneMatch(x -> x.toString().equals(p.toString()))) {
                            return false;
                        }
                    }
                    return true;
                }),
                eq(Optional.empty()));
    }

    @Test
    public void testCreatedVersionSetThatExistsShouldFail() throws VersionSetExistsException,
            VersionSetDoseNotExistsException, Exception {
        String vsName = "TestVS-" + RevisionUtil.getRandomRevisionId();
        List<ArchipelagoPackage> targets = List.of(TestConstants.pkgA);
        doThrow(new VersionSetExistsException(vsName)).when(createVersionSetDelegate)
                .create(eq(vsName), anyList(), any());

        mockMvc.perform(post("/version-sets/")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(String.format("{\n" +
                        "                        \"name\": \"%s\",\n" +
                        "                        \"targets\": [\n" +
                        "                            \"%s\"\n" +
                        "                        ]\n" +
                        "                    }", vsName, targets.get(0).toString())))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void testCreatedValidVersionSetWith2Target() throws VersionSetExistsException,
            VersionSetDoseNotExistsException, Exception {
        String vsName = "TestVS-" + RevisionUtil.getRandomRevisionId();
        List<ArchipelagoPackage> targets = List.of(TestConstants.pkgA, TestConstants.pkgB);
        mockMvc.perform(post("/version-sets/")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(String.format("{\n" +
                        "                        \"name\": \"%s\",\n" +
                        "                        \"targets\": [\n" +
                        "                            \"%s\",\n" +
                        "                            \"%s\"\n" +
                        "                        ]\n" +
                        "                    }", vsName, targets.get(0).toString(), targets.get(1).toString())))
                .andDo(print())
                .andExpect(status().isOk());

        verify(createVersionSetDelegate, times(1)).create(
                eq(vsName),
                argThat(given -> {
                    for (ArchipelagoPackage p : targets) {
                        if (given.stream().noneMatch(x -> x.toString().equals(p.toString()))) {
                            return false;
                        }
                    }
                    return true;
                }),
                eq(Optional.empty()));
    }

    @Test
    public void testCreatedVersionSetWith0TargetShouldFail() throws VersionSetExistsException,
            VersionSetDoseNotExistsException, Exception {
        String vsName = "TestVS-" + RevisionUtil.getRandomRevisionId();
        mockMvc.perform(post("/version-sets/")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(String.format("{\n" +
                        "                        \"name\": \"%s\",\n" +
                        "                        \"targets\": []\n" +
                        "                    }", vsName)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreatedVersionSetWith1TargetThatIsMissingVersion() throws Exception {
        String vsName = "TestVS-" + RevisionUtil.getRandomRevisionId();
        List<ArchipelagoPackage> targets = List.of(TestConstants.pkgA);
        mockMvc.perform(post("/version-sets/")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(String.format("{\n" +
                        "                        \"name\": \"%s\",\n" +
                        "                        \"targets\": [\n" +
                        "                            \"%s\"\n" +
                        "                        ]\n" +
                        "                    }", vsName, TestConstants.pkgA.getName())))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreatedVersionSetWith1TargetThatIsEmptyVersion() throws Exception {
        String vsName = "TestVS-" + RevisionUtil.getRandomRevisionId();
        List<ArchipelagoPackage> targets = List.of(TestConstants.pkgA);
        mockMvc.perform(post("/version-sets/")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(String.format("{\n" +
                        "                        \"name\": \"%s\",\n" +
                        "                        \"targets\": [\n" +
                        "                            \"%s\"\n" +
                        "                        ]\n" +
                        "                    }", vsName, TestConstants.pkgA.getName() + "-")))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreatedVersionSetWith1TargetThatHasBuild() throws Exception {
        String vsName = "TestVS-" + RevisionUtil.getRandomRevisionId();
        List<ArchipelagoPackage> targets = List.of(TestConstants.pkgA);
        mockMvc.perform(post("/version-sets/")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(String.format("{\n" +
                        "                        \"name\": \"%s\",\n" +
                        "                        \"targets\": [\n" +
                        "                            \"%s\"\n" +
                        "                        ]\n" +
                        "                    }", vsName, TestConstants.pkgA.toString() + ":" + RevisionUtil.getRandomRevisionId())))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreatedValidVersionSetWith1TargetWithParent() throws VersionSetExistsException,
            VersionSetDoseNotExistsException, Exception {
        String vsName = "TestVS-" + RevisionUtil.getRandomRevisionId();
        String vsParentName = "TestVS-" + RevisionUtil.getRandomRevisionId();
        List<ArchipelagoPackage> targets = List.of(TestConstants.pkgA);
        mockMvc.perform(post("/version-sets/")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(String.format("{\n" +
                        "                        \"name\": \"%s\",\n" +
                        "                        \"parent\": \"%s\",\n" +
                        "                        \"targets\": [\n" +
                        "                            \"%s\"\n" +
                        "                        ]\n" +
                        "                    }", vsName, vsParentName, targets.get(0).toString())))
                .andDo(print())
                .andExpect(status().isOk());

        verify(createVersionSetDelegate, times(1)).create(
                eq(vsName),
                argThat(given -> {
                    for (ArchipelagoPackage p : targets) {
                        if (given.stream().noneMatch(x -> x.toString().equals(p.toString()))) {
                            return false;
                        }
                    }
                    return true;
                }),
                eq(Optional.of(vsParentName)));
    }

    @Test
    public void testCreateValidVSRevisionWith3Packages() throws Exception, VersionSetDoseNotExistsException {
        String vsName = "TestVS-" + RevisionUtil.getRandomRevisionId();
        List<ArchipelagoBuiltPackage> targets = List.of(TestConstants.pkgABuild, TestConstants.pkgBBuild,
                TestConstants.pkgCBuild);
        String RevisionId = RevisionUtil.getRandomRevisionId();
        when(createVersionSetRevisionDelegate.createRevision(any(), anyList())).thenReturn(RevisionId);
        var result = mockMvc.perform(post("/version-sets/" + vsName)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(String.format("{\n" +
                                "                        \"packages\": [\n" +
                                "                            \"%s\", \"%s\", \"%s\"\n" +
                                "                        ]\n" +
                                "                    }", targets.get(0).toString(), targets.get(1).toString(),
                        targets.get(2).toString())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();


        verify(createVersionSetRevisionDelegate, times(1)).createRevision(
                eq(vsName),
                argThat(given -> {
                    for (ArchipelagoBuiltPackage p : targets) {
                        if (given.stream().noneMatch(x -> x.toString().equals(p.toString()))) {
                            return false;
                        }
                    }
                    return true;
                }));


        String content = result.getResponse().getContentAsString();
        assertFalse(Strings.isNullOrEmpty(content));

        CreateVersionSetRevisionResponse response = gson.fromJson(content, CreateVersionSetRevisionResponse.class);
        assertEquals(RevisionId, response.getRevisionId());
    }

    @Test
    public void testCreateRevisionWithNonExistingVersionSetShouldFail() throws Exception,
            VersionSetDoseNotExistsException {
        String vsName = "TestVS-" + RevisionUtil.getRandomRevisionId();
        List<ArchipelagoBuiltPackage> targets = List.of(TestConstants.pkgABuild);
        when(createVersionSetRevisionDelegate.createRevision(any(), anyList()))
                .thenThrow(new VersionSetDoseNotExistsException(vsName));
        mockMvc.perform(post("/version-sets/" + vsName)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(String.format("{\n" +
                        "                        \"packages\": [\n" +
                        "                            \"%s\"\n" +
                        "                        ]\n" +
                        "                    }", targets.get(0).toString())))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateRevisionWithPackageWithoutBuildIdShouldFail() throws Exception {
        String vsName = "TestVS-" + RevisionUtil.getRandomRevisionId();

        mockMvc.perform(post("/version-sets/" + vsName)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(String.format("{\n" +
                        "                        \"packages\": [\n" +
                        "                            \"%s\"\n" +
                        "                        ]\n" +
                        "                    }", TestConstants.pkgA.toString())))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetVSRevisionPackages() throws Exception, VersionSetDoseNotExistsException {
        String vsName = "TestVS-" + RevisionUtil.getRandomRevisionId();
        String revision = RevisionUtil.getRandomRevisionId();
        List<ArchipelagoBuiltPackage> packages = List.of(TestConstants.pkgABuild, TestConstants.pkgBBuild);
        Instant created = Instant.now();

        when(getVersionSetPackagesDelegate.getPackages(eq(vsName), eq(revision)))
                .thenReturn(VersionSetRevision.builder()
                    .packages(packages)
                    .created(created)
                    .build());

        var result = mockMvc.perform(get("/version-sets/" + vsName + "/" + revision))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertFalse(Strings.isNullOrEmpty(content));

        VersionSetRevisionResponse response = gson.fromJson(content, VersionSetRevisionResponse.class);
        Assert.assertEquals((Long)created.toEpochMilli(), response.getCreated());
        for (ArchipelagoBuiltPackage p : packages) {
            Assert.assertTrue(response.getPackages().stream()
                    .anyMatch(x -> x != null &&  x.equals(p.toString())));
        }
    }

    @Test
    public void testGetVSRevisionPackagesWhereVersionDoseNotExistsShouldFail() throws Exception,
            VersionSetDoseNotExistsException {
        String vsName = "TestVS-" + RevisionUtil.getRandomRevisionId();
        String revision = RevisionUtil.getRandomRevisionId();

        when(getVersionSetPackagesDelegate.getPackages(eq(vsName), eq(revision)))
                .thenThrow(new VersionSetDoseNotExistsException(vsName));

        mockMvc.perform(get("/version-sets/" + vsName + "/" + revision))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private void testVSName(String vsName) throws VersionSetDoseNotExistsException, Exception {
        VersionSet vs = createValidVS(vsName);
        when(getVersionSetDelegate.getVersionSet(eq(vsName))).thenReturn(vs);

        MvcResult result = mockMvc.perform(get("/version-sets/" + vsName))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertFalse(Strings.isNullOrEmpty(content));

        VersionSetResponse response = gson.fromJson(content, VersionSetResponse.class);
        assertEquals(vsName, response.getName());
    }

    private VersionSet createValidVS(String name) {
        return VersionSet.builder()
                .name(name)
                .created(Instant.now())
                .targets(List.of(TestConstants.pkgA))
                .revisions(new ArrayList<>())
                .build();
    }
}