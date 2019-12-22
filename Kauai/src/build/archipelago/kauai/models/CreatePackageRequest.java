package build.archipelago.kauai.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreatePackageRequest {
    private String name;
    private String description;
}
