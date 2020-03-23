package build.archipelago.packageservice.core.delegates.uploadBuildArtifact;

import build.archipelago.common.ArchipelagoBuiltPackage;
import build.archipelago.common.exceptions.PackageExistsException;
import build.archipelago.common.exceptions.PackageNotFoundException;
import build.archipelago.packageservice.core.data.PackageData;
import build.archipelago.packageservice.core.storage.PackageStorage;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class UploadBuildArtifactDelegate {

    private PackageStorage packageStorage;
    private PackageData packageData;

    public UploadBuildArtifactDelegate(PackageData packageData,
                                       PackageStorage packageStorage) {
        this.packageData = packageData;
        this.packageStorage = packageStorage;
    }

    public String uploadArtifact(UploadBuildArtifactDelegateRequest request)
            throws PackageNotFoundException, PackageExistsException {
        request.validate();

        String hash = UUID.randomUUID().toString().split("-")[0];

        ArchipelagoBuiltPackage pkg = new ArchipelagoBuiltPackage(request.getPkg(), hash);
        packageData.createBuild(pkg, request.getConfig());
        packageStorage.upload(pkg, request.getBuildArtifact());

        return hash;
    }

}
