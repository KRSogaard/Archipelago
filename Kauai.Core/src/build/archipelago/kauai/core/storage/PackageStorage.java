package build.archipelago.kauai.core.storage;

import java.io.File;

public interface PackageStorage {
    void upload(String packageName, String version, String hash, byte[] artifactBytes);
}
