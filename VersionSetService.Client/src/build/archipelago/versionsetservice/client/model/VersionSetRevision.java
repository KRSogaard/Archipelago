package build.archipelago.versionsetservice.client.model;

import build.archipelago.common.ArchipelagoBuiltPackage;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Builder
@Value
public class VersionSetRevision {
    private Instant created;
    private List<ArchipelagoBuiltPackage> packages;
}
