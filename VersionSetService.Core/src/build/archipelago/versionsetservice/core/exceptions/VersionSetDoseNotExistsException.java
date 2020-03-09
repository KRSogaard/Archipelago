package build.archipelago.versionsetservice.core.exceptions;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;

import java.util.Optional;

public class VersionSetDoseNotExistsException extends Throwable {
    public VersionSetDoseNotExistsException(String name) {
        super(getMessage(name));
    }
    public VersionSetDoseNotExistsException(String name, Exception exp) {
        super(getMessage(name), exp);
    }
    public VersionSetDoseNotExistsException(String name, String reversion) {
        super(getMessage(name, reversion));
    }
    public VersionSetDoseNotExistsException(String name, String reversion, Exception exp) {
        super(getMessage(name, reversion), exp);
    }
    private static String getMessage(String name) {
        return "Version Set " + name + " dose not exist";
    }
    private static String getMessage(String name, String reversion) {
        return getMessage(name + ":" + reversion);
    }
}
