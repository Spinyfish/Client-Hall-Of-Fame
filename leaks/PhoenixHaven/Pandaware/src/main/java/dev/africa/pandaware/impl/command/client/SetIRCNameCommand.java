package dev.africa.pandaware.impl.command.client;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.command.Command;
import dev.africa.pandaware.api.command.interfaces.CommandInformation;
import dev.africa.pandaware.utils.client.Printer;
import net.minecraft.util.EnumChatFormatting;

@CommandInformation(name = "setIRCName", description = "Set's IRC username")
public class SetIRCNameCommand extends Command {

    @Override
    public void process(String[] arguments) {
        if (arguments.length > 0) {

            String name = arguments[1];

            System.out.println(name);

            if (name.length() < 3 || name.length() > 16) {
                Printer.chat(EnumChatFormatting.RED + "Name length must be over 3 and less than 16 characters");
            } else {
                name = name.replace(" ", "")
                        .replaceAll("[^\\p{ASCII}]", "");

                String finalName = name;

                String finalName1 = name;
                new Thread(() -> {
                    Printer.chat("§aIRC Username has been set to §7" + finalName1);
                    Printer.chat("§aNew IRC name will be set at the next reconnection");
                }).start();

                Client.getInstance().getExecutor().execute(() -> Client.getInstance().getFileManager().saveAll());
            }

        } else {
            Printer.chat(EnumChatFormatting.RED + "Please supply a name.");
        }
    }
}
