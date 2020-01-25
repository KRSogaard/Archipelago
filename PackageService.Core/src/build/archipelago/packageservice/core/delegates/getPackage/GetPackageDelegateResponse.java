package build.archipelago.packageservice.core.delegates.getPackage;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class GetPackageDelegateResponse {
    private String name;
    private String description;
    private Instant create;
}
