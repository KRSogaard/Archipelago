package build.archipelago.packageservice.models;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class GetPackageBuildResponse {
    private String hash;
    private long created;
    private String config;
}
