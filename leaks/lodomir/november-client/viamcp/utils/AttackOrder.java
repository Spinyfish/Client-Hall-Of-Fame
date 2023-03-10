/*
 * Decompiled with CFR 0.152.
 */
package viamcp.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import viamcp.ViaMCP;
import viamcp.protocols.ProtocolCollection;

public class AttackOrder {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final int VER_1_8_ID = 47;

    public static void sendConditionalSwing(MovingObjectPosition mop) {
        if (mop != null && mop.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
            AttackOrder.mc.thePlayer.swingItem();
        }
    }

    public static void sendFixedAttack(EntityPlayer entityIn, Entity target) {
        if (ViaMCP.getInstance().getVersion() <= ProtocolCollection.getProtocolById(47).getVersion()) {
            AttackOrder.send1_8Attack(entityIn, target);
        } else {
            AttackOrder.send1_9Attack(entityIn, target);
        }
    }

    private static void send1_8Attack(EntityPlayer entityIn, Entity target) {
        AttackOrder.mc.thePlayer.swingItem();
        AttackOrder.mc.playerController.attackEntity(entityIn, target);
    }

    private static void send1_9Attack(EntityPlayer entityIn, Entity target) {
        AttackOrder.mc.playerController.attackEntity(entityIn, target);
        AttackOrder.mc.thePlayer.swingItem();
    }
}

