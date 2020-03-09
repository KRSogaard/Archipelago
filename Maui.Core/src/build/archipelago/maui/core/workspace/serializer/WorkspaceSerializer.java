package build.archipelago.maui.core.workspace.serializer;

import build.archipelago.common.ArchipelagoPackage;
import build.archipelago.maui.core.workspace.Workspace;
import build.archipelago.maui.core.workspace.WorkspaceConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Builder
@Data
public
class WorkspaceSerializer {
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    private String versionSet;
    private List<String> localPackages;

    private static WorkspaceSerializer convert(Workspace ws) {
        List<String> localPackages = new ArrayList<String>();
        for (ArchipelagoPackage nameVersion : ws.getLocalPackages()) {
            localPackages.add(String.format("%s-%s", nameVersion.getName(), nameVersion.getVersion()));
        }

        return WorkspaceSerializer.builder()
                .versionSet(ws.getVersionSet())
                .localPackages(localPackages)
                .build();
    }

    private static Workspace convert(WorkspaceSerializer wss) {
        List<ArchipelagoPackage> localPackages = new ArrayList<ArchipelagoPackage>();
        for (String packageString : wss.getLocalPackages()) {
            int p = packageString.lastIndexOf("-");
            if (p == -1) {
                log.warn("Was unable to parse the package and version from string \"{}\"", packageString);
                continue;
            }
            String packageName = packageString.substring(0, p - 1);
            String packageVersion = packageString.substring(p + 1);
            localPackages.add(new ArchipelagoPackage(packageName, packageVersion));
        }

        return Workspace.builder()
                .versionSet(wss.getVersionSet())
                .localPackages(localPackages)
                .build();
    }

    public static void save(Workspace workspace, Path workspaceRoot) throws IOException {
        if (Files.notExists(workspaceRoot)) {
            throw new IOException(String.format("Workspace root \"%s\" was not found", workspaceRoot));
        }

        Path workspaceFilePath = Paths.get(workspaceRoot.toString(), WorkspaceConstants.WORKSPACE_FILE_NAME);
        if (Files.exists(workspaceFilePath)) {
            log.warn("Workspace file \"{}\" already exists, we will override it", workspaceFilePath.toString());
        }

        WorkspaceSerializer wss = WorkspaceSerializer.convert(workspace);
        mapper.writeValue(workspaceFilePath.toFile(), wss);
    }

    public static Workspace load(Path workspaceRoot) throws IOException {
        Path workspaceFilePath = Paths.get(workspaceRoot.toString(), WorkspaceConstants.WORKSPACE_FILE_NAME);
        if (Files.notExists(workspaceFilePath)) {
            throw new IOException(String.format("Could not find the workspace file \"%s\" in \"%s\"",
                    WorkspaceConstants.WORKSPACE_FILE_NAME, workspaceRoot));
        }

        WorkspaceSerializer wss = mapper.readValue(workspaceFilePath.toFile(), WorkspaceSerializer.class);
        return WorkspaceSerializer.convert(wss);
    }

}
