/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.combat;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Regen
extends Module {
    public Regen() {
        super("Regen", 0, Category.COMBAT);
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        if (Regen.mc.thePlayer.ticksExisted > 10) {
            if (Regen.mc.thePlayer.getHealth() > 20.0f && Regen.mc.thePlayer.hurtTime > 0) {
                this.sendPacket(new C03PacketPlayer(Regen.mc.thePlayer.onGround));
                this.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(Regen.mc.thePlayer.posX, Regen.mc.thePlayer.posY, Regen.mc.thePlayer.posZ, Regen.mc.thePlayer.onGround));
            }
        }
    }
}

