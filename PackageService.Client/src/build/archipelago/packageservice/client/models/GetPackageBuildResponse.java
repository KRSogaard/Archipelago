package build.archipelago.packageservice.client.models;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class GetPackageBuildResponse {
    private String hash;
    private Instant created;
    private String config;
}
