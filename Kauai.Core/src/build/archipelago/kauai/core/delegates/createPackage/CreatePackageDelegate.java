package build.archipelago.kauai.core.delegates.createPackage;

import build.archipelago.kauai.core.data.PackageData;
import build.archipelago.kauai.core.data.models.CreatePackageModel;

public class CreatePackageDelegate {

    private PackageData packageData;

    public CreatePackageDelegate(PackageData packageData) {
        this.packageData = packageData;
    }

    public void create(CreatePackageDelegateRequest request) {
        request.validate();

        packageData.createPackage(CreatePackageModel.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build());
    }
}
