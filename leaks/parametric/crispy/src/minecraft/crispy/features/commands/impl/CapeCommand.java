package crispy.features.commands.impl;

import crispy.Crispy;
import crispy.features.commands.Command;
import crispy.features.commands.CommandInfo;
import crispy.util.file.Filer;
import crispy.util.server.CapeManager;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(name = "Cape", description = "Change your cape", syntax = ".cape [imgur]", alias = "cape")
public class CapeCommand extends Command {

    List<String> validCapes = new ArrayList<>();

    public CapeCommand() {
        validCapes.add("default");
    }


    @Override
    public void onCommand(String command, String[] args) throws Exception {


        if (args[0].contains("i.imgur.com") || args[0].contains(".png") || args[0].contains(".jpg") || validCapes.contains(args[0].toLowerCase())) {
            switch (args[0].toLowerCase()) {
                case "default":
                    args[0] = "Crispy";
                    break;
            }
            Crispy.INSTANCE.setCapeUrl(args[0]);
            CapeManager capeManager = new CapeManager();
            CapeManager.capeUsers.removeIf(capeUser -> capeUser.player.equalsIgnoreCase(Minecraft.getMinecraft().thePlayer.getCommandSenderName()));
            CapeManager.ignoreUsers.removeIf(capeUser -> capeUser.equalsIgnoreCase(Minecraft.getMinecraft().thePlayer.getCommandSenderName()));
            capeManager.sendCapeRequest(Minecraft.getMinecraft().thePlayer.getCommandSenderName(), Crispy.INSTANCE.getCapeUrl());
            capeManager.getCape(Minecraft.getMinecraft().thePlayer.getCommandSenderName());
            message("Cape set to " + args[0], true);

        } else {
            message(".cape [imgur] || .cape default", true);
        }
    }
}