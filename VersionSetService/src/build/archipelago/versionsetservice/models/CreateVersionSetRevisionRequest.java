package build.archipelago.versionsetservice.models;

import build.archipelago.common.ArchipelagoPackage;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import lombok.NonNull;
import lombok.Value;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.PathParam;

@Value
public class CreateVersionSetRevisionRequest {

    @PathParam("versionSet")
    @NonNull
    private String versionSetName;
    @NonNull
    private List<String> packages;

    public void validate() {
        Preconditions.checkArgument(Strings.isNullOrEmpty(versionSetName), "VersionSet Name is required");
        Preconditions.checkNotNull(packages, "Packages are required");
        Preconditions.checkArgument(packages.size() > 0, "A minimum of 1 package is required");
    }
}
