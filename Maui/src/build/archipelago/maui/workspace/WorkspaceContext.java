package build.archipelago.maui.workspace;

import build.archipelago.maui.models.PackageDependency;
import java.nio.file.Path;
import java.util.List;

public class WorkspaceContext {
    public String versionSetName;
    public Path workspaceRoot;

    public List<PackageDependency> packages;
}
