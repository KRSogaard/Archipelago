package build.archipelago.packageservice.core.data;

import build.archipelago.common.ArchipelagoBuiltPackage;
import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.common.exceptions.PackageExistsException;
import build.archipelago.common.exceptions.PackageNotFoundException;
import build.archipelago.packageservice.core.data.models.BuiltPackageDetails;
import build.archipelago.packageservice.core.data.models.CreatePackageModel;
import build.archipelago.packageservice.core.data.models.PackageDetails;
import build.archipelago.packageservice.core.data.models.VersionBuildDetails;
import com.google.common.collect.ImmutableList;

public interface PackageData {
    boolean buildExists(ArchipelagoBuiltPackage pkg);
    boolean packageVersionExists(ArchipelagoPackage pkg);
    boolean packageExists(String name);

    PackageDetails getPackageDetails(String name) throws PackageNotFoundException;
    ImmutableList<VersionBuildDetails> getPackageVersionBuilds(ArchipelagoPackage pkg);
    BuiltPackageDetails getBuildPackage(ArchipelagoBuiltPackage pkg) throws PackageNotFoundException;
    BuiltPackageDetails getLatestBuildPackage(ArchipelagoPackage pkg) throws PackageNotFoundException;

    void createBuild(ArchipelagoBuiltPackage pkg, String config) throws PackageNotFoundException, PackageExistsException;
    void createPackage(CreatePackageModel model) throws PackageExistsException;
}
