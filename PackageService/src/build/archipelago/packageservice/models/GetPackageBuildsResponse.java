package build.archipelago.packageservice.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetPackageBuildsResponse {
    private List<Build> builds;

    public static class Build {
        private String hash;
        private long created;

        public Build(String hash, long created) {
            this.hash = hash;
            this.created = created;
        }

        public String getHash() {
            return hash;
        }

        public long getCreated() {
            return created;
        }
    }
}
