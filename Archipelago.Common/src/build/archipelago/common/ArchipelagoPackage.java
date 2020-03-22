package build.archipelago.common;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.regex.Pattern;

public class ArchipelagoPackage {

    private final static Pattern VERSION_PATTERN = Pattern.compile("^[^:]+$");
    private final static Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z0-9-_]+$");

    private String name;
    private String version;

    public static ArchipelagoPackage parse(String value) {
        int i = value.lastIndexOf('-');
        if (i == -1) {
            throw new IllegalArgumentException("\"" + value + "\" is not a valid name version");
        }
        String pkgName = value.substring(0, i);
        String version = (i == value.length()-1) ? null : value.substring(i + 1);
        return new ArchipelagoPackage(pkgName, version);
    }

    public ArchipelagoPackage(String name, String version) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Name is required");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(version), "Version is required");
        Preconditions.checkArgument(validateName(name), "The name \"" + name + "\" was not valid");
        Preconditions.checkArgument(validateVersion(version), "The version \"" + version + "\" was not valid");

        this.name = name;
        this.version = version;
    }

    @Override
    public String toString() {
        return getNameVersion();
    }

    public String getNameVersion() {
        return String.format("%s-%s", name, version);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static boolean validateName(String name) {
        return NAME_PATTERN.matcher(name).find();
    }
    public static boolean validateVersion(String version) {
        return VERSION_PATTERN.matcher(version).find();
    }
}
