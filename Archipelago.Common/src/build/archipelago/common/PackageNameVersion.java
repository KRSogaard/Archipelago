package build.archipelago.common;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PackageNameVersion {
    private String name;
    private String version;

    public static PackageNameVersion parse(String nameAndVersion) throws NullPointerException {
        String[] nV = nameAndVersion.split("-", 1);
        if (nV.length != 2) {
            throw new IllegalArgumentException("\"" + nameAndVersion + "\" is not a valid name version");
        }
        return new PackageNameVersion(nV[0], nV[1]);
    }

    public String getConcatenated() {
        return name + "-" + version;
    }

    public void validate() throws NullPointerException {
        Preconditions.checkNotNull(name, "Name is required");
        Preconditions.checkNotNull(version, "Version is required");
    }
}
