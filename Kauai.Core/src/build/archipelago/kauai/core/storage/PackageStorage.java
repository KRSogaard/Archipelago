package build.archipelago.kauai.core.storage;

import build.archipelago.common.PackageNameVersion;

import java.io.File;
import java.io.IOException;

public interface PackageStorage {
    void upload(PackageNameVersion nameVersion, String hash, byte[] artifactBytes);
    byte[] get(PackageNameVersion nameVersion, String packageHash) throws IOException;
}
