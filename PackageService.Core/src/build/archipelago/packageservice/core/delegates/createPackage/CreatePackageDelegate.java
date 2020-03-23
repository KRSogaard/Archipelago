package build.archipelago.packageservice.core.delegates.createPackage;

import build.archipelago.common.exceptions.PackageExistsException;
import build.archipelago.packageservice.core.data.PackageData;
import build.archipelago.packageservice.core.data.models.CreatePackageModel;

public class CreatePackageDelegate {

    private PackageData packageData;

    public CreatePackageDelegate(PackageData packageData) {
        this.packageData = packageData;
    }

    public void create(CreatePackageDelegateRequest request) throws PackageExistsException {
        request.validate();

        packageData.createPackage(CreatePackageModel.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build());
    }
}
