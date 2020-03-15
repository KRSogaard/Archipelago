package build.archipelago.packageservice.core.storage;

import build.archipelago.common.ArchipelagoBuiltPackage;

import java.io.IOException;

public interface PackageStorage {
    void upload(ArchipelagoBuiltPackage pkg, byte[] artifactBytes);
    byte[] get(ArchipelagoBuiltPackage pkg) throws IOException;
}
