package build.archipelago.packageservice.core.exceptions;

import build.archipelago.common.PackageNameVersion;

import java.util.Optional;

public class PackageNotFoundException extends Exception {
    public PackageNotFoundException(String message) {
        super("No package with the name \"" + message + "\" was found");
    }

    public PackageNotFoundException(PackageNameVersion nameVersion) {
        super("No package with the name \"" + nameVersion.getName() +
                "\" and version \"" + nameVersion.getVersion() + "\" was found");
    }

    public PackageNotFoundException(PackageNameVersion nameVersion, Optional<String> hash) {
        super("No package with the name \"" + nameVersion.getName() +
                "\" and version \"" + nameVersion.getVersion() + "\" was found. Hash [" +
                (hash.isPresent() ? hash.get() : "") + "]");
    }
}
