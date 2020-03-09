package build.archipelago.packageservice.core.storage;

import build.archipelago.common.ArchipelagoPackage;

import java.io.IOException;

public interface PackageStorage {
    void upload(ArchipelagoPackage nameVersion, String hash, byte[] artifactBytes);
    byte[] get(ArchipelagoPackage nameVersion, String packageHash) throws IOException;
}
