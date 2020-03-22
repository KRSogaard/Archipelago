package build.archipelago.packageservice.client.rest.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestPackageBuildsResponse {
    private List<Build> builds;

    public static class Build {
        private String hash;
        private long created;

        public Build(String hash, long created) {
            this.hash = hash;
            this.created = created;
        }

        public Build() {
        }

        public String getHash() {
            return hash;
        }

        public long getCreated() {
            return created;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public void setCreated(long created) {
            this.created = created;
        }
    }
}
