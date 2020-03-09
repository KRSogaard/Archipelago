package build.archipelago.versionsetservice.core.services;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class DynamoDbVersionSetServiceConfig {
    private String versionSetTable;
    private String versionSetRevisionTable;
}
