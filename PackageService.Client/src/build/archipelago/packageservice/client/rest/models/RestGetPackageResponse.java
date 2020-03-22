package build.archipelago.packageservice.client.rest.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestGetPackageResponse {
    private String name;
    private String description;
    private long created;
    private List<Version> versions;

    public static class Version {
        private String version;
        private String latestBuildHash;
        private long latestBuildTime;

        public Version() {
        }

        public Version(String version, String latestBuildHash, long latestBuildTime) {
            this.version = version;
            this.latestBuildHash = latestBuildHash;
            this.latestBuildTime = latestBuildTime;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getLatestBuildHash() {
            return latestBuildHash;
        }

        public void setLatestBuildHash(String latestBuildHash) {
            this.latestBuildHash = latestBuildHash;
        }

        public long getLatestBuildTime() {
            return latestBuildTime;
        }

        public void setLatestBuildTime(long latestBuildTime) {
            this.latestBuildTime = latestBuildTime;
        }
    }
}
