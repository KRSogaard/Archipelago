package build.archipelago.versionsetservice.configuration;

import build.archipelago.packageservice.client.PackageServiceClient;
import build.archipelago.packageservice.client.rest.RestPackageServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PackageClientConfiguration {

    @Bean
    public PackageServiceClient getPackageServiceClient(@Value("${endpoints.package-service}") String endpoint) {
        return new RestPackageServiceClient(endpoint);
    }

}
