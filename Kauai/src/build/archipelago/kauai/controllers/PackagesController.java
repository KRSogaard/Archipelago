package build.archipelago.kauai.controllers;

import build.archipelago.common.PackageNameVersion;
import build.archipelago.kauai.core.delegates.uploadPackage.PackageArtifactExistsException;
import build.archipelago.kauai.core.delegates.uploadPackage.UploadPackageDelegate;
import build.archipelago.kauai.core.delegates.uploadPackage.UploadPackageDelegateRequest;
import build.archipelago.kauai.models.UploadPackageRequest;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.nio.file.Path;

@RestController
@RequestMapping("packages")
@Slf4j
public class PackagesController {

    private UploadPackageDelegate uploadDelegate;

    public PackagesController(UploadPackageDelegate uploadDelegate) {
        this.uploadDelegate = uploadDelegate;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void uploadPackage(@ModelAttribute UploadPackageRequest request) {
        Preconditions.checkNotNull(request.getBuildArtifact(),
                "build artifact is required");
        Preconditions.checkArgument(request.getBuildArtifact().getSize() > 0,
                "build artifact is required");
        try {
            uploadDelegate.uploadPackage(
                    UploadPackageDelegateRequest.builder()
                            .nameVersion(new PackageNameVersion(request.getName(), request.getVersion()))
                            .hash(request.getHash())
                            .config(request.getConfig())
                            .buildArtifact(request.getBuildArtifact().getBytes())
                            .build()
            );
        } catch (IOException exp) {
            log.error("Failed to read build artifact: " + exp.getMessage());
            throw new RuntimeException(exp);
        } catch (PackageArtifactExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}
