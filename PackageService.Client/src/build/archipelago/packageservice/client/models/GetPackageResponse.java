package build.archipelago.packageservice.client.models;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Builder
@Value
public class GetPackageResponse {
    private String name;
    private String description;
    private Instant created;
}
