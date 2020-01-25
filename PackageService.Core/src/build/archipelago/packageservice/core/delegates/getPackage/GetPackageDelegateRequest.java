package build.archipelago.packageservice.core.delegates.getPackage;

import build.archipelago.packageservice.core.utils.PackageUtil;
import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetPackageDelegateRequest {
    private String name;

    public void validate() {
        Preconditions.checkNotNull(getName(), "A name is required");
        Preconditions.checkArgument(getName().trim().length() > 0, "A name is required");
        PackageUtil.validatePackageName(getName());
    }
}
