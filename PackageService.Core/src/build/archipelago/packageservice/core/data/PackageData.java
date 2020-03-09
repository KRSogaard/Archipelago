package build.archipelago.packageservice.core.data;

import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.packageservice.core.data.models.CreatePackageModel;
import build.archipelago.packageservice.core.data.models.PackageBuiltDataModel;
import build.archipelago.packageservice.core.data.models.PackageDataModel;

import java.util.Optional;

public interface PackageData {
    boolean buildExists(ArchipelagoPackage nameVersion, String hash);
    boolean buildExists(ArchipelagoPackage nameVersion);
    boolean packageExists(String name);
    Optional<PackageBuiltDataModel> getBuild(ArchipelagoPackage nameVersion, String hash);
    Optional<PackageBuiltDataModel> getBuild(ArchipelagoPackage nameVersion);
    Optional<PackageDataModel> getPackage(String name);
    void createBuild(ArchipelagoPackage nameVersion, String hash, String config);
    Optional<String> getLatestHash(ArchipelagoPackage nameVersion);
    void createPackage(CreatePackageModel model);
}
