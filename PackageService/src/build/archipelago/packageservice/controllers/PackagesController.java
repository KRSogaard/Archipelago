package build.archipelago.packageservice.controllers;

import build.archipelago.common.ArchipelagoBuiltPackage;
import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.common.exceptions.PackageExistsException;
import build.archipelago.common.exceptions.PackageNotFoundException;
import build.archipelago.packageservice.core.data.models.BuiltPackageDetails;
import build.archipelago.packageservice.core.data.models.PackageDetails;
import build.archipelago.packageservice.core.data.models.VersionBuildDetails;
import build.archipelago.packageservice.core.delegates.createPackage.CreatePackageDelegate;
import build.archipelago.packageservice.core.delegates.createPackage.CreatePackageDelegateRequest;
import build.archipelago.packageservice.core.delegates.getPackage.GetPackageDelegate;
import build.archipelago.packageservice.core.delegates.getPackageBuild.GetPackageBuildDelegate;
import build.archipelago.packageservice.core.delegates.getPackageBuilds.GetPackageBuildsDelegate;
import build.archipelago.packageservice.core.delegates.verifyBuildsExists.VerifyBuildsExistsDelegate;
import build.archipelago.packageservice.core.delegates.verifyPackageExists.VerifyPackageExistsDelegate;
import build.archipelago.packageservice.models.CreatePackageRequest;
import build.archipelago.packageservice.models.GetPackageBuildResponse;
import build.archipelago.packageservice.models.GetPackageBuildsResponse;
import build.archipelago.packageservice.models.GetPackageResponse;
import build.archipelago.packageservice.models.VerificationRequest;
import build.archipelago.packageservice.models.VerificationResponse;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("package")
@Slf4j
public class PackagesController {

    private CreatePackageDelegate createPackageDelegate;
    private GetPackageDelegate getPackageDelegate;
    private GetPackageBuildsDelegate getPackageBuildsDelegate;
    private GetPackageBuildDelegate getPackageBuildDelegate;
    private VerifyBuildsExistsDelegate verifyBuildsExistsDelegate;
    private VerifyPackageExistsDelegate verifyPackageExistsDelegate;

    public PackagesController(GetPackageDelegate getPackageDelegate,
                              CreatePackageDelegate createPackageDelegate,
                              GetPackageBuildsDelegate getPackageBuildsDelegate,
                              GetPackageBuildDelegate getPackageBuildDelegate,
                              VerifyBuildsExistsDelegate verifyBuildsExistsDelegate,
                              VerifyPackageExistsDelegate verifyPackageExistsDelegate) {
        Preconditions.checkNotNull(createPackageDelegate);
        this.createPackageDelegate = createPackageDelegate;
        Preconditions.checkNotNull(getPackageDelegate);
        this.getPackageDelegate = getPackageDelegate;
        Preconditions.checkNotNull(getPackageBuildsDelegate);
        this.getPackageBuildsDelegate = getPackageBuildsDelegate;
        Preconditions.checkNotNull(getPackageBuildDelegate);
        this.getPackageBuildDelegate = getPackageBuildDelegate;
        Preconditions.checkNotNull(verifyBuildsExistsDelegate);
        this.verifyBuildsExistsDelegate = verifyBuildsExistsDelegate;
        Preconditions.checkNotNull(verifyPackageExistsDelegate);
        this.verifyPackageExistsDelegate = verifyPackageExistsDelegate;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createPackage(@RequestBody CreatePackageRequest request) throws PackageExistsException {
        Preconditions.checkNotNull(request);
        Preconditions.checkNotNull(request.getName());
        Preconditions.checkNotNull(request.getDescription());

        createPackageDelegate.create(CreatePackageDelegateRequest.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build());
    }

    @GetMapping(value = "{name}")
    @ResponseStatus(HttpStatus.OK)
    public GetPackageResponse getPackage(
            @PathVariable("name") String name
    ) throws PackageNotFoundException {
        Preconditions.checkNotNull(name);

        PackageDetails pkg = getPackageDelegate.get(name);

        return GetPackageResponse.builder()
                .name(pkg.getName())
                .description(pkg.getDescription())
                .created(pkg.getCreated().toEpochMilli())
                .versions(pkg.getVersions().stream().map(x -> new GetPackageResponse.Version(
                        x.getVersion(),
                        x.getLatestBuildHash(),
                        x.getLatestBuildTime().toEpochMilli())).collect(Collectors.toList()))
                .build();
    }

    @GetMapping(value = "{name}/{version}")
    @ResponseStatus(HttpStatus.OK)
    public GetPackageBuildsResponse getPackageBuilds(
            @PathVariable("name") String name,
            @PathVariable("version") String version
    ) throws PackageNotFoundException {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(version);
        ArchipelagoPackage pkg = new ArchipelagoPackage(name, version);
        log.info("Request to get builds for {}", pkg);

        ImmutableList<VersionBuildDetails> builds = getPackageBuildsDelegate.get(pkg);

        return GetPackageBuildsResponse.builder()
                .builds(builds.stream()
                        .map(x -> new GetPackageBuildsResponse.Build(x.getHash(), x.getCreated().toEpochMilli()))
                        .collect(Collectors.toList()))
                .build();
    }

    @GetMapping(value = "{name}/{version}/{hash}")
    @ResponseStatus(HttpStatus.OK)
    public GetPackageBuildResponse getPackageBuild(
            @PathVariable("name") String name,
            @PathVariable("version") String version,
            @PathVariable("hash") String hash
    ) throws PackageNotFoundException {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(version);
        Preconditions.checkNotNull(hash);
        ArchipelagoBuiltPackage pkg = new ArchipelagoBuiltPackage(name, version, hash);
        log.info("Request to get build details for {}", pkg);

        BuiltPackageDetails build = getPackageBuildDelegate.get(pkg);

        return GetPackageBuildResponse.builder()
                .hash(build.getHash())
                .config(build.getConfig())
                .created(build.getCreated().toEpochMilli())
                .build();
    }

    @PostMapping(value = "verify-packages")
    @ResponseStatus(HttpStatus.OK)
    public VerificationResponse verifyPackages(@RequestBody VerificationRequest request) {
        ImmutableList.Builder<ArchipelagoPackage> packages = ImmutableList.builder();
        for (String pkg : request.getPackages()) {
            packages.add(ArchipelagoPackage.parse(pkg));
        }
        ImmutableList<ArchipelagoPackage> pkgs = packages.build();
        log.info("Verifying packages for: {}", pkgs);

        ImmutableList<ArchipelagoPackage> missing = verifyPackageExistsDelegate.verify(pkgs);
        return VerificationResponse.builder()
                .missing(missing.stream().map(ArchipelagoPackage::getNameVersion).collect(Collectors.toList()))
                .build();
    }

    @PostMapping(value = "verify-builds")
    @ResponseStatus(HttpStatus.OK)
    public VerificationResponse verifyBuilds(@RequestBody VerificationRequest request) {
        ImmutableList.Builder<ArchipelagoBuiltPackage> packages = ImmutableList.builder();
        for (String pkg : request.getPackages()) {
            packages.add(ArchipelagoBuiltPackage.parse(pkg));
        }
        ImmutableList<ArchipelagoBuiltPackage> pkgs = packages.build();
        log.info("Verifying builds for: {}", pkgs);

        ImmutableList<ArchipelagoBuiltPackage> missing = verifyBuildsExistsDelegate.verify(pkgs);
        return VerificationResponse.builder()
                .missing(missing.stream().map(ArchipelagoBuiltPackage::toString).collect(Collectors.toList()))
                .build();
    }
}
