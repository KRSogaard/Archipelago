package build.archipelago.packageservice.models;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class GetPackageResponse {
    private String name;
    private String description;
    private long created;
    private List<Version> versions;

    public static class Version {
        private String version;
        private String latestBuildHash;
        private long latestBuildTime;

        public Version(String version, String latestBuildHash, long latestBuildTime) {
            this.version = version;
            this.latestBuildHash = latestBuildHash;
            this.latestBuildTime = latestBuildTime;
        }

        public String getVersion() {
            return version;
        }

        public String getLatestBuildHash() {
            return latestBuildHash;
        }

        public long getLatestBuildTime() {
            return latestBuildTime;
        }
    }
}
