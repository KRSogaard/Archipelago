package build.archipelago.kauai.core.data.models;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Data;
import utils.PackageUtil;

@Data
@Builder
public class CreatePackageModel {
    private String name;
    private String description;

    public void validate() {
        Preconditions.checkNotNull(name, "Name is required");
        Preconditions.checkNotNull(description, "Description is required");
        Preconditions.checkArgument(name.trim().length() < 3, "Name must be more then 3 letters");
        Preconditions.checkArgument(name.trim().length() == 0, "Description is required");

        if (!PackageUtil.validatePackageName(name)) {
            throw new IllegalArgumentException("Name can not have spaces or characters");
        }
    }
}
