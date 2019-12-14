package build.archipelago.kauai.core.data;

import build.archipelago.common.PackageNameVersion;

import java.util.Optional;

public interface PackageData {
    boolean exists(PackageNameVersion nameVersion, String hash);
    boolean exists(PackageNameVersion nameVersion);
    Optional<PackageDataModel> get(PackageNameVersion nameVersion, String hash);
    Optional<PackageDataModel> get(PackageNameVersion nameVersion);
    void create(PackageNameVersion nameVersion, String hash, String config);
}
