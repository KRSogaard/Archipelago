package build.archipelago.kauai.core.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;

@Data
@AllArgsConstructor
public class PackageDataModel {
    private String name;
    private String hash;
    private Instant uploaded;
    private String config;
}
