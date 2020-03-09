package build.archipelago.packageservice.controllers;

import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.packageservice.core.delegates.getBuildArtifact.GetBuildArtifactDelegate;
import build.archipelago.packageservice.core.delegates.getBuildArtifact.GetBuildArtifactResponse;
import build.archipelago.packageservice.core.delegates.uploadBuildArtifact.UploadBuildArtifactDelegate;
import build.archipelago.packageservice.common.exceptions.PackageArtifactExistsException;
import build.archipelago.packageservice.common.exceptions.PackageNotFoundException;
import build.archipelago.packageservice.models.UploadPackageRequest;
import build.archipelago.packageservice.utils.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(ArtifactController.class)
public class ArtifactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UploadBuildArtifactDelegate uploadBuildArtifactDelegate;

    @MockBean
    private GetBuildArtifactDelegate getBuildArtifactDelegate;

    @Test
    public void uploadValidArtifactForExistingPackage() throws Exception {
        String packageName = "PackageA";
        String hash = UUID.randomUUID().toString();
        String version = "1.0";

        this.mockMvc.perform(createUploadRequest(UploadPackageRequest.builder()
                        .name(packageName)
                        .version(version)
                        .hash(hash)
                        .config(TestUtils.readResourceFile("configExample.yml"))
                        .build(),
                "artifact.zip"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testUploadingArtifactForNotExistingPackage() throws Exception {
        String packageName = "PackageA";
        String hash = UUID.randomUUID().toString();
        String version = "1.0";

        doThrow(new PackageNotFoundException(new ArchipelagoPackage(packageName, version)))
                .when(uploadBuildArtifactDelegate).uploadArtifact(any());

        this.mockMvc.perform(createUploadRequest(UploadPackageRequest.builder()
                        .name(packageName)
                        .version(version)
                        .hash(hash)
                        .config(TestUtils.readResourceFile("configExample.yml"))
                        .build(),
                "artifact.zip"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUploadingDuplicationArtifactForPackage() throws Exception {
        String packageName = "PackageA";
        String hash = UUID.randomUUID().toString();
        String version = "1.0";

        doThrow(new PackageArtifactExistsException("Hash already uploaded"))
                .when(uploadBuildArtifactDelegate).uploadArtifact(any());

        this.mockMvc.perform(createUploadRequest(UploadPackageRequest.builder()
                        .name(packageName)
                        .version(version)
                        .hash(hash)
                        .config(TestUtils.readResourceFile("configExample.yml"))
                        .build(),
                "artifact.zip"))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void testGetValidBuildArtifactWithoutHash() throws Exception {
        String packageName = "PackageA";
        String hash = UUID.randomUUID().toString();
        String version = "1.0";
        ArchipelagoPackage nameVersion = new ArchipelagoPackage(packageName, version);
        byte[] byteArray = TestUtils.readBinaryResourceFile("artifact.zip");

        GetBuildArtifactResponse response = GetBuildArtifactResponse.builder()
                .nameVersion(nameVersion)
                .hash(hash)
                .byteArray(byteArray)
                .build();
        when(getBuildArtifactDelegate.getBuildArtifact(
                argThat(argument -> packageName.equalsIgnoreCase(argument.getName()) &&
                        version.equals(argument.getVersion())),
                argThat(argument -> !argument.isPresent())))
                .thenReturn(Optional.of(response));

        this.mockMvc.perform(createGetArtifact(
                nameVersion.getConcatenated(), Optional.empty()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().bytes(byteArray));
    }

    @Test
    public void testGetValidBuildArtifactWithHash() throws Exception {
        String packageName = "PackageA";
        String hash = UUID.randomUUID().toString();
        String version = "1.0";
        ArchipelagoPackage nameVersion = new ArchipelagoPackage(packageName, version);
        byte[] byteArray = TestUtils.readBinaryResourceFile("artifact.zip");

        GetBuildArtifactResponse response = GetBuildArtifactResponse.builder()
                .nameVersion(nameVersion)
                .hash(hash)
                .byteArray(byteArray)
                .build();
        when(getBuildArtifactDelegate.getBuildArtifact(
                argThat(argument -> packageName.equalsIgnoreCase(argument.getName()) &&
                        version.equals(argument.getVersion())),
                argThat(argument -> argument.isPresent() && hash.equals(argument.get()))))
                .thenReturn(Optional.of(response));

        this.mockMvc.perform(createGetArtifact(
                nameVersion.getConcatenated(), Optional.of(hash)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().bytes(byteArray));
    }

    @Test
    public void testGetPackageNotExistsWithoutHash() throws Exception {
        String packageName = "PackageA";
        String version = "1.0";
        ArchipelagoPackage nameVersion = new ArchipelagoPackage(packageName, version);

        when(getBuildArtifactDelegate.getBuildArtifact(
                argThat(argument -> packageName.equalsIgnoreCase(argument.getName()) &&
                        version.equals(argument.getVersion())),
                argThat(argument -> !argument.isPresent())))
                .thenReturn(Optional.empty());

        this.mockMvc.perform(createGetArtifact(
                nameVersion.getConcatenated(), Optional.empty()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPackageThatExistsWithAHashThatDoseNot() throws Exception {
        String packageName = "PackageA";
        String version = "1.0";
        ArchipelagoPackage nameVersion = new ArchipelagoPackage(packageName, version);

        when(getBuildArtifactDelegate.getBuildArtifact(
                argThat(argument -> packageName.equalsIgnoreCase(argument.getName()) &&
                        version.equals(argument.getVersion())),
                argThat(argument -> !argument.isPresent())))
                .thenReturn(Optional.empty());

        this.mockMvc.perform(createGetArtifact(
                nameVersion.getConcatenated(), Optional.empty()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPackageNotExistsWithHash() throws Exception {
        String packageName = "PackageA";
        String hash = UUID.randomUUID().toString();
        String version = "1.0";
        ArchipelagoPackage nameVersion = new ArchipelagoPackage(packageName, version);

        when(getBuildArtifactDelegate.getBuildArtifact(
                argThat(argument -> packageName.equalsIgnoreCase(argument.getName()) &&
                        version.equals(argument.getVersion())),
                argThat(argument -> !argument.isPresent())))
                .thenReturn(Optional.empty());

        this.mockMvc.perform(createGetArtifact(
                nameVersion.getConcatenated(), Optional.of(hash)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private MockHttpServletRequestBuilder createUploadRequest(UploadPackageRequest request, String file) throws IOException {
        return MockMvcRequestBuilders.multipart("/artifact")
                .file(new MockMultipartFile("buildArtifact", file, "application/zip",
                        TestUtils.readBinaryResourceFile(file)))
                .param("name", request.getName())
                .param("version", request.getVersion())
                .param("hash", request.getHash())
                .param("config", request.getConfig());
    }

    private MockHttpServletRequestBuilder createGetArtifact(String nameAndVersion, Optional<String> hash) {
        StringBuilder sb = new StringBuilder("/artifact/");
        sb.append(nameAndVersion);
        if (hash.isPresent()) {
            sb.append("/");
            sb.append(hash.get());
        }
        return get(sb.toString());
    }
}