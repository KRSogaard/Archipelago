package build.archipelago.packageservice.client.models;

import build.archipelago.common.ArchipelagoPackage;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class UploadPackageRequest {
    private ArchipelagoPackage pkg;
    private String config;
}
