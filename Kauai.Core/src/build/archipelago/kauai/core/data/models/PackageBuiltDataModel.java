package build.archipelago.kauai.core.data.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;

@Data
@AllArgsConstructor
public class PackageBuiltDataModel {
    private String name;
    private String hash;
    private Instant uploaded;
    private String config;
}
