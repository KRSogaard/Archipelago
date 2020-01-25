package build.archipelago.packageservice.core.delegates.getPackage;

import build.archipelago.packageservice.core.data.PackageData;
import build.archipelago.packageservice.core.data.models.PackageDataModel;

import java.util.Optional;

public class GetPackageDelegate {

    private PackageData packageData;

    public GetPackageDelegate(PackageData packageData) {
        this.packageData = packageData;
    }

    public Optional<GetPackageDelegateResponse> get(GetPackageDelegateRequest model) {
        model.validate();
        Optional<PackageDataModel> pkg = packageData.getPackage(model.getName());
        if (!pkg.isPresent()) {
            return Optional.empty();
        }
        PackageDataModel pdm = pkg.get();
        return Optional.of(GetPackageDelegateResponse.builder()
                .name(pdm.getName())
                .description(pdm.getDescription())
                .create(pdm.getCreated())
                .build());
    }
}
