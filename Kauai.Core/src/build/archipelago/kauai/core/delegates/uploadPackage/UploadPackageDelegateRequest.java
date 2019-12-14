package build.archipelago.kauai.core.delegates.uploadPackage;

import build.archipelago.common.PackageNameVersion;
import lombok.Builder;
import lombok.Data;
import java.io.File;
import java.io.InputStream;

@Builder
@Data
public class UploadPackageDelegateRequest {
    private PackageNameVersion nameVersion;
    private String hash;
    private String config;
    private byte[] buildArtifact;
}
