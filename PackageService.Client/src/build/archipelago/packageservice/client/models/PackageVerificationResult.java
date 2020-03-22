package build.archipelago.packageservice.client.models;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class PackageVerificationResult<T> {
    private ImmutableList<T> missingPackages;

    public boolean isValid() {
        return missingPackages == null || missingPackages.size() == 0;
    }
}
