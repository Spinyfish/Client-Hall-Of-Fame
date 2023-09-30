package de.verschwiegener.atero.module.modules.movement;


import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.TimeUtils;
import god.buddy.aot.BCompiler;

import java.awt.*;

public class AntiVoid extends Module {
    TimeUtils timeUtils;

    public AntiVoid() {
        super("AntiVoid", "AntiVoid", Keyboard.KEY_NONE, Category.Movement);
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
            setExtraTag("Watchdog");
            if (!isBlockUnder()) 	 {
                if (Minecraft.thePlayer.fallDistance > 4.5F) {
                    Minecraft.thePlayer.sendQueue.addToSendQueue((Packet) new C03PacketPlayer(true));
                }
                    if(mc.thePlayer.hurtTime != 0){
                        mc.thePlayer.motionY = 15F;
                    }
                }
            //    timeUtils.reset();
           // }
        }
    }

    public boolean isBlockUnder() {
        for (int i = (int) mc.thePlayer.posY;  i >= 0; --i) {
            BlockPos position = new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ);

            if (!(mc.theWorld.getBlockState(position).getBlock() instanceof BlockAir)) {
                return true;
            }
        }

        return false;
    }
}
