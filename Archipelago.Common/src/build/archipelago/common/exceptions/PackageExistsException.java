package build.archipelago.common.exceptions;

import build.archipelago.common.ArchipelagoBuiltPackage;
import build.archipelago.common.ArchipelagoPackage;

public class PackageExistsException extends Exception {
    public PackageExistsException(String name) {
        super("The package \"" + name + "\" already exists");
    }

    public PackageExistsException(ArchipelagoBuiltPackage pkg) {
        super("The package \"" + pkg + "\" already exists");
    }

    public PackageExistsException(ArchipelagoPackage pkg) {
        super("The package \"" + pkg.getNameVersion() + "\" already exists");
    }
}
