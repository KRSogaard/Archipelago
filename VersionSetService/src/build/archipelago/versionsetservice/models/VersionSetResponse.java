package build.archipelago.versionsetservice.models;

import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.versionsetservice.core.models.Revision;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Builder
@Value
public class VersionSetResponse {
    private String name;
    private Optional<String> parent;
    private Long created;
    private Optional<String> latestRevision;
    private Optional<Long> latestRevisionCreated;
    private List<String> targets;
    private List<RevisionIdResponse> revisions;
}
