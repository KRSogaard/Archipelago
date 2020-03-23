package build.archipelago.packageservice.core.delegates.getPackage;

import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.common.exceptions.PackageNotFoundException;
import build.archipelago.packageservice.core.data.PackageData;
import build.archipelago.packageservice.core.data.models.PackageDetails;
import com.google.common.base.Preconditions;

public class GetPackageDelegate {

    private PackageData packageData;

    public GetPackageDelegate(PackageData packageData) {
        this.packageData = packageData;
    }

    public PackageDetails get(String name) throws PackageNotFoundException {
        Preconditions.checkNotNull(name, "A package name is required");
        Preconditions.checkArgument(ArchipelagoPackage.validateName(name),
                "The package name \"" + name + "\" was not valid");

        return packageData.getPackageDetails(name);
    }
}
