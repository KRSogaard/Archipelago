package build.archipelago.packageservice.client.models;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class UploadPackageRequest {
    private String name;
    private String version;
    private String hash;
    private String config;
}
