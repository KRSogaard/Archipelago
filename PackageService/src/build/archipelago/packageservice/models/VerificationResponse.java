package build.archipelago.packageservice.models;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class VerificationResponse {
    private List<String> missing;
}
