package build.archipelago.packageservice.configuration;

import build.archipelago.packageservice.core.data.DynamoDBPackageConfig;
import build.archipelago.packageservice.core.data.DynamoDBPackageData;
import build.archipelago.packageservice.core.data.PackageData;
import build.archipelago.packageservice.core.storage.PackageStorage;
import build.archipelago.packageservice.core.storage.S3PackageStorage;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.s3.AmazonS3;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@Slf4j
public class ServiceConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public PackageStorage packageStorage(AmazonS3 amazonS3,
                                         @Value("${s3.packages.name}") String bucketName) {
        log.info("Creating S3PackageStorage using bucket \"{}\"",
                bucketName);
        return new S3PackageStorage(amazonS3, bucketName);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public PackageData packageData(
            @Value("${dynamodb.packages.name}") String packageTable,
            @Value("${dynamodb.packages_versions.name}") String packageVersionsTable,
            @Value("${dynamodb.packages_builds.name}") String packageBuildsTable,
            AmazonDynamoDB dynamoDB) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(packageTable));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(packageVersionsTable));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(packageBuildsTable));

        DynamoDBPackageConfig config = DynamoDBPackageConfig.builder()
                .packagesTableName(packageTable)
                .packagesVersionsTableName(packageVersionsTable)
                .packagesBuildsTableName(packageBuildsTable)
                .build();

        log.info("Creating DynamoDBPackageData with config \"{}\"",
                config.toString());
        return new DynamoDBPackageData(dynamoDB, config);
    }
}
