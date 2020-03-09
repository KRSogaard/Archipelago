package build.archipelago.packageservice.client.models;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class CreatePackageRequest {
    private String name;
    private String description;
}
