package build.archipelago.kauai.core.delegates.uploadBuildArtifact;

import build.archipelago.common.PackageNameVersion;
import build.archipelago.kauai.core.data.PackageData;
import build.archipelago.kauai.core.data.models.PackageBuiltDataModel;
import build.archipelago.kauai.core.data.models.PackageDataModel;
import build.archipelago.kauai.core.exceptions.PackageArtifactExistsException;
import build.archipelago.kauai.core.exceptions.PackageNotFoundException;
import build.archipelago.kauai.core.storage.PackageStorage;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import build.archipelago.kauai.core.utils.Constants;

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

        Optional<PackageDataModel> pkg = packageData.getPackage(request.getNameVersion().getName());
        if (!pkg.isPresent()) {
            throw new PackageNotFoundException(request.getNameVersion().getName());
        }
        PackageNameVersion nameVersion = new PackageNameVersion(
                pkg.get().getName(), request.getNameVersion().getVersion());

        String hash = request.getHash();
        if (Strings.isNullOrEmpty(hash)) {
            hash = Constants.LATEST;
        }

        Optional<PackageBuiltDataModel> p = packageData.getBuild(nameVersion, hash);
        if (p.isPresent()) {
            throw new PackageArtifactExistsException(
                    nameVersion.getConcatenated() + " [" + request.getHash() + "] already exists"
            );
        }

        packageStorage.upload(nameVersion,
                              request.getHash(),
                              request.getBuildArtifact());

        packageData.createBuild(request.getNameVersion(), request.getHash(), request.getConfig());
    }

}
