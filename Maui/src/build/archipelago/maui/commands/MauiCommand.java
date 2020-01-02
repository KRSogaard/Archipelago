package build.archipelago.maui.commands;

import java.util.concurrent.Callable;

public interface MauiCommand {
    void call(String... args);
}
