package build.archipelago.packageservice.configuration;

import build.archipelago.packageservice.core.data.PackageData;
import build.archipelago.packageservice.core.delegates.createPackage.CreatePackageDelegate;
import build.archipelago.packageservice.core.delegates.getBuildArtifact.GetBuildArtifactDelegate;
import build.archipelago.packageservice.core.delegates.uploadBuildArtifact.UploadBuildArtifactDelegate;
import build.archipelago.packageservice.core.storage.PackageStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@Slf4j
public class DelegateConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public UploadBuildArtifactDelegate uploadPackageDelegate(
            PackageData packageData,
            PackageStorage packageStorage) {
        return new UploadBuildArtifactDelegate(packageData, packageStorage);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public GetBuildArtifactDelegate getBuildArtifactDelegate(
            PackageData packageData,
            PackageStorage packageStorage) {
        return new GetBuildArtifactDelegate(packageData, packageStorage);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public CreatePackageDelegate createPackageDelegate(
            PackageData packageData) {
        return new CreatePackageDelegate(packageData);
    }
}
