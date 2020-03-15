package build.archipelago.common;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class ArchipelagoBuiltPackage extends ArchipelagoPackage {
    private String hash;

    public void setHash(String hash) {
        this.hash = hash;
    }
    public String getHash() {
        return hash;
    }

    public static ArchipelagoBuiltPackage parse(String value) throws NullPointerException {
        String[] hashSplit = value.split(":", 2);
        if (hashSplit.length != 2) {
            throw new IllegalArgumentException(String.format("The string \"%s\" was not a valid built package", value));
        }
        String hash = hashSplit[1];
        return new ArchipelagoBuiltPackage(ArchipelagoPackage.parse(hashSplit[0]), hash);
    }

    public ArchipelagoBuiltPackage(String name, String version, String hash) {
        super(name, version);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(hash), "Build hash is required");
        this.hash = hash;
    }

    public ArchipelagoBuiltPackage(ArchipelagoPackage pkg, String hash) {
        this(pkg.getName(), pkg.getVersion(), hash);
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean omitHash) {
        if (!omitHash) {
            return String.format("%s:%s", super.toString(), hash);
        }
        return super.toString();
    }
}
