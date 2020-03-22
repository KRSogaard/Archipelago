package build.archipelago.packageservice.core.delegates.verifyPackageExists;

import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.packageservice.core.data.PackageData;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class VerifyPackageExistsDelegate {

    private PackageData packageData;

    public VerifyPackageExistsDelegate(PackageData packageData) {
        this.packageData = packageData;
    }

    public ImmutableList<ArchipelagoPackage> verify(final List<ArchipelagoPackage> packages) {
        Preconditions.checkNotNull(packages, "A list of packages are required");

        var missingPackages = ImmutableList.<ArchipelagoPackage>builder();
        for (ArchipelagoPackage pkg : packages) {
            log.debug("Checking if {} exists", pkg);
            if (packageData.getPackageVersionBuilds(pkg).isEmpty()) {
                log.debug("There where no builds for {} so it must not exist", pkg);
                missingPackages.add(pkg);
            }
        }
        return missingPackages.build();
    }

}
