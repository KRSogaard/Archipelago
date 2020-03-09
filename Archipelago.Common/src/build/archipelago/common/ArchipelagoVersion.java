package build.archipelago.common;

import com.google.common.base.Strings;

public class ArchipelagoVersion {

    private String major;
    private String minor;

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    @Override
    public String toString() {
        if (Strings.isNullOrEmpty(minor)) {
            return major;
        }

        return String.format("%s.%s", major, minor);
    }

    public static ArchipelagoVersion parse(String value) throws NullPointerException {
        ArchipelagoVersion version = new ArchipelagoVersion();
        String[] versionSplit = value.split(".", 2);
        if (versionSplit.length > 1) {
            version.setMinor(versionSplit[1]);
        }
        version.setMajor(versionSplit[0]);
        return version;
    }
}
