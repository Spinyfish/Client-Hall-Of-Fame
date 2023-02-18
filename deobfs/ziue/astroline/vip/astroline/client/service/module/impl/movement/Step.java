/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.C03PacketPlayer$C04PacketPlayerPosition
 *  net.minecraft.network.play.client.C0BPacketEntityAction
 *  net.minecraft.network.play.client.C0BPacketEntityAction$Action
 *  vip.astroline.client.service.event.impl.move.EventStep
 *  vip.astroline.client.service.event.impl.move.EventUpdate
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.storage.utils.other.TimeHelper
 */
package vip.astroline.client.service.module.impl.movement;

import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import vip.astroline.client.service.event.impl.move.EventStep;
import vip.astroline.client.service.event.impl.move.EventUpdate;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.storage.utils.other.TimeHelper;

public class Step
extends Module {
    private boolean resetTimer;
    private TimeHelper timer = new TimeHelper();

    public Step() {
        super("Step", Category.Movement, 0, false);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        Step.mc.thePlayer.stepHeight = Step.mc.thePlayer.isInLiquid() || !Step.mc.thePlayer.onGround ? 0.625f : 1.0f;
        if (!this.resetTimer) return;
        Step.mc.timer.timerSpeed = 1.0f;
        this.resetTimer = false;
    }

    @EventTarget
    public void onStep(EventStep event) {
        double diffY = Step.mc.thePlayer.getEntityBoundingBox().minY - Step.mc.thePlayer.posY;
        double posX = Step.mc.thePlayer.posX;
        double posY = Step.mc.thePlayer.posY;
        double posZ = Step.mc.thePlayer.posZ;
        if (!(diffY > 0.625)) return;
        if (!(diffY <= 1.0)) return;
        mc.getNetHandler().getNetworkManager().sendPacketWithoutEvent((Packet)new C0BPacketEntityAction((Entity)Step.mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
        Step.mc.thePlayer.setSprinting(false);
        Step.mc.thePlayer.setSpeed(0.0);
        Step.mc.timer.timerSpeed = 0.3f;
        this.resetTimer = true;
        double first = 0.41999998688698;
        double second = 0.7431999805212;
        if (diffY != 1.0) {
            first *= diffY;
            second *= diffY;
            if (first > 0.425) {
                first = 0.425;
            }
            if (second > 0.78) {
                second = 0.78;
            } else if (second < 0.49) {
                second = 0.49;
            }
        }
        mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + first, posZ, false));
        mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C03PacketPlayer.C04PacketPlayerPosition(posX, posY + second, posZ, false));
        mc.getNetHandler().getNetworkManager().sendPacketWithoutEvent((Packet)new C0BPacketEntityAction((Entity)Step.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        Step.mc.thePlayer.stepHeight = 0.625f;
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        Step.mc.thePlayer.stepHeight = 0.5f;
        super.onDisable();
    }
}
