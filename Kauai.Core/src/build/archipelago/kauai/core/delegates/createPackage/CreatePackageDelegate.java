package build.archipelago.kauai.core.delegates.createPackage;

import build.archipelago.kauai.core.data.PackageData;
import build.archipelago.kauai.core.data.models.CreatePackageModel;
import build.archipelago.kauai.core.data.models.PackageDataModel;
import build.archipelago.kauai.core.exceptions.PackageExistsException;

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
