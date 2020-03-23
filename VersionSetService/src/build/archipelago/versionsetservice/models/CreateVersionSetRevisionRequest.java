package build.archipelago.versionsetservice.models;

import com.google.common.base.Preconditions;
import lombok.Data;

import java.util.List;

@Data
public class CreateVersionSetRevisionRequest {
    private List<String> packages;

    public void validate() {
        Preconditions.checkNotNull(packages, "Packages are required");
        Preconditions.checkArgument(packages.size() > 0, "A minimum of 1 package is required");
    }
}
