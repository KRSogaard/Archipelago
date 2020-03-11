package build.archipelago.versionsetservice.configuration;

import build.archipelago.packageservice.client.PackageServiceClient;
import build.archipelago.versionsetservice.core.delegates.CreateVersionSetDelegate;
import build.archipelago.versionsetservice.core.delegates.CreateVersionSetRevisionDelegate;
import build.archipelago.versionsetservice.core.delegates.GetVersionSetDelegate;
import build.archipelago.versionsetservice.core.delegates.GetVersionSetPackagesDelegate;
import build.archipelago.versionsetservice.core.services.DynamoDbVersionSetService;
import build.archipelago.versionsetservice.core.services.DynamoDbVersionSetServiceConfig;
import build.archipelago.versionsetservice.core.services.VersionSetService;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class VersionSetConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public VersionSetService getVersionSetService(AmazonDynamoDB amazonDynamoDB,
                              @Value("${dynamodb.versionsets.name}") String versionSetTable,
                              @Value("${dynamodb.versionsets.revisions.name}") String versionSetRevisionsTable) {
        return new DynamoDbVersionSetService(amazonDynamoDB, DynamoDbVersionSetServiceConfig.builder()
                .versionSetTable(versionSetTable)
                .versionSetRevisionTable(versionSetRevisionsTable)
                .build());
    }


    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public CreateVersionSetDelegate createVersionSetDelegate(VersionSetService versionSetService,
                                                             PackageServiceClient packageServiceClient) {
        return new CreateVersionSetDelegate(versionSetService, packageServiceClient);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public CreateVersionSetRevisionDelegate createVersionSetRevisionDelegate(VersionSetService versionSetService,
                                                                             PackageServiceClient packageServiceClient) {
        return new CreateVersionSetRevisionDelegate(versionSetService, packageServiceClient);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public GetVersionSetDelegate getVersionSetDelegate(VersionSetService versionSetService) {
        return new GetVersionSetDelegate(versionSetService);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public GetVersionSetPackagesDelegate getVersionSetPackagesDelegate(VersionSetService versionSetService) {
        return new GetVersionSetPackagesDelegate(versionSetService);
    }

}
