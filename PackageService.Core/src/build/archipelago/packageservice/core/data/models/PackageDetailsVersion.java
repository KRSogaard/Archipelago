package build.archipelago.packageservice.core.data.models;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class PackageDetailsVersion {
    private String version;
    private String latestBuildHash;
    private Instant latestBuildTime;
}
