package dev.africa.pandaware.api.command;

import dev.africa.pandaware.api.command.interfaces.CommandInformation;
import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.api.interfaces.Processable;
import dev.africa.pandaware.utils.client.Printer;
import lombok.Getter;

@Getter
public abstract class Command implements MinecraftInstance, Processable {
    private String name;
    private String description;
    private String[] aliases;

    public Command() {
        if (this.getClass().isAnnotationPresent(CommandInformation.class)) {
            CommandInformation commandInformation = this.getClass().getAnnotation(CommandInformation.class);

            this.name = commandInformation.name();
            this.description = commandInformation.description();
            this.aliases = commandInformation.aliases();
        } else {
            System.out.println("@" + CommandInformation.class.getSimpleName()
                    + " not present in " + this.getClass().getName());
            System.exit(-1);
        }
    }

    public void onError(String[] arguments, String message) {

    }

    public void sendInvalidArgumentsMessage(String... args) {
        Printer.chat("§cInvalid arguments");

        if (args.length > 0) {
            StringBuilder builder = new StringBuilder();

            for (String arg : args) {
                builder.append("§7<").append(arg.toUpperCase()).append("> ");
            }

            Printer.chat("§aUsage: §7." + this.name + " " + builder);
        }
    }
}
