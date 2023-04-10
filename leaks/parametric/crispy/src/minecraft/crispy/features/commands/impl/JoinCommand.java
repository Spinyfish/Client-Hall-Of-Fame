package crispy.features.commands.impl;

import crispy.features.commands.Command;
import crispy.features.commands.CommandInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;

@CommandInfo(name = "Join", alias = "join", description = "Joins any minehut server", syntax = ".join [mhserver]")
public class JoinCommand extends Command {
    @Override
    public void onCommand(String arg, String[] args) throws Exception {
        if(args.length > 0) {
            String server = args[0];
            Minecraft.getMinecraft().displayGuiScreen(new GuiConnecting(Minecraft.getMinecraft().currentScreen, Minecraft.getMinecraft(), new ServerData("MINEHUT", server + ".minehut.gg")));
        } else {
            message(getSyntax(), true);
        }
    }
}
