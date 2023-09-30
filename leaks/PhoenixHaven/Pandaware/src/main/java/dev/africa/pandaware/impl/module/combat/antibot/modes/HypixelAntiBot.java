package dev.africa.pandaware.impl.module.combat.antibot.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.event.player.UpdateEvent;
import dev.africa.pandaware.impl.module.combat.antibot.AntiBotModule;
import dev.africa.pandaware.utils.player.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S02PacketChat;

public class HypixelAntiBot extends ModuleMode<AntiBotModule> {
    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat) event.getPacket();
            String text = packet.getChatComponent().getUnformattedText();
            if (text.contains("?????????????????????????????????")) {
                Client.getInstance().getIgnoreManager().getIgnoreList().clear();
            }
        }
    };

    @EventHandler
    EventCallback<UpdateEvent> onUpdate = event -> {
        if (mc.thePlayer.ticksExisted % 20 == 0) {
            Client.getInstance().getIgnoreManager().getIgnoreList().clear();
            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (!PlayerUtils.getPlayerList().contains(entity) && entity.isInvisible() && entity instanceof EntityPlayer) {
                    Client.getInstance().getIgnoreManager().add((EntityPlayer) entity, false);
                }
            }
        }
    };

    public HypixelAntiBot(String name, AntiBotModule parent) {
        super(name, parent);
    }

    @Override
    public void onDisable() {
        Client.getInstance().getIgnoreManager().getIgnoreList().clear();
    }
}
