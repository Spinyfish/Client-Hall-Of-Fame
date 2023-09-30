/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.module.combat.Aura;

public class Criticals
extends Module {
    private final Setting<Mode> mode = new Setting<Mode>("Mode", Mode.PACKET).describedBy("The mode of the criticals.");
    @EventLink
    public final Listener<EventPacket> eventPacketListener = e -> {
        C02PacketUseEntity packet;
        if (e.getPacket() instanceof C02PacketUseEntity && (packet = (C02PacketUseEntity)e.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
            switch (this.mode.getValue()) {
                case NCP: {
                    if (!(packet.getEntityFromWorld(this.mc.theWorld) instanceof EntityPlayer) || !this.mc.thePlayer.onGround) break;
                    EntityPlayer target = (EntityPlayer)packet.getEntityFromWorld(this.mc.theWorld);
                    if (target.hurtTime >= 3) break;
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.1, this.mc.thePlayer.posZ, false));
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 1.0E-4, this.mc.thePlayer.posZ, false));
                    break;
                }
                case PACKET: {
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.1, this.mc.thePlayer.posZ, false));
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.02, this.mc.thePlayer.posZ, false));
                }
            }
        }
    };
    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        EntityLivingBase target = Wrapper.getModule(Aura.class).getTarget();
        switch (this.mode.getValue()) {
            case EDIT: {
                if (target == null) {
                    return;
                }
                if (target.hurtTime == 3) {
                    e.setY(e.getY() + 0.08);
                    e.setOnGround(false);
                    break;
                }
                if (target.hurtTime >= 3) break;
                e.setY(e.getY() + 0.001);
                e.setOnGround(false);
                break;
            }
        }
    };

    public Criticals() {
        super("Criticals", "Always land a critical hit.", Category.COMBAT);
        this.setMetadata(() -> StringUtil.formatEnum(this.mode.getValue()));
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    static enum Mode {
        PACKET("Packet"),
        EDIT("Edit"),
        NCP("NCP"),
        UPDATED_NCP("Updated NCP");

        String modeName;

        private Mode(String modeName) {
            this.modeName = modeName;
        }

        public String toString() {
            return this.modeName;
        }
    }
}

