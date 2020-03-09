package build.archipelago.versionsetservice.core.models;

import build.archipelago.common.ArchipelagoPackage;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Builder
@Value
public class VersionSet {
    private String name;
    private Optional<String> parent;
    private Instant created;
    private Optional<String> latestRevision;
    private Optional<Instant> latestRevisionCreated;
    private List<ArchipelagoPackage> targets;
    private List<Revision> revisions;
}
