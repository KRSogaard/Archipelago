package build.archipelago.kauai.controllers;

import build.archipelago.kauai.core.delegates.createPackage.CreatePackageDelegate;
import build.archipelago.kauai.core.delegates.createPackage.CreatePackageDelegateRequest;
import build.archipelago.kauai.models.CreatePackageRequest;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("packages")
@Slf4j
public class PackagesController {

    private CreatePackageDelegate createPackageDelegate;

    public PackagesController(CreatePackageDelegate createPackageDelegate) {
        this.createPackageDelegate = createPackageDelegate;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createPackage(@ModelAttribute CreatePackageRequest request) {
        Preconditions.checkNotNull(request);
        Preconditions.checkNotNull(request.getName());
        Preconditions.checkNotNull(request.getDescription());

        createPackageDelegate.create(CreatePackageDelegateRequest.builder()
                                        .name(request.getName())
                                        .description(request.getDescription())
                                        .build());
    }
}
