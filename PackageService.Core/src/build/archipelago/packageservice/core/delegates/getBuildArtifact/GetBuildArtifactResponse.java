package build.archipelago.packageservice.core.delegates.getBuildArtifact;

import build.archipelago.common.ArchipelagoPackage;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GetBuildArtifactResponse {
    private byte[] byteArray;
    private ArchipelagoPackage nameVersion;
    private String hash;
}
