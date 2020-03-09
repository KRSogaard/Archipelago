package build.archipelago.versionsetservice.models;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class VersionSetRevisionResponse {
    private Long created;
    private List<String> packages;
}
