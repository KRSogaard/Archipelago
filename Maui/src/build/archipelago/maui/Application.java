package build.archipelago.maui;

import build.archipelago.maui.commands.ParentCommand;
import build.archipelago.maui.commands.WorkspaceCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
@Slf4j
public class Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        CommandLine cmd = new CommandLine(new ParentCommand());
        cmd.setExecutionStrategy(new CommandLine.RunLast()); // default is RunLast
        cmd.execute(args);

        if (args.length == 0) { cmd.usage(System.out); }
        log.error("Finished");
    }

}
