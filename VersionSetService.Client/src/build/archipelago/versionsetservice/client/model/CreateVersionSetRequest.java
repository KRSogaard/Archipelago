package build.archipelago.versionsetservice.client.model;

import build.archipelago.common.ArchipelagoPackage;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Optional;

@Value
@Builder
public class CreateVersionSetRequest {
    private String name;
    private List<ArchipelagoPackage> targets;
    private Optional<String> parent;

    public void validate() throws IllegalArgumentException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Name is required");
        Preconditions.checkNotNull(targets, "At least 1 target is required");
        Preconditions.checkArgument(targets.size() > 0, "At least 1 target is required");
    }
}
