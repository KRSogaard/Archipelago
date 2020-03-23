package build.archipelago.versionsetservice.core.delegates;

import build.archipelago.common.exceptions.VersionSetDoseNotExistsException;
import build.archipelago.versionsetservice.core.models.VersionSet;
import build.archipelago.versionsetservice.core.services.VersionSetService;
import build.archipelago.versionsetservice.core.utils.NameUtil;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class GetVersionSetDelegate {

    private VersionSetService versionSetService;

    public GetVersionSetDelegate(VersionSetService versionSetService) {

        this.versionSetService = versionSetService;
    }

    public VersionSet getVersionSet(String versionSetName) throws VersionSetDoseNotExistsException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(versionSetName), "Version Set Name is required");
        Preconditions.checkArgument(NameUtil.validateVersionSetName(versionSetName), "Version set name was invalid");
        VersionSet vs = versionSetService.get(versionSetName);
        if (vs == null) {
            throw new VersionSetDoseNotExistsException(versionSetName);
        }
        return vs;
    }
}
