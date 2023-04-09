package tech.dort.dortware.impl.commands;

import org.apache.commons.lang3.RandomUtils;
import tech.dort.dortware.api.command.Command;
import tech.dort.dortware.api.command.CommandData;
import tech.dort.dortware.impl.utils.player.ChatUtil;

public class GayRateCommand extends Command {

    public GayRateCommand() {
        super(new CommandData("gayrate"));
    }

    @Override
    public void run(String command, String... args) {
        try {
            if ("gayrate".equalsIgnoreCase(command)) {
                if (args[1].length() != 0 && args[1].length() < 16) {
                    ChatUtil.displayChatMessage(args[1] + " is " + RandomUtils.nextInt(0, 100) + "% gay.");
                }
            }
        } catch (ArrayIndexOutOfBoundsException exception) {
            ChatUtil.displayChatMessage("Not enough arguments\nUsage:\ngayrate [username]");
        } catch (Exception exception) {
            ChatUtil.displayChatMessage("An unknown error occurred.");
        }
    }
}
