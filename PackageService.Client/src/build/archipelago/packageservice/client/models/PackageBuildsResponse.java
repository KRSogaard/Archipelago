package build.archipelago.packageservice.client.models;

import com.google.common.collect.ImmutableList;
import lombok.Builder;

import java.time.Instant;

@Builder
public class PackageBuildsResponse {
    private ImmutableList<Build> builds;

    public ImmutableList<Build> getBuilds() {
        return builds;
    }

    public static class Build {
        private String hash;
        private Instant created;

        public Build(String hash, Instant created) {
            this.hash = hash;
            this.created = created;
        }

        public String getHash() {
            return hash;
        }

        public Instant getCreated() {
            return created;
        }
    }
}
