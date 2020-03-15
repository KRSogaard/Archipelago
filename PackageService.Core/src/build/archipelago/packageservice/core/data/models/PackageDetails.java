package build.archipelago.packageservice.core.data.models;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class PackageDetails {
    private String name;
    private String description;
    private Instant created;
    private ImmutableList<PackageDetailsVersion> versions;
}
