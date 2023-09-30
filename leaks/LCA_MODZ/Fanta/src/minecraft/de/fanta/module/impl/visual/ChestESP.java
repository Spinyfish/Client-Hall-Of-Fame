package de.fanta.module.impl.visual;



import java.awt.Color;
import java.util.List;

import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.module.Module;
import de.fanta.utils.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class ChestESP extends Module {
    public ChestESP() {
        super("Chestesp",0, Type.Visual, new Color(108, 2, 139));
    }

    @Override
    public void onEvent(Event event) {
    	for(Object o: mc.theWorld.loadedTileEntityList){
			if(o instanceof TileEntityChest){
			//	blockESPBox(((TileEntityChest)o).getPos());
			}
		}
	}
//    public void blockESPBox(BlockPos blockPos) {
//		double x = blockPos.getX() - Minecraft.getMinecraft().getRenderManager().renderPosX;
//		double y = blockPos.getY() - Minecraft.getMinecraft().getRenderManager().renderPosY;
//		double z = blockPos.getZ() - Minecraft.getMinecraft().getRenderManager().renderPosZ;
//		//GL11.glBlendFunc(770, 771);
//		GL11.glEnable(GL11.GL_LINE_SMOOTH);
//		//GL11.glEnable(GL11.GL_BLEND);
//		GL11.glLineWidth(1.0F);
//		GL11.glDisable(GL11.GL_LINE_SMOOTH);
//		GL11.glDisable(GL11.GL_TEXTURE_2D);
//		GL11.glDisable(GL11.GL_DEPTH_TEST);
//		GL11.glDepthMask(false);
//		{
//			GL11.glColor4d(0, 0, 1, 0.5);
//		}
//		RenderGlobal.func_181561_a(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
//		GL11.glEnable(GL11.GL_TEXTURE_2D);
//		GL11.glEnable(GL11.GL_DEPTH_TEST);
//		GL11.glDepthMask(true);
//		//GL11.glDisable(GL11.GL_BLEND);
//		GL11.glColor3f(1, 1, 1);
//	}
    
    public static void drawChestESP() {
        List<TileEntity> loadedTileEntityList = mc.getMinecraft().theWorld.loadedTileEntityList;
        for (int i = 0, loadedTileEntityListSize = loadedTileEntityList.size(); i < loadedTileEntityListSize; i++) {
            TileEntity tileEntity = loadedTileEntityList.get(i);
            if (tileEntity instanceof TileEntityChest) {     
                GlStateManager.disableTexture2D();
                TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity,
                        mc.getMinecraft().timer.renderPartialTicks, 1);
                GlStateManager.enableTexture2D();


            }
        }

    }
}
