package me.kansio.client.modules.impl.player.hackerdetect.checks;

import me.kansio.client.Client;
import me.kansio.client.event.impl.PacketEvent;
import me.kansio.client.modules.impl.player.HackerDetect;
import me.kansio.client.utils.chat.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public abstract class Check {

    public abstract String name();

    protected HackerDetect detect = HackerDetect.getInstance();

    protected static Minecraft mc = Minecraft.getMinecraft();

    public void onUpdate() {

    }

    public void onBlocksMCGameStartTick() {


    }
    public void onPacket(PacketEvent event) {

    }

    public void flag(EntityPlayer player) {
        HackerDetect hack = (HackerDetect) Client.getInstance().getModuleManager().getModuleByName("HackerDetect");
        switch (hack.theme.getValue()) {
            case "Sleek":
                ChatUtil.logSleekCheater(player.getName(), name());
                break;
            case "Verus":
                ChatUtil.logVerusCheater(player.getName(), name(), "2" );
                break;
        }
    }

}
