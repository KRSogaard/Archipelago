package build.archipelago.packageservice.core.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DynamoDBPackageConfig {
    private String packagesTableName;
    private String packagesVersionsTableName;
    private String packagesBuildsTableName;

    @Override
    public String toString() {
        return "DynamoDBPackageConfig{" +
                "packagesTableName='" + packagesTableName + '\'' +
                ", packagesVersionsTableName='" + packagesVersionsTableName + '\'' +
                ", packagesBuildsTableName='" + packagesBuildsTableName + '\'' +
                '}';
    }
}
