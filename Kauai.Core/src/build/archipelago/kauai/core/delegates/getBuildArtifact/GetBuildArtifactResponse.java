package build.archipelago.kauai.core.delegates.getBuildArtifact;

import build.archipelago.common.PackageNameVersion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GetBuildArtifactResponse {
    private byte[] byteArray;
    private PackageNameVersion nameVersion;
    private String hash;
}
