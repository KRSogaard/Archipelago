package build.archipelago.packageservice.core.data.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class PackageDataModel {
    private String name;
    private String description;
    private Instant created;
}
