package build.archipelago.packageservice.core.delegates.getBuildArtifact;

import build.archipelago.common.PackageNameVersion;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GetBuildArtifactResponse {
    private byte[] byteArray;
    private PackageNameVersion nameVersion;
    private String hash;
}
