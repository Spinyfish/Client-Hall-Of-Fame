package com.alan.clients.module.impl.player.scaffold.tower;

import com.alan.clients.component.impl.player.SlotComponent;
import com.alan.clients.module.impl.player.Scaffold;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.Priorities;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.motion.PushOutOfBlockEvent;
import com.alan.clients.newevent.impl.packet.PacketSendEvent;
import com.alan.clients.util.packet.PacketUtil;
import com.alan.clients.util.player.MoveUtil;
import com.alan.clients.util.player.PlayerUtil;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.value.Mode;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;

public class WatchdogTower extends Mode<Scaffold> {

    public WatchdogTower(String name, Scaffold parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {

        if (!mc.gameSettings.keyBindJump.isKeyDown()) return;

        PacketUtil.send(new C08PacketPlayerBlockPlacement(SlotComponent.getItemStack()));
        mc.thePlayer.motionY = 0.42f;
    };
}
