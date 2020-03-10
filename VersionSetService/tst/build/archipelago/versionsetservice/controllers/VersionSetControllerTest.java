package build.archipelago.versionsetservice.controllers;


import build.archipelago.packageservice.client.models.GetPackageResponse;
import build.archipelago.versionsetservice.core.delegates.CreateVersionSetDelegate;
import build.archipelago.versionsetservice.core.delegates.CreateVersionSetRevisionDelegate;
import build.archipelago.versionsetservice.core.delegates.GetVersionSetDelegate;
import build.archipelago.versionsetservice.core.delegates.GetVersionSetPackagesDelegate;
import build.archipelago.versionsetservice.core.exceptions.VersionSetDoseNotExistsException;
import build.archipelago.versionsetservice.core.models.Revision;
import build.archipelago.versionsetservice.core.models.VersionSet;
import build.archipelago.versionsetservice.core.utils.RevisionUtil;
import build.archipelago.versionsetservice.models.RevisionIdResponse;
import build.archipelago.versionsetservice.models.VersionSetResponse;
import build.archipelago.versionsetservice.utils.TestConstants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dongliu.gson.GsonJava8TypeAdapterFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.text.html.Option;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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