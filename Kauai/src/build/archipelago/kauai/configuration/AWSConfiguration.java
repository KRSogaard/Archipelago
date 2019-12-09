package build.archipelago.kauai.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfiguration {

    @Bean
    public String serviceName(@Value("${database.packages.name}") String tableName) {
        return "beerService";
    }
}