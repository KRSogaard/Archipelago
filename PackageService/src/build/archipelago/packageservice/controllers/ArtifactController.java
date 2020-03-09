package build.archipelago.packageservice.controllers;

import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.packageservice.core.delegates.getBuildArtifact.GetBuildArtifactDelegate;
import build.archipelago.packageservice.core.delegates.getBuildArtifact.GetBuildArtifactResponse;
import build.archipelago.packageservice.core.delegates.uploadBuildArtifact.UploadBuildArtifactDelegate;
import build.archipelago.packageservice.core.delegates.uploadBuildArtifact.UploadBuildArtifactDelegateRequest;
import build.archipelago.packageservice.common.exceptions.PackageArtifactExistsException;
import build.archipelago.packageservice.common.exceptions.PackageNotFoundException;
import build.archipelago.packageservice.models.UploadPackageRequest;
import com.google.common.base.Preconditions;
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

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void uploadBuiltArtifact(@ModelAttribute UploadPackageRequest request)
            throws PackageArtifactExistsException, PackageNotFoundException {
        Preconditions.checkNotNull(request.getBuildArtifact(),
                "build artifact is required");
        Preconditions.checkArgument(request.getBuildArtifact().getSize() > 0,
                "build artifact is required");
        try {
            uploadBuildArtifactDelegate.uploadArtifact(
                    UploadBuildArtifactDelegateRequest.builder()
                            .nameVersion(new ArchipelagoPackage(request.getName(), request.getVersion()))
                            .hash(request.getHash())
                            .config(request.getConfig())
                            .buildArtifact(request.getBuildArtifact().getBytes())
                            .build()
            );
        } catch (IOException e) {
            log.error("Failed to read build artifact: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping(value = {"{nameVersion}/{hash}", "{nameVersion}"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> getBuildArtifact(
            @PathVariable("nameVersion") String nameAndVersion,
            @PathVariable("hash") Optional<String> hash) throws PackageNotFoundException {
        ArchipelagoPackage nameVersion = ArchipelagoPackage.parse(nameAndVersion);
        nameVersion.validate();

        Optional<GetBuildArtifactResponse> response = null;
        try {
            response = getBuildArtifactDelegate.getBuildArtifact(nameVersion, hash);
        } catch (IOException e) {
            log.error("Unable to read build artifact. " + e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        if (!response.isPresent()) {
            throw new PackageNotFoundException(nameVersion, hash);
        }

        String zipFileName = String.format("%s-%s.zip", response.get().getNameVersion().getConcatenated(),
                response.get().getHash());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"")
                .body(new ByteArrayResource(response.get().getByteArray()));
    }
}
