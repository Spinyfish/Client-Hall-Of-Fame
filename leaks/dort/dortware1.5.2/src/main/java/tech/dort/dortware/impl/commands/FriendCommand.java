package tech.dort.dortware.impl.commands;

import tech.dort.dortware.Client;
import tech.dort.dortware.api.command.Command;
import tech.dort.dortware.api.command.CommandData;
import tech.dort.dortware.impl.utils.player.ChatUtil;

import java.util.NoSuchElementException;

public class FriendCommand extends Command {

    public FriendCommand() {
        super(new CommandData("friend", "f"));
    }

    @Override
    public void run(String command, String... args) {
        try {
            switch (command.toLowerCase()) {
                case "friend":
                case "f":
                    switch (args.length) {
                        case 2: {
                            String name = args[1];
                            if (Client.INSTANCE.getFriendManager().getObjects().contains(name.toLowerCase())) {
                                Client.INSTANCE.getFriendManager().getObjects().remove(name.toLowerCase());
                                ChatUtil.displayChatMessage("Removed " + name + " from the friends list.");
                                return;
                            }
                            Client.INSTANCE.getFriendManager().getObjects().add(name.toLowerCase());
                            ChatUtil.displayChatMessage("Added " + name + " to the friends list.");
                            break;
                        }
                        case 3: {
                            String name1 = args[2];
                            if (!Client.INSTANCE.getFriendManager().getObjects().contains(name1.toLowerCase()) && args[1].equalsIgnoreCase("add")) {
                                Client.INSTANCE.getFriendManager().getObjects().add(name1.toLowerCase());
                                ChatUtil.displayChatMessage("Added " + name1 + " to the friends list.");
                                return;
                            } else if (Client.INSTANCE.getFriendManager().getObjects().contains(name1.toLowerCase()) && args[1].equalsIgnoreCase("remove")) {
                                Client.INSTANCE.getFriendManager().getObjects().remove(name1.toLowerCase());
                                ChatUtil.displayChatMessage("Removed " + name1 + " from the friends list.");
                                return;
                            }
                            ChatUtil.displayChatMessage("Invalid parameters.");
                        }
                    }
            }
        } catch (ArrayIndexOutOfBoundsException exception) {
            ChatUtil.displayChatMessage("Not enough arguments\nUsage:\n[friend/f] [username]");
            exception.printStackTrace();
        } catch (NoSuchElementException exception) {
            ChatUtil.displayChatMessage("Invalid username.");
        } catch (Exception exception) {
            ChatUtil.displayChatMessage("The fuck did you do? LOL");
            exception.printStackTrace();
        }
    }
}
