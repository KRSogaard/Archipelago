package build.archipelago.versionsetservice.client.model;

import build.archipelago.common.ArchipelagoPackage;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Data
@Builder
public class VersionSet {
    private String name;
    private Instant created;
    private List<ArchipelagoPackage> targets;
    private List<Revision> revisions;
    private Optional<String> parent;
    private Optional<String> latestRevision;
    private Optional<Instant> latestRevisionCreated;

    public static class Revision {
        private String revisionId;
        private Instant created;

        public Revision(String revisionId, Instant created) {
            this.revisionId = revisionId;
            this.created = created;
        }

        public String getRevisionId() {
            return revisionId;
        }

        public Instant getCreated() {
            return created;
        }
    }
}
