package build.archipelago.common.exceptions;

import build.archipelago.common.ArchipelagoPackage;

public class MissingTargetPackageException extends Exception {
    public MissingTargetPackageException() {
        super("A target package was missing");
    }
    public MissingTargetPackageException(ArchipelagoPackage pkg) {
        super("This target package " + pkg.toString() +
                " was not in the build packages list, all Version Set targets is" +
                " required to be in the version set revision");
    }
}
