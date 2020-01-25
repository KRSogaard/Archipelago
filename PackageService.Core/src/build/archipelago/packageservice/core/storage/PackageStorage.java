package build.archipelago.packageservice.core.storage;

import build.archipelago.common.PackageNameVersion;

import java.io.IOException;

public interface PackageStorage {
    void upload(PackageNameVersion nameVersion, String hash, byte[] artifactBytes);
    byte[] get(PackageNameVersion nameVersion, String packageHash) throws IOException;
}
