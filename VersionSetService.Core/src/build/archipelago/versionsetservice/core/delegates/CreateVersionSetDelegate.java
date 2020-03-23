package build.archipelago.versionsetservice.core.delegates;

import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.packageservice.client.PackageServiceClient;
import build.archipelago.packageservice.client.models.PackageVerificationResult;
import build.archipelago.common.exceptions.PackageNotFoundException;
import build.archipelago.common.exceptions.VersionSetDoseNotExistsException;
import build.archipelago.common.exceptions.VersionSetExistsException;
import build.archipelago.versionsetservice.core.services.VersionSetService;
import build.archipelago.versionsetservice.core.utils.NameUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.util.List;
import java.util.Optional;

public class CreateVersionSetDelegate {

    private VersionSetService versionSetService;
    private PackageServiceClient packageServiceClient;

    public CreateVersionSetDelegate(VersionSetService versionSetService,
                                    PackageServiceClient packageServiceClient) {
        this.versionSetService = versionSetService;
        this.packageServiceClient = packageServiceClient;
    }

    public void create(String name, List<ArchipelagoPackage> targets, Optional<String> parent)
            throws VersionSetExistsException, VersionSetDoseNotExistsException, PackageNotFoundException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Name is required");
        Preconditions.checkArgument(NameUtil.validateVersionSetName(name), "Version set name was invalid");
        Preconditions.checkNotNull(targets, "At least 1 target is required");
        Preconditions.checkArgument(targets.size() > 0, "At least 1 target is required");

        if (versionSetService.get(name) != null) {
            throw new VersionSetExistsException(name);
        }

        PackageVerificationResult<ArchipelagoPackage> targetsVerified = packageServiceClient.verifyPackagesExists(targets);
        if (!targetsVerified.isValid()) {
            throw new PackageNotFoundException(targets);
        }

        if (parent.isPresent()) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(parent.get()),
                    "Parent is required");
            Preconditions.checkArgument(NameUtil.validateVersionSetName(parent.get()),
                    "Parent name was not valid");

            // If the parent version set dose not exists this will throw an exception
            versionSetService.get(parent.get());
        }

        versionSetService.create(name, targets, parent);
    }
}
