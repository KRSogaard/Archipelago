package build.archipelago.kauai.core.exceptions;

public class PackageExistsException extends Exception {
    public PackageExistsException(String name) {
        super("A package with the name \"" + name + "\" already exists.");
    }
}
