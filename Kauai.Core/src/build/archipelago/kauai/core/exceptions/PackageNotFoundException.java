package build.archipelago.kauai.core.exceptions;

import build.archipelago.common.PackageNameVersion;

public class PackageNotFoundException extends Exception {
    public PackageNotFoundException(String message) {
        super("No package with the name \"" + message + "\" was found");
    }
    public PackageNotFoundException(PackageNameVersion nameVersion) {
        super("No package with the name \"" + nameVersion.getName() +
                "\" and version \"" + nameVersion.getVersion() + "\" was found");
    }
}
