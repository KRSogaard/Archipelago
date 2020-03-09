package build.archipelago.packageservice.core.delegates.createPackage;

import build.archipelago.packageservice.core.data.PackageData;
import build.archipelago.packageservice.core.data.models.CreatePackageModel;
import build.archipelago.packageservice.core.data.models.PackageDataModel;
import build.archipelago.packageservice.common.exceptions.PackageExistsException;

import java.util.Optional;

public class CreatePackageDelegate {

    private PackageData packageData;

    public CreatePackageDelegate(PackageData packageData) {
        this.packageData = packageData;
    }

    public void create(CreatePackageDelegateRequest request) throws PackageExistsException {
        request.validate();

        Optional<PackageDataModel> packageSearch = packageData.getPackage(request.getName());
        if (packageSearch.isPresent()) {
            throw new PackageExistsException(request.getName());
        }

        packageData.createPackage(CreatePackageModel.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build());
    }
}
