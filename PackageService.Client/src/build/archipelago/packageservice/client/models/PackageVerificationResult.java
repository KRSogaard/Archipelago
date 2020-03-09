package build.archipelago.packageservice.client.models;

import build.archipelago.common.ArchipelagoPackage;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class PackageVerificationResult<T> {
    private List<T> missingPackages;

    public boolean isValid() {
        return missingPackages == null || missingPackages.size() == 0;
    }
}
