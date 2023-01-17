package com.alan.clients.component.impl.hud;

import com.alan.clients.component.Component;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.motion.PreMotionEvent;
import com.alan.clients.newevent.impl.packet.PacketReceiveEvent;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.ui.ingame.GuiIngameCache;
import util.time.StopWatch;

import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;


public class AdaptiveRefreshRateComponent extends Component {

    private boolean playerList, debugInfo;
    private boolean setDirty;
    private float health;
    public StopWatch stopWatch = new StopWatch();

    @EventLink()
    public final Listener<PreMotionEvent> onPreMotionEvent = event -> {
        if (mc.thePlayer.ticksExisted % 100 == 0 || setDirty ||
                mc.gameSettings.keyBindPlayerList.isKeyDown() != playerList || mc.gameSettings.showDebugInfo != debugInfo ||
                mc.thePlayer.ticksExisted <= 10 || health != mc.thePlayer.getHealth()) {
            GuiIngameCache.dirty = true;
            playerList = mc.gameSettings.keyBindPlayerList.isKeyDown();
            debugInfo = mc.gameSettings.showDebugInfo;
            setDirty = false;
            health = mc.thePlayer.getHealth();
        }
    };

    @EventLink()
    public final Listener<Render2DEvent> onRender2D = event -> {
        if (mc.currentScreen != null) {
            GuiIngameCache.dirty = true;
        }
    };

    @EventLink()
    public final Listener<PacketReceiveEvent> onPacketReceiveEvent = event -> {
        Packet packet = event.getPacket();

        if (packet instanceof S02PacketChat || packet instanceof S30PacketWindowItems ||
                packet instanceof S0DPacketCollectItem || packet instanceof S2FPacketSetSlot) {
            stopWatch.reset();
            setDirty = true;
        }
    };
}