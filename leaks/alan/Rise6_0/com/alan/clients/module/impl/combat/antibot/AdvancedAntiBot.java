package com.alan.clients.module.impl.combat.antibot;

import com.alan.clients.module.impl.combat.AntiBot;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.value.Mode;
import com.alan.clients.Client;

public final class AdvancedAntiBot extends Mode<AntiBot> {

    public AdvancedAntiBot(String name, AntiBot parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        mc.theWorld.playerEntities.forEach(player -> {
            if (mc.thePlayer.getDistanceSq(player.posX, mc.thePlayer.posY, player.posZ) > 200) {
                Client.INSTANCE.getBotManager().remove(player);
            }

            if (player.ticksExisted < 5 || player.isInvisible() || mc.thePlayer.getDistanceSq(player.posX, mc.thePlayer.posY, player.posZ) > 100 * 100) {
                Client.INSTANCE.getBotManager().add(player);
            }
        });
    };

}