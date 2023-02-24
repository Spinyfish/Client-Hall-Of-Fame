package me.kansio.client.commands.impl;

import me.kansio.client.Client;
import me.kansio.client.commands.Command;
import me.kansio.client.commands.CommandData;
import me.kansio.client.utils.chat.ChatUtil;

@CommandData(
        name = "focus",
        description = "Focuses a mentioned player"
)
public class FocusCommand extends Command {

    @Override
    public void run(String[] args) {
        if (args.length > 0) {
            Client.getInstance().getTargetManager().getTarget().add(args[0]);
            ChatUtil.log("Added them as a target.");
        }
    }
}
