/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.impl.event.EventMove;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.movement.Flight;

public class NoFall
extends Module {
    public Setting<Mode> mode = new Setting<Mode>("Mode", Mode.GROUNDSPOOF).describedBy("How to prevent fall damage");
    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        switch (this.mode.getValue()) {
            case EDIT: {
                if (!(this.mc.thePlayer.fallDistance > 3.0f)) break;
                e.setOnGround(true);
                break;
            }
            case VERUS: {
                if (!((double)this.mc.thePlayer.fallDistance - this.mc.thePlayer.motionY > 3.0) || Wrapper.getModule(Flight.class).isEnabled()) break;
                this.mc.thePlayer.motionY = 0.0;
                this.mc.thePlayer.motionX *= 0.7;
                this.mc.thePlayer.motionZ *= 0.7;
                this.mc.thePlayer.fallDistance = 0.0f;
                this.mc.thePlayer.setPosition(e.getX(), e.getY(), e.getZ());
                e.setOnGround(true);
                break;
            }
            case PACKET: {
                if (!(this.mc.thePlayer.fallDistance > 3.0f)) break;
                PacketUtil.sendPacket(new C03PacketPlayer(true));
                this.mc.thePlayer.fallDistance = 0.0f;
                break;
            }
            case HYPIXEL: {
                e.setY(256.0 + Math.random() * 10.0);
            }
        }
    };
    @EventLink
    public final Listener<EventMove> eventMove = e -> {
        switch (this.mode.getValue()) {
            case YCHANGE: {
                if (!(this.mc.thePlayer.fallDistance > 3.0f)) break;
                e.setY(e.getY() - ((double)this.mc.thePlayer.fallDistance - 0.5));
                PacketUtil.sendPacket(new C03PacketPlayer(true));
                break;
            }
        }
    };
    @EventLink
    public final Listener<EventPacket> eventUpdateListener = e -> {
        if (this.mc.thePlayer == null || this.mc.theWorld == null) {
            return;
        }
        if (this.mc.thePlayer.fallDistance > 3.0f) {
            switch (this.mode.getValue()) {
                case GROUNDSPOOF: {
                    this.mc.thePlayer.onGround = true;
                    break;
                }
                case POSITION: {
                    this.mc.thePlayer.onGround = true;
                    PacketUtil.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY - (double)(this.mc.thePlayer.fallDistance - 1.0f), this.mc.thePlayer.posZ, true));
                }
            }
        }
    };
    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        if (e.getPacket() instanceof C03PacketPlayer && this.mc.thePlayer.fallDistance > 3.0f && this.mode.getValue() == Mode.INTERCEPT) {
            ((C03PacketPlayer)e.getPacket()).onGround = true;
        }
        switch (this.mode.getValue()) {
            case INTERCEPT: {
                if (!(e.getPacket() instanceof C03PacketPlayer) || !(this.mc.thePlayer.fallDistance > 3.0f)) break;
                ((C03PacketPlayer)e.getPacket()).onGround = true;
                break;
            }
        }
    };

    public NoFall() {
        super("No Fall", "Take no fall damage", Category.PLAYER);
        this.setMetadata(() -> StringUtil.formatEnum(this.mode.getValue()));
    }

    static enum Mode {
        GROUNDSPOOF,
        INTERCEPT,
        PACKET,
        POSITION,
        EDIT,
        YCHANGE,
        MLG,
        VERUS,
        HYPIXEL;

    }
}

