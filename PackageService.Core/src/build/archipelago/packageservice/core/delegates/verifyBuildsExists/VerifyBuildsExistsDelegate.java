package build.archipelago.packageservice.core.delegates.verifyBuildsExists;

import build.archipelago.common.ArchipelagoBuiltPackage;
import build.archipelago.common.exceptions.PackageNotFoundException;
import build.archipelago.packageservice.core.data.PackageData;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class VerifyBuildsExistsDelegate {

    private PackageData packageData;

    public VerifyBuildsExistsDelegate(PackageData packageData) {
        this.packageData = packageData;
    }

    public ImmutableList<ArchipelagoBuiltPackage> verify(List<ArchipelagoBuiltPackage> packages) {
        Preconditions.checkNotNull(packages);

        var missingPackages = ImmutableList.<ArchipelagoBuiltPackage>builder();
        for (ArchipelagoBuiltPackage pkg : packages) {
            try {
                log.debug("Checking if {} exists", pkg);
                packageData.getBuildPackage(pkg);
            } catch (PackageNotFoundException e) {
                log.debug("Got not found exception, {}", e.getMessage(), e);
                missingPackages.add(pkg);
            }
        }
        return missingPackages.build();
    }
}
