/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.ghost;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.impl.event.EventPreMotion;

public class AutoBridger
extends Module {
    @EventLink
    private final Listener<EventPreMotion> eventPreMotionListener = e -> {
        this.mc.gameSettings.keyBindSneak.pressed = this.playerOverAir();
    };

    public AutoBridger() {
        super("Auto Bridger", "Automatically bridges for you.", Category.GHOST);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.mc.gameSettings.keyBindSneak.pressed = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.mc.gameSettings.keyBindSneak.pressed = false;
    }

    private boolean playerOverAir() {
        double x = this.mc.thePlayer.posX;
        double y = this.mc.thePlayer.posY - 1.0;
        double z = this.mc.thePlayer.posZ;
        BlockPos p = new BlockPos(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
        return this.mc.theWorld.isAirBlock(p);
    }
}

