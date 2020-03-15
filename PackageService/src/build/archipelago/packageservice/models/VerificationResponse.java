package build.archipelago.packageservice.models;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class VerificationResponse {
    private boolean verified;
}
