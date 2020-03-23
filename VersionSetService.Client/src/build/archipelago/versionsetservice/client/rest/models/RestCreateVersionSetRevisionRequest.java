package build.archipelago.versionsetservice.client.rest.models;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@AllArgsConstructor
@Value
public class RestCreateVersionSetRevisionRequest {
    private List<String> packages;
}
