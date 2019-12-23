package build.archipelago.kauai.models;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class GetPackageResponse {
    private String name;
    private String description;
    private Instant create;
}
