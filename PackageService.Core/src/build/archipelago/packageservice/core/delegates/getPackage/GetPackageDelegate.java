package build.archipelago.packageservice.core.delegates.getPackage;

import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.packageservice.core.data.PackageData;
import build.archipelago.packageservice.core.data.models.PackageDetails;
import com.google.common.base.Preconditions;

import java.util.Optional;

public class GetPackageDelegate {

    private PackageData packageData;

    public GetPackageDelegate(PackageData packageData) {
        this.packageData = packageData;
    }

    public Optional<GetPackageDelegateResponse> get(String name) {
        Preconditions.checkNotNull(name, "A package name is required");
        Preconditions.checkArgument(ArchipelagoPackage.validateName(name),
                "The package name \"" + name + "\" was not valid");

        Optional<PackageDetails> pkg = packageData.getPackage(name);
        if (!pkg.isPresent()) {
            return Optional.empty();
        }
        PackageDetails pdm = pkg.get();
        return Optional.of(GetPackageDelegateResponse.builder()
                .name(pdm.getName())
                .description(pdm.getDescription())
                .create(pdm.getCreated())
                .build());
    }
}
