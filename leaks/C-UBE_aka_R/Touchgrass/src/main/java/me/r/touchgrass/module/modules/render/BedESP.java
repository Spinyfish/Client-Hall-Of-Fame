package me.r.touchgrass.module.modules.render;

import com.darkmagician6.eventapi.EventTarget;
import me.r.touchgrass.utils.RenderUtil;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.module.modules.player.BedAura;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import me.r.touchgrass.events.EventRender3D;

import java.awt.*;

/**
 * Created by r on 27/02/2021
 */
@Info(name = "BedESP", category = Category.Render, description = "Draws a box around beds")
public class BedESP extends Module {

    public BedESP() {}

    @EventTarget
    public void onRender(EventRender3D e) {
        BlockPos blockPos = BedAura.pos;
        double x = blockPos.getX() - Minecraft.getMinecraft().getRenderManager().renderPosX;
        double y = blockPos.getY() - Minecraft.getMinecraft().getRenderManager().renderPosY;
        double z = blockPos.getZ() - Minecraft.getMinecraft().getRenderManager().renderPosZ;
        int id = Block.getIdFromBlock(mc.theWorld.getBlockState(blockPos).getBlock());
        if (id == 26) {
            Color c = new Color(Color.red.getRed(), Color.red.getGreen(), Color.red.getBlue(), 30);
            RenderUtil.renderBox(x + 0.5D, y - 0.5D, z + 0.5D, 1.0F, 0.6F, c);
        }

    }
}
