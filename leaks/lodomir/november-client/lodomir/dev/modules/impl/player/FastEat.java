/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.November;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import net.minecraft.network.play.client.C03PacketPlayer;

public class FastEat
extends Module {
    public FastEat() {
        super("FastEat", 0, Category.PLAYER);
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        if (FastEat.mc.thePlayer.isEating() && !November.INSTANCE.getModuleManager().getModule("Scaffold").isEnabled()) {
            this.sendPacket(new C03PacketPlayer(FastEat.mc.thePlayer.onGround));
            this.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(FastEat.mc.thePlayer.posX, FastEat.mc.thePlayer.posY, FastEat.mc.thePlayer.posZ, FastEat.mc.thePlayer.onGround));
        }
    }
}

