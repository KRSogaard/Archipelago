package build.archipelago.packageservice.core.delegates.uploadBuildArtifact;

import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.packageservice.core.data.PackageData;
import build.archipelago.packageservice.core.data.models.BuiltPackageDetails;
import build.archipelago.packageservice.core.data.models.PackageDetails;
import build.archipelago.packageservice.common.exceptions.PackageArtifactExistsException;
import build.archipelago.packageservice.common.exceptions.PackageNotFoundException;
import build.archipelago.packageservice.core.storage.PackageStorage;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import build.archipelago.packageservice.core.utils.Constants;

import java.util.Optional;

@Slf4j
public class UploadBuildArtifactDelegate {

    private PackageStorage packageStorage;
    private PackageData packageData;

    public UploadBuildArtifactDelegate(PackageData packageData,
                                       PackageStorage packageStorage) {
        this.packageData = packageData;
        this.packageStorage = packageStorage;
    }

    public void uploadArtifact(UploadBuildArtifactDelegateRequest request)
            throws PackageArtifactExistsException, PackageNotFoundException {
        request.validate();

        Optional<PackageDetails> pkg = packageData.getPackage(request.getNameVersion().getName());
        if (!pkg.isPresent()) {
            throw new PackageNotFoundException(request.getNameVersion().getName());
        }
        ArchipelagoPackage nameVersion = new ArchipelagoPackage(
                pkg.get().getName(), request.getNameVersion().getVersion());

        String hash = request.getHash();
        if (Strings.isNullOrEmpty(hash)) {
            hash = Constants.LATEST;
        }

        Optional<BuiltPackageDetails> p = packageData.getBuild(nameVersion, hash);
        if (p.isPresent()) {
            throw new PackageArtifactExistsException(
                    nameVersion.toString() + " [" + request.getHash() + "] already exists"
            );
        }

        packageStorage.upload(nameVersion,
                              request.getHash(),
                              request.getBuildArtifact());

        packageData.createBuild(request.getNameVersion(), request.getHash(), request.getConfig());
    }

}
