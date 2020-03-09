package build.archipelago.versionsetservice.core.exceptions;

import build.archipelago.common.ArchipelagoPackage;

public class MissingTargetPackageException extends Exception {
    public MissingTargetPackageException(ArchipelagoPackage pkg) {
        super("This target package " + pkg.getConcatenated() +
                " was not in the build packages list, all Version Set targets is" +
                " required to be in the version set revision");
    }
}
