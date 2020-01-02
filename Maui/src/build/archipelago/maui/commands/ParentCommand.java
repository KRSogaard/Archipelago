package build.archipelago.maui.commands;

import picocli.CommandLine;

@CommandLine.Command(name = "maui",
        subcommands = { WorkspaceCommand.class, CommandLine.HelpCommand.class },
        description = "Maui the Archipelago build system")
public class ParentCommand implements Runnable {
    @Override
    public void run() {
        System.err.println("Parent");
    }
}
