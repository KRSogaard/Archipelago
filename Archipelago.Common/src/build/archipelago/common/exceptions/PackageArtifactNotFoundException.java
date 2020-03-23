package build.archipelago.common.exceptions;

import build.archipelago.common.ArchipelagoPackage;

public class PackageArtifactNotFoundException extends Exception {
    public PackageArtifactNotFoundException(ArchipelagoPackage nameVersion, String hash) {
        super("Artifact \"" +
                nameVersion.toString() + "\" with hash [" +
                hash + "] was not found");
    }
}
