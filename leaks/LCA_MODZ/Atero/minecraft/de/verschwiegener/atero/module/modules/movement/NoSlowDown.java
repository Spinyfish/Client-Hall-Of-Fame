package de.verschwiegener.atero.module.modules.movement;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.events.callables.EventPostMotionUpdate;
import net.minecraft.client.Minecraft;

import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.TimeUtils;
import god.buddy.aot.BCompiler;

import java.awt.*;

public class NoSlowDown extends Module {
    TimeUtils timeUtils;

    public NoSlowDown() {
        super("NoSlowDown", "NoSlowDown", Keyboard.KEY_NONE, Category.Movement);
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void onUpdate() {
        if (this.isEnabled()) {
            super.onUpdate();
            setExtraTag("Motion");
            try {
                if (mc.thePlayer.isBlocking() && (mc.thePlayer.motionX != 0.0 || mc.thePlayer.motionZ != 0.0) && !mc.thePlayer.isEating()) {

                //    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                //            BlockPos.ORIGIN, EnumFacing.DOWN));
                }

    } catch (Exception e) {}


}


        }

    @EventTarget
    public void onPost(EventPostMotionUpdate post) {
        try {
        //    if (mc.thePlayer.isBlocking() && (mc.thePlayer.motionX != 0.0 || mc.thePlayer.motionZ != 0.0)&& !mc.thePlayer.isEating()) {

              //  mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
           // }
        } catch (Exception e) {}
    }



    }
