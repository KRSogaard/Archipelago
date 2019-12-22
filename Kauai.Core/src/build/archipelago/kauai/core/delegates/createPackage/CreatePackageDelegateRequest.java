package build.archipelago.kauai.core.delegates.createPackage;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Data;
import utils.Constants;

@Data
@Builder
public class CreatePackageDelegateRequest {
    private String name;
    private String description;

    protected void validate() {
        Preconditions.checkNotNull(name, "Name required");
        Preconditions.checkNotNull(description, "Hash required");
    }
}
