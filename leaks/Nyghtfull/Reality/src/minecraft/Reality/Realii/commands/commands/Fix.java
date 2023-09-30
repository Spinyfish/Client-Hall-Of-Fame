package Reality.Realii.commands.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import Reality.Realii.commands.Command;
import Reality.Realii.utils.cheats.player.Helper;

public class Fix extends Command {
    public Fix() {
        super("fix", new String[]{"fix,sounds,sound"}, "", "fix sounds bug");
    }

    @Override
    public String execute(String[] args) {
//        Minecraft.getMinecraft().mcSoundHandler = new SoundHandler(Minecraft.getMinecraft().mcResourceManager, Minecraft.getMinecraft().gameSettings);
        Minecraft.getMinecraft().mcResourceManager.registerReloadListener(Minecraft.getMinecraft().mcSoundHandler);
        Minecraft.getMinecraft().renderGlobal.loadRenderers();
        Helper.sendMessage("Your sounds system & renders should work now!");
        return null;
    }
}
