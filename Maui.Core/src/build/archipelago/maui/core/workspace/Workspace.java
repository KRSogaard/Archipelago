package build.archipelago.maui.core.workspace;

import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.maui.core.workspace.serializer.WorkspaceSerializer;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Data
@Builder
@Slf4j
public class Workspace {
    private String versionSet;
    private List<ArchipelagoPackage> localPackages;

    protected void save(Path workspaceRoot) throws IOException {
        WorkspaceSerializer.save(this, workspaceRoot);
    }

    protected static Workspace load(Path workspaceRoot) throws IOException {
        return WorkspaceSerializer.load(workspaceRoot);
    }
}
