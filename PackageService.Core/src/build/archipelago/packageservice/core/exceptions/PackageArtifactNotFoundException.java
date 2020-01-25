package build.archipelago.packageservice.core.exceptions;

import build.archipelago.common.PackageNameVersion;

public class PackageArtifactNotFoundException extends Exception {
    public PackageArtifactNotFoundException(PackageNameVersion nameVersion, String hash) {
        super("Artifact \"" +
                nameVersion.getConcatenated() + "\" with hash [" +
                hash + "] was not found");
    }
}
