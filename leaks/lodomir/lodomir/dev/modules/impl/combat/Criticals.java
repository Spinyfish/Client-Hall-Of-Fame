/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.EventUpdate;
import lodomir.dev.event.impl.player.EventPreMotion;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.combat.KillAura;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.utils.math.TimeUtils;
import lodomir.dev.utils.player.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Criticals
extends Module {
    public ModeSetting mode = new ModeSetting("Mode", "Packet", "Packet", "Edit", "Hypixel");
    private final NumberSetting delay = new NumberSetting("Delay", 0.0, 1000.0, 500.0, 25.0);
    private final TimeUtils timer = new TimeUtils();

    public Criticals() {
        super("Criticals", 0, Category.COMBAT);
        this.addSetting(this.mode);
        this.addSetting(this.delay);
    }

    @Override
    @Subscribe
    public void onGuiUpdate(EventUpdate e) {
    }

    @Subscribe
    public void onUpdate(lodomir.dev.event.impl.game.EventUpdate event) {
        this.setSuffix(this.mode.getMode());
        super.onUpdate(event);
    }

    @Override
    @Subscribe
    public void onPreMotion(EventPreMotion event) {
        switch (this.mode.getMode()) {
            case "Packet": {
                double[] values;
                if (!Criticals.mc.thePlayer.onGround || KillAura.target == null || Criticals.mc.thePlayer.movementInput.jump || !this.timer.hasReached((long)this.delay.getValue()) || PlayerUtils.isOnLiquid() || Criticals.mc.thePlayer.isOnLadder()) break;
                for (double d : values = new double[]{0.0625, 0.001 - Math.random() / 10000.0}) {
                    this.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Criticals.mc.thePlayer.posX, Criticals.mc.thePlayer.posY + d, Criticals.mc.thePlayer.posZ, false));
                }
                break;
            }
            case "Edit": {
                EntityPlayer target = (EntityPlayer)KillAura.target;
                if (!Criticals.mc.thePlayer.onGround || target == null || Criticals.mc.thePlayer.movementInput.jump || !this.timer.hasReached((long)this.delay.getValue()) || PlayerUtils.isOnLiquid() || Criticals.mc.thePlayer.isOnLadder()) break;
                this.edit(event, target);
                break;
            }
            case "Hypixel": {
                EntityPlayer target = (EntityPlayer)KillAura.target;
                if (!Criticals.mc.thePlayer.onGround || target == null || Criticals.mc.thePlayer.movementInput.jump || !this.timer.hasReached((long)this.delay.getValue()) || PlayerUtils.isOnLiquid() || Criticals.mc.thePlayer.isOnLadder()) break;
                this.hypixel(event, target);
            }
        }
        this.timer.reset();
    }

    @Override
    public void onDisable() {
        this.timer.reset();
        super.onDisable();
    }

    private void hypixel(EventPreMotion e, EntityPlayer target) {
        if (Criticals.mc.thePlayer.onGround) {
            if (target.hurtTime == 2) {
                e.setY(e.getY() + 6.25E-6);
                e.setOnGround(false);
            } else if (target.hurtTime == 0) {
                e.setY(0.0);
                e.setOnGround(false);
            }
        }
    }

    private void edit(EventPreMotion e, EntityPlayer target) {
        if (target.hurtTime < 5) {
            e.setOnGround(true);
        } else if (target.hurtTime < 3) {
            e.setY(e.getY() + 0.01);
            e.setOnGround(false);
        } else {
            e.setY(e.getY() + 1.0E-5);
            e.setOnGround(false);
        }
    }
}

