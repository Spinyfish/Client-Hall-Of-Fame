/*
 * Decompiled with CFR 0.152.
 */
package viamcp.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.WorldSettings;
import viamcp.ViaMCP;
import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.event.EventAttackEntity;

public class AttackOrder {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final int VER_1_8_ID = 47;

    public static void sendConditionalSwing(MovingObjectPosition mop) {
        if (mop != null && mop.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
            AttackOrder.mc.thePlayer.swingItem();
        }
    }

    public static void sendFixedAttack(EntityPlayer entityIn, Entity target) {
        if (ViaMCP.getInstance().getVersion() <= 47) {
            AttackOrder.send1_8Attack(entityIn, target);
        } else {
            AttackOrder.send1_9Attack(entityIn, target);
        }
    }

    private static void send1_8Attack(EntityPlayer entityIn, Entity targetEntity) {
        EventAttackEntity eventAttackEntity = new EventAttackEntity(targetEntity);
        Wrapper.getEventBus().post(eventAttackEntity);
        AttackOrder.mc.thePlayer.swingItem();
        AttackOrder.mc.playerController.syncCurrentPlayItem();
        AttackOrder.mc.playerController.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.ATTACK));
        if (AttackOrder.mc.playerController.currentGameType != WorldSettings.GameType.SPECTATOR) {
            entityIn.attackTargetEntityWithCurrentItem(targetEntity);
        }
    }

    private static void send1_9Attack(EntityPlayer entityIn, Entity targetEntity) {
        EventAttackEntity eventAttackEntity = new EventAttackEntity(targetEntity);
        Wrapper.getEventBus().post(eventAttackEntity);
        AttackOrder.mc.playerController.syncCurrentPlayItem();
        AttackOrder.mc.playerController.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.ATTACK));
        if (AttackOrder.mc.playerController.currentGameType != WorldSettings.GameType.SPECTATOR) {
            entityIn.attackTargetEntityWithCurrentItem(targetEntity);
        }
        AttackOrder.mc.thePlayer.swingItem();
    }
}

