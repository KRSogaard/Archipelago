package build.archipelago.maui.commands;

import picocli.CommandLine;

@CommandLine.Command(name = "workspace",
        aliases = { "ws" },
        subcommands = { WorkspaceCreateCommand.class, CommandLine.HelpCommand.class },
        description = "Workspace related commands")
public class WorkspaceCommand implements Runnable {

    @Override
    public void run() {
        System.err.println("Running Workspace Command");
    }
}
