package build.archipelago.kauai.configuration;

import build.archipelago.kauai.core.data.DynamoDBPackageConfig;
import build.archipelago.kauai.core.data.DynamoDBPackageData;
import build.archipelago.kauai.core.data.PackageData;
import build.archipelago.kauai.core.storage.PackageStorage;
import build.archipelago.kauai.core.storage.S3PackageStorage;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.s3.AmazonS3;
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
            @Value("${dynamodb.packages_builds.name}") String packageBuildsTable,
            @Value("${dynamodb.packages_latest.name}") String packageLatestTable,
            AmazonDynamoDB dynamoDB) {

        DynamoDBPackageConfig config = DynamoDBPackageConfig.builder()
                                            .packagesTableName(packageTable)
                                            .packagesBuildsTableName(packageBuildsTable)
                                            .packagesLatestTableName(packageLatestTable)
                                            .build();

        log.info("Creating DynamoDBPackageData with config \"{}\"",
                config.toString());
        return new DynamoDBPackageData(dynamoDB, config);
    }
}
