package build.archipelago.kauai.core.data;

import build.archipelago.common.PackageNameVersion;
import build.archipelago.kauai.core.data.models.CreatePackageModel;
import build.archipelago.kauai.core.data.models.PackageBuiltDataModel;
import build.archipelago.kauai.core.data.models.PackageDataModel;

import java.util.Optional;

public interface PackageData {
    boolean buildExists(PackageNameVersion nameVersion, String hash);
    boolean buildExists(PackageNameVersion nameVersion);
    boolean packageExists(String name);
    Optional<PackageBuiltDataModel> getBuild(PackageNameVersion nameVersion, String hash);
    Optional<PackageBuiltDataModel> getBuild(PackageNameVersion nameVersion);
    Optional<PackageDataModel> getPackage(String name);
    void createBuild(PackageNameVersion nameVersion, String hash, String config);
    Optional<String> getLatestHash(PackageNameVersion nameVersion);
    void createPackage(CreatePackageModel model);
}
