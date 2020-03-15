package build.archipelago.packageservice.client.models;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Value;
import java.time.Instant;

@Builder
@Value
public class GetPackageResponse {
    private String name;
    private String description;
    private Instant created;
    private ImmutableList<Version> versions;

    public static class Version {
        private String version;
        private String latestBuildHash;
        private Instant latestBuildTime;
    }
}
