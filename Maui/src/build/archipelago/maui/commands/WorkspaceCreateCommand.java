package build.archipelago.maui.commands;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Command;

import java.util.List;

@Slf4j
@Command(name = "create",
        aliases = { "c" },
        description = "Create a archipelago workspace")
public class WorkspaceCreateCommand implements Runnable {

    @CommandLine.Option(
            names = { "--name", "-n" },
            description = "Name of the workspace")
    private String name;

    @CommandLine.Option(
            names = { "--package", "-p" },
            description = "Packages to build locally")
    public List<String> packages;

    @CommandLine.Option(
            names = { "--versionset", "-vs" },
            description = "The version set to build against")
    public String versionSet;

    @Override
    public void run() {
        if (Strings.isNullOrEmpty(name)) {
            log.error("A workspace name is required");
            return;
        }

        System.err.println("Running Workspace Create Command, " + packages.size() + "");
    }
}
