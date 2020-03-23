package build.archipelago.versionsetservice.client.rest.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestVersionSetResponse {
    private String name;
    private Optional<String> parent;
    private Long created;
    private Optional<String> latestRevision;
    private Optional<Long> latestRevisionCreated;
    private List<String> targets;
    private List<RestRevisionIdResponse> revisions;
}
