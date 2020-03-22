package build.archipelago.packageservice.client.rest.models;

import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class RestCreatePackageRequest {
    private String name;
    private String description;
}
