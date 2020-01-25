package build.archipelago.packageservice.core.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DynamoDBPackageConfig {
    private String packagesTableName;
    private String packagesBuildsTableName;
    private String packagesLatestTableName;

    @Override
    public String toString() {
        return String.format("[package table: \"%s\", builds table: \"%s\", latest table: \"%s\"]",
                packagesTableName, packagesBuildsTableName, packagesLatestTableName);
    }
}
