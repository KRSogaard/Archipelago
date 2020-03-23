package build.archipelago.packageservice.client;

import build.archipelago.common.ArchipelagoBuiltPackage;
import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.packageservice.client.models.CreatePackageRequest;
import build.archipelago.packageservice.client.models.GetPackageBuildResponse;
import build.archipelago.packageservice.client.models.GetPackageResponse;
import build.archipelago.packageservice.client.models.PackageBuildsResponse;
import build.archipelago.packageservice.client.models.PackageVerificationResult;
import build.archipelago.packageservice.client.models.UploadPackageRequest;
import build.archipelago.common.exceptions.PackageExistsException;
import build.archipelago.common.exceptions.PackageNotFoundException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface PackageServiceClient {
    void createPackage(CreatePackageRequest request) throws PackageExistsException;

    GetPackageResponse getPackage(String name) throws PackageNotFoundException;
    PackageBuildsResponse getPackageBuilds(ArchipelagoPackage pks) throws PackageNotFoundException;
    GetPackageBuildResponse getPackageBuild(ArchipelagoBuiltPackage pkg) throws PackageNotFoundException;

    PackageVerificationResult<ArchipelagoPackage> verifyPackagesExists(List<ArchipelagoPackage> packages);
    PackageVerificationResult<ArchipelagoBuiltPackage> verifyBuildsExists(List<ArchipelagoBuiltPackage> packages);

    String uploadBuiltArtifact(UploadPackageRequest request, Path file) throws PackageNotFoundException;
    Path getBuildArtifact(ArchipelagoBuiltPackage pkg, Path directory) throws PackageNotFoundException, IOException;
}
