package build.archipelago.common;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class ArchipelagoPackage {

    private String name;
    private String version;

    public static ArchipelagoPackage parse(String value) throws NullPointerException {
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

        this.name = name;
        this.version = version;
    }

    public String getConcatenated() {
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
}
