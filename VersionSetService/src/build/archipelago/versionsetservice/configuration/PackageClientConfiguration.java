package build.archipelago.versionsetservice.configuration;

import build.archipelago.common.ArchipelagoBuiltPackage;
import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.packageservice.client.PackageServiceClient;
import build.archipelago.packageservice.client.models.CreatePackageRequest;
import build.archipelago.packageservice.client.models.GetPackageBuildResponse;
import build.archipelago.packageservice.client.models.GetPackageResponse;
import build.archipelago.packageservice.client.models.PackageBuildsResponse;
import build.archipelago.packageservice.client.models.PackageVerificationResult;
import build.archipelago.packageservice.client.models.UploadPackageRequest;
import build.archipelago.packageservice.common.exceptions.PackageArtifactExistsException;
import build.archipelago.packageservice.common.exceptions.PackageExistsException;
import build.archipelago.packageservice.common.exceptions.PackageNotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class PackageClientConfiguration {

    @Bean
    public PackageServiceClient getPackageServiceClient() {
        return new PackageServiceClient() {
            @Override
            public void createPackage(CreatePackageRequest request) throws PackageExistsException {

            }

            @Override
            public GetPackageResponse getPackage(String name) throws PackageNotFoundException {
                return null;
            }

            @Override
            public PackageBuildsResponse getPackageBuilds(ArchipelagoPackage pks) throws PackageNotFoundException {
                return null;
            }

            @Override
            public GetPackageBuildResponse getPackageBuild(ArchipelagoBuiltPackage pkg) throws PackageNotFoundException {
                return null;
            }

            @Override
            public void uploadBuiltArtifact(UploadPackageRequest request, Path file) throws PackageArtifactExistsException, PackageNotFoundException {

            }

            @Override
            public Path getBuildArtifact(ArchipelagoPackage pkg) throws PackageNotFoundException {
                return null;
            }

            @Override
            public PackageVerificationResult<ArchipelagoPackage> verifyPackagesExists(List<ArchipelagoPackage> packages) {
                return PackageVerificationResult.<ArchipelagoPackage>builder()
                        .missingPackages(new ArrayList<>())
                        .build();
            }

            @Override
            public PackageVerificationResult<ArchipelagoBuiltPackage> verifyBuildsExists(List<ArchipelagoBuiltPackage> packages) {
                return PackageVerificationResult.<ArchipelagoBuiltPackage>builder()
                        .missingPackages(new ArrayList<>())
                        .build();
            }
        };
    }

}
