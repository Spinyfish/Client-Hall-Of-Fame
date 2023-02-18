/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.S12PacketEntityVelocity
 *  net.minecraft.network.play.server.S27PacketExplosion
 *  vip.astroline.client.service.event.impl.packet.EventReceivePacket
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.value.FloatValue
 */
package vip.astroline.client.service.module.impl.combat;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import vip.astroline.client.service.event.impl.packet.EventReceivePacket;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.value.FloatValue;

public class Velocity
extends Module {
    public static FloatValue vertical = new FloatValue("Velocity", "Vertical", 0.0f, -100.0f, 100.0f, 1.0f);
    public static FloatValue horizontal = new FloatValue("Velocity", "Horizontal", 0.0f, -100.0f, 100.0f, 1.0f);

    public Velocity() {
        super("Velocity", Category.Combat, 0, false);
    }

    @EventTarget
    public void onPacket(EventReceivePacket event) {
        S12PacketEntityVelocity packet;
        if (event.getPacket() instanceof S12PacketEntityVelocity && (packet = (S12PacketEntityVelocity)event.getPacket()).getEntityID() == Velocity.mc.thePlayer.getEntityId()) {
            if (horizontal.getValue().floatValue() == 0.0f && vertical.getValue().floatValue() == 0.0f) {
                event.setCancelled(true);
            } else {
                packet.setMotionX((int)((double)((float)packet.getMotionX() * horizontal.getValue().floatValue()) / 100.0));
                packet.setMotionY((int)((double)((float)packet.getMotionY() * vertical.getValue().floatValue()) / 100.0));
                packet.setMotionZ((int)((double)((float)packet.getMotionZ() * horizontal.getValue().floatValue()) / 100.0));
            }
        }
        if (!(event.getPacket() instanceof S27PacketExplosion)) return;
        packet = (S27PacketExplosion)event.getPacket();
        if (horizontal.getValue().floatValue() == 0.0f && vertical.getValue().floatValue() == 0.0f) {
            event.setCancelled(true);
        } else {
            packet.setField_149152_f((float)((int)((double)(packet.func_149149_c() * horizontal.getValue().floatValue()) / 100.0)));
            packet.setField_149153_g((float)((int)((double)(packet.func_149144_d() * vertical.getValue().floatValue()) / 100.0)));
            packet.setField_149159_h((float)((int)((double)(packet.func_149147_e() * horizontal.getValue().floatValue()) / 100.0)));
        }
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }
}
