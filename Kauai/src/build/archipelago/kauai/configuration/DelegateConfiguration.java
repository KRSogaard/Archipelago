package build.archipelago.kauai.configuration;

import build.archipelago.kauai.core.data.PackageData;
import build.archipelago.kauai.core.delegates.uploadPackage.UploadPackageDelegate;
import build.archipelago.kauai.core.storage.PackageStorage;
import build.archipelago.kauai.core.storage.S3PackageStorage;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class DelegateConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public UploadPackageDelegate uploadPackageDelegate(
            PackageData packageData,
            PackageStorage packageStorage) {
        return new UploadPackageDelegate(packageData, packageStorage);
    }
}
