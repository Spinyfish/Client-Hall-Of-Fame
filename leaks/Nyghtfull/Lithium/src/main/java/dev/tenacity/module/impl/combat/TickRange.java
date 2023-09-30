package dev.tenacity.module.impl.combat;

import dev.tenacity.event.impl.network.PacketReceiveEvent;
import dev.tenacity.event.impl.network.PacketSendEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.utils.player.ChatUtil;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.ArrayList;

public class TickRange extends Module {
    private final TimerUtil timeHelper;
    private final TimerUtil timeHelper2;
    private final ArrayList<Integer> diffs;
    public long balanceCounter;
    private long lastTime;
    private WorldClient lastWorld;
    public TickRange() {
        super("TickRange", Category.COMBAT, "umm uses uhhh lag to give advance.... advnadate... advnatge");
        this.timeHelper = new TimerUtil();
        this.timeHelper2 = new TimerUtil();
        this.diffs = new ArrayList<Integer>();
        this.balanceCounter = 0L;
        this.lastWorld = null;
    }

    @Override
    public void onPacketSendEvent(PacketSendEvent event) {
        final Packet packet = event.getPacket();
        if (packet instanceof C03PacketPlayer) {

            if (this.lastWorld != null && this.lastWorld != mc.theWorld) {
                this.balanceCounter = 0L;
                this.diffs.clear();
            }
            if (this.balanceCounter > 0L) {
                --this.balanceCounter;
            }
            final long diff = System.currentTimeMillis() - this.lastTime;
            this.diffs.add((int) diff);
            this.balanceCounter += (diff - 50L) * -3L;
            this.lastTime = System.currentTimeMillis();
            if (this.balanceCounter > 150L) {
            }
            this.lastWorld = mc.theWorld;
        }
            super.onPacketSendEvent(event);
    }

    @Override
    public void onPacketReceiveEvent(PacketReceiveEvent event) {
        final Packet packet = event.getPacket();
        if (packet instanceof S08PacketPlayerPosLook) {
            this.balanceCounter -= 100L;
        }

        super.onPacketReceiveEvent(event);
    }




}