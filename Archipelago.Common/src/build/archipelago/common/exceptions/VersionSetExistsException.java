package build.archipelago.common.exceptions;

public class VersionSetExistsException extends Throwable {
    public VersionSetExistsException(String name) {
        super("Version Set " + name + " already exists");
    }
}
