package dev.africa.pandaware.manager.command;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.command.Command;
import dev.africa.pandaware.api.interfaces.Initializable;
import dev.africa.pandaware.impl.command.client.*;
import dev.africa.pandaware.impl.command.player.*;
import dev.africa.pandaware.impl.container.Container;
import dev.africa.pandaware.impl.event.player.ChatEvent;
import dev.africa.pandaware.utils.client.Printer;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CommandManager extends Container<Command> implements Initializable {
    @Override
    public void init() {
        this.addCommands(
                new BindCommand(),
                new PanicCommand(),
                new HelpCommand(),
                new ConfigCommand(),
                new ToggleCommand(),
                new ScriptCommand(),

                new VClipCommand(),
                new SetIRCNameCommand(),
                new SusCommand(),
                new OnlineCommand()
        );
    }

    public void handleCommands(ChatEvent event) {
        String message = event.getMessage();

        if (message.startsWith(".")) {
            if (Client.getInstance().isKillSwitch()) {
                throw new NullPointerException();
            }
            event.cancel();

            Command command = null;
            String[] arguments = message.split(" ");
            String replace = arguments[0].replaceFirst(".", "");

            for (Command c : this.getItems()) {

                if (this.arrayContainsIgnoreCase(
                        Arrays.stream(c.getAliases()).collect(Collectors.toList()),
                        replace) || replace.equalsIgnoreCase(c.getName())) {
                    command = c;
                    break;
                }
            }

            if (command != null) {
                try {
                    command.process(arguments);
                } catch (Exception e) {
                    e.printStackTrace();
                    Printer.chat("§cFailed to process §7" + command.getName() + " §ccommand");
                    Printer.chat("§cStacktrace: " + e.getMessage());

                    command.onError(arguments, e.getMessage());
                }
            } else {
                Printer.chat("§cCommand does not exist");
            }
        }
    }

    private boolean arrayContainsIgnoreCase(List<String> listIn, String stringIn) {
        return listIn.stream().anyMatch(stringIn::equalsIgnoreCase);
    }

    private void addCommands(Command... commands) {
        for (Command command : commands) {
            if (!this.getItems().contains(command)) {
                this.getItems().add(command);
            }
        }
    }
}
