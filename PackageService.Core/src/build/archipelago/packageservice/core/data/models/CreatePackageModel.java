package build.archipelago.packageservice.core.data.models;

import build.archipelago.common.ArchipelagoPackage;
import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePackageModel {
    private String name;
    private String description;

    public void validate() {
        Preconditions.checkNotNull(name, "Name is required");
        Preconditions.checkNotNull(description, "Description is required");
        Preconditions.checkArgument(ArchipelagoPackage.validateName(name), "The package name \"" + name + "\" was not valid");
        Preconditions.checkArgument(description.trim().length() == 0, "A description is required");
    }
}
