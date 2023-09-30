/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.player;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.ArrayList;
import java.util.Collections;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.util.AxisAlignedBB;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.movement.Flight;
import wtf.monsoon.impl.ui.notification.NotificationType;

public class AntiVoid
extends Module {
    Setting<Mode> mode = new Setting<Mode>("Mode", Mode.FLAG);
    Setting<Float> flagAfterBlocks = new Setting<Float>("AfterBlocks", Float.valueOf(7.0f)).minimum(Float.valueOf(1.0f)).maximum(Float.valueOf(20.0f)).incrementation(Float.valueOf(0.1f));
    private ArrayList<Packet> packets = new ArrayList();
    private double flyHeight;
    private Timer timer = new Timer();
    private boolean flagged;
    double xPos;
    double yPos;
    double zPos;
    boolean shouldHop;
    @EventLink
    private Listener<EventPreMotion> pre = e -> {
        this.setMetadata(() -> StringUtil.formatEnum(this.mode.getValue()));
        if (this.mc.thePlayer.onGround) {
            this.shouldHop = true;
        }
        switch (this.mode.getValue()) {
            case FLAG: {
                if (!(this.mc.thePlayer.fallDistance > this.flagAfterBlocks.getValue().floatValue())) break;
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition());
                break;
            }
            case JUMP: {
                if (!(this.mc.thePlayer.fallDistance > this.flagAfterBlocks.getValue().floatValue())) break;
                this.mc.thePlayer.jump();
                break;
            }
            case GROUND: {
                e.setOnGround(true);
                break;
            }
            case POSITION: {
                if (!(this.mc.thePlayer.fallDistance > this.flagAfterBlocks.getValue().floatValue())) break;
                e.setY(e.getY() + (double)this.mc.thePlayer.fallDistance);
                break;
            }
            case TEST: {
                if (!this.isBlockUnder() && this.mc.thePlayer.fallDistance > 5.0f) {
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY - 20.0, this.mc.thePlayer.posZ, false));
                }
                if (!this.mc.thePlayer.onGround) break;
                this.xPos = this.mc.thePlayer.posX - this.mc.thePlayer.motionX;
                this.yPos = this.mc.thePlayer.posY;
                this.zPos = this.mc.thePlayer.posZ - this.mc.thePlayer.motionZ;
                break;
            }
            case VOID_HOP: {
                if (!(this.mc.thePlayer.fallDistance > this.flagAfterBlocks.getValue().floatValue()) || this.isBlockUnder() || !(this.mc.thePlayer.fallDistance > 6.0f) || this.mc.thePlayer.ticksExisted % 5 != 0 || this.mc.thePlayer.isDead || !this.shouldHop || !(this.mc.thePlayer.posY < 15.0)) break;
                this.mc.thePlayer.motionY = 3.0;
                this.shouldHop = false;
                break;
            }
            case NOBO_LION_TASTE_ANTIVOID: {
                this.setMetadata(() -> "$$ Blink Bypass $$");
                this.updateFlyHeight();
                if (this.mc.thePlayer.onGround) {
                    this.flagged = false;
                }
                if (!(this.flyHeight > 40.0) || !(this.mc.thePlayer.fallDistance > 0.0f) || Wrapper.getModule(Flight.class).isEnabled() || this.packets.isEmpty() || this.flagged || !(this.mc.thePlayer.motionY < 0.0)) break;
                Collections.reverse(this.packets);
                PacketUtil.sendPacketNoEvent(new C18PacketSpectate(this.mc.thePlayer.getUniqueID()));
                for (Packet value : this.packets) {
                    PacketUtil.sendPacketNoEvent(value);
                }
                this.packets.clear();
                Wrapper.getNotifManager().notify(NotificationType.INFO, "BLINK", "BLINKING ANTIVOIDING!!!! (NOVOLINE REFERENCE)");
                PlayerUtil.sendClientMessage("BLINKING ANTIVOIDING!!!! (NOVOLINE REFERENCE)");
            }
        }
    };
    @EventLink
    private Listener<EventPacket> packet = e -> {
        if (e.direction.equals((Object)EventPacket.Direction.SEND)) {
            switch (this.mode.getValue()) {
                case TEST: {
                    break;
                }
                case NOBO_LION_TASTE_ANTIVOID: {
                    Packet packet = e.getPacket();
                    if (!(packet instanceof C03PacketPlayer)) break;
                    if (!(this.flyHeight > 40.0) || !(this.mc.thePlayer.fallDistance > 0.0f) || Wrapper.getModule(Flight.class).isEnabled()) {
                        this.packets.add(packet);
                        if (this.packets.size() <= 5) break;
                        this.packets.remove(0);
                        break;
                    }
                    if (this.isBlockUnder() || !(this.mc.thePlayer.motionY < 0.0)) break;
                    e.setCancelled(true);
                }
            }
        }
    };

    public AntiVoid() {
        super("Anti Void", "Avoid the void.", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.shouldHop = false;
        this.packets.clear();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.packets.clear();
    }

    public void updateFlyHeight() {
        double h = 1.0;
        AxisAlignedBB box = this.mc.thePlayer.getEntityBoundingBox().expand(0.0625, 0.0625, 0.0625);
        this.flyHeight = 0.0;
        while (this.flyHeight < this.mc.thePlayer.posY) {
            AxisAlignedBB nextBox = box.offset(0.0, -this.flyHeight, 0.0);
            if (this.mc.theWorld.checkBlockCollision(nextBox)) {
                if (h < 0.0625) break;
                this.flyHeight -= h;
                h /= 2.0;
            }
            this.flyHeight += h;
        }
    }

    private boolean isBlockUnder() {
        int offset = 0;
        while ((double)offset < this.mc.thePlayer.posY - 10.0) {
            AxisAlignedBB boundingBox = this.mc.thePlayer.getEntityBoundingBox().offset(0.0, -offset, 0.0);
            if (!this.mc.theWorld.getCollidingBoundingBoxes(this.mc.thePlayer, boundingBox).isEmpty()) {
                return true;
            }
            offset += 2;
        }
        return false;
    }

    static enum Mode {
        FLAG,
        JUMP,
        GROUND,
        POSITION,
        TEST,
        VOID_HOP,
        NOBO_LION_TASTE_ANTIVOID;

    }
}

