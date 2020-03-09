package build.archipelago.packageservice.core.delegates.uploadBuildArtifact;

import build.archipelago.common.ArchipelagoPackage;
import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Data;
import build.archipelago.packageservice.core.utils.Constants;

@Builder
@Data
public class UploadBuildArtifactDelegateRequest {
    private ArchipelagoPackage nameVersion;
    private String hash;
    private String config;
    private byte[] buildArtifact;

    protected void validate() {
        Preconditions.checkNotNull(nameVersion, "Name required");
        getNameVersion().validate();
        Preconditions.checkNotNull(hash, "Hash required");
        Preconditions.checkArgument(
                !Constants.LATEST.equalsIgnoreCase(hash),
                "Hash can not be \"" + Constants.LATEST + "\"");
        Preconditions.checkNotNull(config, "Config required");
        Preconditions.checkNotNull(buildArtifact, "Build artifact required");
    }
}
