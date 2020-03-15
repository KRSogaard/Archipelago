package build.archipelago.packageservice.core.delegates.uploadBuildArtifact;

import build.archipelago.common.ArchipelagoPackage;
import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UploadBuildArtifactDelegateRequest {
    private ArchipelagoPackage pkg;
    private String config;
    private byte[] buildArtifact;

    protected void validate() {
        Preconditions.checkNotNull(pkg, "Name required");
        Preconditions.checkNotNull(config, "Config required");
        Preconditions.checkNotNull(buildArtifact, "Build artifact required");
    }
}
