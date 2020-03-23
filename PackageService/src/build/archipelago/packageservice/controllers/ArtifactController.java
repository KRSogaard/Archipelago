package build.archipelago.packageservice.controllers;

import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.common.exceptions.PackageExistsException;
import build.archipelago.common.exceptions.PackageNotFoundException;
import build.archipelago.packageservice.core.delegates.getBuildArtifact.GetBuildArtifactDelegate;
import build.archipelago.packageservice.core.delegates.getBuildArtifact.GetBuildArtifactResponse;
import build.archipelago.packageservice.core.delegates.uploadBuildArtifact.UploadBuildArtifactDelegate;
import build.archipelago.packageservice.core.delegates.uploadBuildArtifact.UploadBuildArtifactDelegateRequest;
import build.archipelago.packageservice.models.ArtifactUploadResponse;
import build.archipelago.packageservice.models.UploadPackageRequest;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("artifact")
@Slf4j
public class ArtifactController {

    private UploadBuildArtifactDelegate uploadBuildArtifactDelegate;
    private GetBuildArtifactDelegate getBuildArtifactDelegate;

    public ArtifactController(UploadBuildArtifactDelegate uploadBuildArtifactDelegate,
                              GetBuildArtifactDelegate getBuildArtifactDelegate) {
        this.uploadBuildArtifactDelegate = uploadBuildArtifactDelegate;
        this.getBuildArtifactDelegate = getBuildArtifactDelegate;
    }

    @PostMapping("{name}/{version}")
    @ResponseStatus(HttpStatus.OK)
    public ArtifactUploadResponse uploadBuiltArtifact(
            @PathVariable("name") String name,
            @PathVariable("version") String version,
            @ModelAttribute UploadPackageRequest request)
            throws PackageNotFoundException, PackageExistsException {
        log.info("Request to upload new build: {}", request);
        Preconditions.checkNotNull(request.getBuildArtifact(),
                "build artifact is required");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "A name is required");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(version), "A version is required");
        Preconditions.checkArgument(request.getBuildArtifact().getSize() > 0,
                "build artifact is required");
        try {
            String hash = uploadBuildArtifactDelegate.uploadArtifact(
                    UploadBuildArtifactDelegateRequest.builder()
                            .pkg(new ArchipelagoPackage(name, version))
                            .config(request.getConfig())
                            .buildArtifact(request.getBuildArtifact().getBytes())
                            .build()
            );
            return ArtifactUploadResponse.builder()
                    .hash(hash)
                    .build();
        } catch (IOException e) {
            log.error("Failed to read build artifact: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping(value = {"{name}/{version}/{hash}", "{name}/{version}"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> getBuildArtifact(
            @PathVariable("name") String name,
            @PathVariable("version") String version,
            @PathVariable("hash") Optional<String> hash) throws PackageNotFoundException {
        log.info("Request to get build artifact for Package {}, Version: {}, Hash: {}", name, version, hash);
        ArchipelagoPackage pkg = new ArchipelagoPackage(name, version);

        GetBuildArtifactResponse response = null;
        try {
            response = getBuildArtifactDelegate.getBuildArtifact(pkg, hash);
        } catch (IOException e) {
            log.error("Unable to read build artifact. " + e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        String zipFileName = String.format("%s.zip", response.getPkg().toString());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"")
                .body(new ByteArrayResource(response.getByteArray()));
    }
}
