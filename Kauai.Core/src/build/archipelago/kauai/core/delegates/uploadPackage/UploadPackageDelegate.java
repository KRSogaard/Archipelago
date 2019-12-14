package build.archipelago.kauai.core.delegates.uploadPackage;

import build.archipelago.kauai.core.data.DynamoDBKeys;
import build.archipelago.kauai.core.data.PackageData;
import build.archipelago.kauai.core.data.PackageDataModel;
import build.archipelago.kauai.core.storage.PackageStorage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import utils.Constants;

import java.util.HashMap;
import java.util.Optional;

@Slf4j
public class UploadPackageDelegate {

    private PackageStorage packageStorage;
    private PackageData packageData;

    public UploadPackageDelegate(PackageData packageData,
                                 PackageStorage packageStorage) {
        this.packageData = packageData;
        this.packageStorage = packageStorage;
    }

    public void uploadPackage(UploadPackageDelegateRequest request)
            throws PackageArtifactExistsException {
        Preconditions.checkNotNull(request.getNameVersion(), "Name required");
        request.getNameVersion().validate();
        Preconditions.checkNotNull(request.getHash(), "Hash required");
        Preconditions.checkArgument(
                !Constants.LATEST.equalsIgnoreCase(request.getHash()),
                "Hash can not be \"" + Constants.LATEST + "\"");
        Preconditions.checkNotNull(request.getConfig(), "Config required");
        Preconditions.checkNotNull(request.getBuildArtifact(), "Build artifact required");

        String hash = request.getHash();
        if (Strings.isNullOrEmpty(hash)) {
            hash = Constants.LATEST;
        }
        Optional<PackageDataModel> p = packageData.get(request.getNameVersion(), hash);
        if (p.isPresent()) {
            throw new PackageArtifactExistsException(
                    request.getNameVersion().getNameVersion() + " [" + request.getHash() + "] already exists"
            );
        }

        packageStorage.upload(request.getNameVersion().getName(),
                              request.getNameVersion().getVersion(),
                              request.getHash(),
                              request.getBuildArtifact());

        packageData.create(request.getNameVersion(), request.getHash(), request.getConfig());
    }

}
