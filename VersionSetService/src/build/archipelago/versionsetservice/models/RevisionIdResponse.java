package build.archipelago.versionsetservice.models;

import build.archipelago.versionsetservice.core.models.Revision;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Builder
@Value
public class RevisionIdResponse {
    private String revisionId;
    private Long created;

    public static RevisionIdResponse from(Revision r) {
        return RevisionIdResponse.builder()
                .revisionId(r.getRevisionId())
                .created(r.getCreated().toEpochMilli())
                .build();
    }
}
