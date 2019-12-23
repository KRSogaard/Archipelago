package build.archipelago.kauai.controllers;

import build.archipelago.kauai.core.delegates.createPackage.CreatePackageDelegate;
import build.archipelago.kauai.core.delegates.createPackage.CreatePackageDelegateRequest;
import build.archipelago.kauai.core.delegates.getPackage.GetPackageDelegate;
import build.archipelago.kauai.core.delegates.getPackage.GetPackageDelegateRequest;
import build.archipelago.kauai.core.delegates.getPackage.GetPackageDelegateResponse;
import build.archipelago.kauai.core.exceptions.PackageExistsException;
import build.archipelago.kauai.core.exceptions.PackageNotFoundException;
import build.archipelago.kauai.models.CreatePackageRequest;
import build.archipelago.kauai.models.GetPackageResponse;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@RestController
@RequestMapping("packages")
@Slf4j
public class PackagesController {

    private GetPackageDelegate getPackageDelegate;
    private CreatePackageDelegate createPackageDelegate;

    public PackagesController(GetPackageDelegate getPackageDelegate,
                              CreatePackageDelegate createPackageDelegate) {
        this.createPackageDelegate = createPackageDelegate;
        this.getPackageDelegate = getPackageDelegate;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createPackage(@ModelAttribute CreatePackageRequest request) throws PackageExistsException {
        Preconditions.checkNotNull(request);
        Preconditions.checkNotNull(request.getName());
        Preconditions.checkNotNull(request.getDescription());

        createPackageDelegate.create(CreatePackageDelegateRequest.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public GetPackageResponse getPackage(String name) throws PackageNotFoundException {
        Preconditions.checkNotNull(name);

        Optional<GetPackageDelegateResponse> responseOptional = getPackageDelegate.get(
                GetPackageDelegateRequest.builder().name(name).build());
        if (!responseOptional.isPresent()) {
            throw new PackageNotFoundException(name);
        }
        GetPackageDelegateResponse response = responseOptional.get();
        return GetPackageResponse.builder()
                .name(response.getName())
                .description(response.getDescription())
                .create(response.getCreate())
                .build();
    }

}
