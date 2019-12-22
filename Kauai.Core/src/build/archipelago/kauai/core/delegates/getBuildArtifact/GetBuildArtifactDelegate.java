package build.archipelago.kauai.core.delegates.getBuildArtifact;

import build.archipelago.common.PackageNameVersion;
import build.archipelago.kauai.core.data.PackageData;
import build.archipelago.kauai.core.data.models.PackageDataModel;
import build.archipelago.kauai.core.exceptions.PackageArtifactNotFoundException;
import build.archipelago.kauai.core.exceptions.PackageNotFoundException;
import build.archipelago.kauai.core.storage.PackageStorage;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import utils.Constants;

import java.io.IOException;
import java.util.Optional;

@Slf4j
public class GetBuildArtifactDelegate {

    private PackageStorage packageStorage;
    private PackageData packageData;

    public GetBuildArtifactDelegate(PackageData packageData,
                                 PackageStorage packageStorage) {
        this.packageData = packageData;
        this.packageStorage = packageStorage;
    }

    public GetBuildArtifactResponse getBuildArtifact(PackageNameVersion nameVersion, Optional<String> hash)
            throws PackageNotFoundException, IOException, PackageArtifactNotFoundException {
        Preconditions.checkNotNull(nameVersion, "Name required");
        nameVersion.validate();
        Preconditions.checkNotNull(hash, "A hash is required");

        Optional<String> latestHash = Optional.empty();
        if (!hash.isPresent() || Constants.LATEST.equalsIgnoreCase(hash.get())) {
            latestHash = packageData.getLatestHash(nameVersion);
            if (!latestHash.isPresent()) {
                throw new PackageNotFoundException(nameVersion);
            }
        }
        final String packageHash = latestHash.orElse(hash.get()).toLowerCase();

        if (!packageData.buildExists(nameVersion, packageHash)) {
            throw new PackageArtifactNotFoundException(nameVersion, packageHash);
        }

        Optional<PackageDataModel> pkg = packageData.getPackage(nameVersion.getName());
        if (!pkg.isPresent()) {
            log.error("Was unable to find the package");
        }

        return GetBuildArtifactResponse.builder()
                .byteArray(packageStorage.get(nameVersion, packageHash))
                .nameVersion(new PackageNameVersion(pkg.get().getName(), nameVersion.getVersion()))
                .hash(packageHash)
                .build();
    }
}
