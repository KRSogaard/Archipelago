package build.archipelago.common;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PackageNameVersion {
    private String name;
    private String version;

    public String getNameVersion() {
        return name + "-" + version;
    }

    public void validate() throws NullPointerException {
        Preconditions.checkNotNull(name, "Name is required");
        Preconditions.checkNotNull(version, "Version is required");
    }
}
