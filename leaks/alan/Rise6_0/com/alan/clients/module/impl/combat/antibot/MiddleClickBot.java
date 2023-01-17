package com.alan.clients.module.impl.combat.antibot;

import com.alan.clients.Client;
import com.alan.clients.bots.BotManager;
import com.alan.clients.module.impl.combat.AntiBot;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.value.Mode;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

public final class MiddleClickBot extends Mode<AntiBot>  {

    private boolean down;

    public MiddleClickBot(String name, AntiBot parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (Mouse.isButtonDown(2)) {
            if (down) return;
            down = true;

            if (mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                BotManager botManager = Client.INSTANCE.getBotManager();
                Entity entity = mc.objectMouseOver.entityHit;

                if (botManager.contains(entity)) {
                    Client.INSTANCE.getBotManager().remove(entity);
                } else {
                    Client.INSTANCE.getBotManager().add(entity);
                }
            }
        } else down = false;
    };
}