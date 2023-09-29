package de.fanta.module.impl.visual;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import de.fanta.events.Event;
import de.fanta.events.listeners.EventRender3D;
import de.fanta.gui.font.BasicFontRenderer;
import de.fanta.module.Module;
import de.fanta.utils.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockCake;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class BlockESP extends Module {
	public BlockESP() {
		super("BlockESP", 0, Type.Visual, Color.green);
	}

	@Override
	public void onEvent(Event event) {
		if (event instanceof EventRender3D) {
			final int radius = 30;
			for (int y = -radius; y <= radius; y++) {
				for (int x = -radius; x <= radius; x++) {
					for (int z = -radius; z <= radius; z++) {
						final BlockPos blockPos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y,
								mc.thePlayer.posZ + z);
						final Block block = mc.theWorld.getBlockState(blockPos).getBlock();
						if (block instanceof BlockBed || block instanceof BlockCake) {
							BasicFontRenderer font = Minecraft.getMinecraft().fontRendererObj;
							float distance = (float) PlayerUtil.getDistanceToBlock(new BlockPos(x, y, z));

							blockESPBox(blockPos);
						}
					}
				}
			}
		}
	}

	public static void blockESPBox(BlockPos blockPos) {
		long delayCounter = 100L;

		double x = blockPos.getX() - Minecraft.getMinecraft().getRenderManager().renderPosX;
		double y = blockPos.getY() - Minecraft.getMinecraft().getRenderManager().renderPosY;
		double z = blockPos.getZ() - Minecraft.getMinecraft().getRenderManager().renderPosZ;
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glLineWidth(1.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		GL11.glColor4d(1, 0, 0, 0.5);
		RenderGlobal.func_181561_a(new AxisAlignedBB(x, y, z, x + 1.0, y, z + 1.0));
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor3f(1, 1, 1);

	}

}
