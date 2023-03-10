/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.EventUpdate;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity
extends Module {
    public ModeSetting mode = new ModeSetting("Mode", "Cancel", "Cancel", "Custom", "Glitch", "Reverse");
    private final NumberSetting horizontal = new NumberSetting("Horizontal", 0.0, 100.0, 0.0, 1.0);
    private final NumberSetting vertical = new NumberSetting("Vertical", 0.0, 100.0, 0.0, 1.0);

    public Velocity() {
        super("Velocity", 0, Category.COMBAT);
        this.addSettings(this.mode, this.horizontal, this.vertical);
    }

    @Override
    @Subscribe
    public void onGuiUpdate(EventUpdate e) {
        this.horizontal.setVisible(false);
        this.vertical.setVisible(false);
        if (this.mode.isMode("Custom")) {
            this.horizontal.setVisible(true);
            this.vertical.setVisible(true);
        }
    }

    @Subscribe
    public void onUpdate(lodomir.dev.event.impl.game.EventUpdate event) {
        if (this.mode.isMode("Custom")) {
            this.setSuffix(this.horizontal.getValueFloat() + "% " + this.vertical.getValueFloat() + "%");
        } else {
            this.setSuffix(this.mode.getMode());
        }
        super.onUpdate(event);
    }

    @Override
    @Subscribe
    public void onGetPacket(EventGetPacket event) {
        switch (this.mode.getMode()) {
            case "Glitch": {
                if (Velocity.mc.thePlayer != null && event.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity)event.getPacket()).getEntityID() == Velocity.mc.thePlayer.getEntityId()) {
                    event.setCancelled(true);
                    Velocity.mc.thePlayer.setPositionAndUpdate(Velocity.mc.thePlayer.posX, Velocity.mc.thePlayer.posY - 0.1, Velocity.mc.thePlayer.posZ);
                }
            }
            case "Cancel": {
                if (!(event.getPacket() instanceof S12PacketEntityVelocity) && !(event.getPacket() instanceof S27PacketExplosion)) break;
                event.setCancelled(true);
                break;
            }
            case "Reverse": {
                if (Velocity.mc.thePlayer == null || Velocity.mc.thePlayer.hurtTime <= 0 || !(event.getPacket() instanceof S12PacketEntityVelocity)) break;
                Velocity.mc.thePlayer.motionX = -Velocity.mc.thePlayer.motionX;
                Velocity.mc.thePlayer.motionZ = -Velocity.mc.thePlayer.motionZ;
                break;
            }
            case "Custom": {
                S12PacketEntityVelocity packet;
                if (!(event.getPacket() instanceof S12PacketEntityVelocity) || (packet = (S12PacketEntityVelocity)event.getPacket()).getEntityID() != Velocity.mc.thePlayer.getEntityId()) break;
                double horizontal = this.horizontal.getValue();
                double vertical = this.vertical.getValue();
                if (horizontal == 0.0 && vertical == 0.0) {
                    event.setCancelled(true);
                    return;
                }
                packet.motionX = (int)((double)packet.motionX * (horizontal / 100.0));
                packet.motionY = (int)((double)packet.motionY * (vertical / 100.0));
                packet.motionZ = (int)((double)packet.motionZ * (horizontal / 100.0));
                event.setPacket(packet);
                break;
            }
        }
    }
}

