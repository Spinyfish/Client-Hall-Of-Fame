package de.verschwiegener.atero.module.modules.world;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.events.callables.EventPreMotionUpdate;
import de.verschwiegener.atero.util.RotationRecode2;
import net.minecraft.client.Minecraft;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.TimeUtils;
import god.buddy.aot.BCompiler;

import java.awt.*;
import java.util.Random;

public class TEst extends Module {
    TimeUtils timeUtils;

    public TEst() {
        super("TEEST", "TEEST", Keyboard.KEY_NONE, Category.Movement);
    }

    public void onEnable() {
        // mc.gameSettings.keyBindSneak.pressed = true;
        super.onEnable();
    }

    public void onDisable() {
        //  mc.gameSettings.keyBindSneak.pressed = false;
        super.onDisable();
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)

    @EventTarget
    public void test(EventPreMotionUpdate test) {
        //   test.setYaw(Minecraft.thePlayer.rotationYaw+ 167);
        //   test.setPitch(87F);
    }

    public void onUpdate() {
        if (this.isEnabled()) {
            super.onUpdate();
            BlockPos bb = new BlockPos(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY - 0.0D, Minecraft.getMinecraft().thePlayer.posZ);
            mc.rightClickMouse();
            mc.rightClickDelayTimer = (int) 0F;
            if (mc.theWorld.getBlockState(bb).getBlock() == Blocks.air) {
                mc.gameSettings.keyBindSneak.pressed = true;
            }
        } else {
            mc.gameSettings.keyBindSneak.pressed = false;



        Minecraft.getMinecraft().rightClickDelayTimer = (int) 0F;
    }
}


}