package build.archipelago.versionsetservice.controllers;

import build.archipelago.common.ArchipelagoBuiltPackage;
import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.packageservice.common.exceptions.PackageNotFoundException;
import build.archipelago.versionsetservice.core.delegates.CreateVersionSetDelegate;
import build.archipelago.versionsetservice.core.delegates.CreateVersionSetRevisionDelegate;
import build.archipelago.versionsetservice.core.delegates.GetVersionSetDelegate;
import build.archipelago.versionsetservice.core.delegates.GetVersionSetPackagesDelegate;
import build.archipelago.versionsetservice.core.exceptions.MissingTargetPackageException;
import build.archipelago.versionsetservice.core.exceptions.VersionSetDoseNotExistsException;
import build.archipelago.versionsetservice.core.exceptions.VersionSetExistsException;
import build.archipelago.versionsetservice.core.models.VersionSet;
import build.archipelago.versionsetservice.core.models.VersionSetRevision;
import build.archipelago.versionsetservice.models.CreateVersionSetRequest;
import build.archipelago.versionsetservice.models.CreateVersionSetRevisionRequest;
import build.archipelago.versionsetservice.models.CreateVersionSetRevisionResponse;
import build.archipelago.versionsetservice.models.RevisionIdResponse;
import build.archipelago.versionsetservice.models.VersionSetResponse;
import build.archipelago.versionsetservice.models.VersionSetRevisionResponse;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("version-sets")
@Slf4j
public class VersionSetController {

    private CreateVersionSetDelegate createVersionSetDelegate;
    private CreateVersionSetRevisionDelegate createVersionSetRevisionDelegate;
    private GetVersionSetDelegate getVersionSetDelegate;
    private GetVersionSetPackagesDelegate getVersionSetPackagesDelegate;

    public VersionSetController(CreateVersionSetDelegate createVersionSetDelegate,
                                CreateVersionSetRevisionDelegate createVersionSetRevisionDelegate,
                                GetVersionSetDelegate getVersionSetDelegate,
                                GetVersionSetPackagesDelegate getVersionSetPackagesDelegate) {
        this.createVersionSetDelegate = createVersionSetDelegate;
        this.createVersionSetRevisionDelegate = createVersionSetRevisionDelegate;
        this.getVersionSetDelegate = getVersionSetDelegate;
        this.getVersionSetPackagesDelegate = getVersionSetPackagesDelegate;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void createVersionSet(CreateVersionSetRequest request) throws
            VersionSetDoseNotExistsException, VersionSetExistsException, PackageNotFoundException {
        request.validate();

        List<ArchipelagoPackage> targets = request.getTargets().stream()
                .map(ArchipelagoPackage::parse).collect(Collectors.toList());

        createVersionSetDelegate.create(request.getName(), targets, request.getParent());
    }

    @PostMapping("/{versionSet}")
    @ResponseStatus(HttpStatus.OK)
    public CreateVersionSetRevisionResponse createVersionSetRevision(
            @RequestBody CreateVersionSetRevisionRequest request) throws VersionSetDoseNotExistsException,
            MissingTargetPackageException, PackageNotFoundException {
        request.validate();
        List<ArchipelagoBuiltPackage> packages = request.getPackages().stream()
                .map(ArchipelagoBuiltPackage::parse).collect(Collectors.toList());

        String revisionId = createVersionSetRevisionDelegate.createRevision(
                request.getVersionSetName(), packages);

        return CreateVersionSetRevisionResponse.builder()
                .revisionId(revisionId)
                .build();
    }

    @GetMapping("{versionSet}")
    @ResponseStatus(HttpStatus.OK)
    public VersionSetResponse getVersionSet(@PathVariable("versionSet") String versionSetName)
            throws VersionSetDoseNotExistsException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(versionSetName),
                "Version Set name is required");

        VersionSet vs = getVersionSetDelegate.getVersionSet(versionSetName);

        return VersionSetResponse.builder()
                .name(vs.getName())
                .created(vs.getCreated().toEpochMilli())
                .parent(vs.getParent())
                .targets(vs.getTargets().stream().map(ArchipelagoPackage::getConcatenated).collect(Collectors.toList()))
                .revisions(vs.getRevisions().stream().map(RevisionIdResponse::from).collect(Collectors.toList()))
                .latestRevision(vs.getLatestRevision())
                .latestRevisionCreated(
                        vs.getLatestRevisionCreated() != null && vs.getLatestRevisionCreated().isPresent() ?
                        Optional.of(vs.getLatestRevisionCreated().get().toEpochMilli()) : Optional.empty())
                .build();
    }

    @GetMapping("{versionSet}/{revision}")
    @ResponseStatus(HttpStatus.OK)
    public VersionSetRevisionResponse getVersionSetPackages(@PathVariable("versionSet") String versionSetName,
                                                         @PathVariable("revision") String revisionId)
            throws VersionSetDoseNotExistsException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(versionSetName),
                "Version Set name is required");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(revisionId),
                "Revision id is required");

        VersionSetRevision revision = getVersionSetPackagesDelegate.getPackages(versionSetName, revisionId);

        return VersionSetRevisionResponse.builder()
                .created(revision.getCreated().toEpochMilli())
                .packages(revision.getPackages().stream()
                        .map(ArchipelagoBuiltPackage::getConcatenated).collect(Collectors.toList()))
                .build();
    }

}
