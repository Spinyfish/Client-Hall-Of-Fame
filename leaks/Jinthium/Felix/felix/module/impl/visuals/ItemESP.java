package felix.module.impl.visuals;

import org.lwjgl.opengl.GL11;

import felix.api.annotations.Handler;
import felix.events.render.EventRender3D;
import felix.module.Module;
import felix.util.visual.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;

public class ItemESP extends Module {

	public ItemESP() {
		super("ItemESP", 0, ModuleCategory.VISUALS);
	}
	
	@Handler
	public void onRender3D(final EventRender3D event) {
		for (Object o : mc.theWorld.loadedEntityList) {
    		if (!(o instanceof EntityItem)) continue;
    		EntityItem item = (EntityItem)o;
 		   	double var10000 = item.posX;
 		   	RenderManager renderManager = mc.getRenderManager();
 		   	Minecraft.getMinecraft().getRenderManager();
 		   	double x = var10000 - renderManager.getRenderPosX();
 		   	var10000 = item.posY + 0.5D;
 		   	Minecraft.getMinecraft().getRenderManager();
 		   	double y = var10000 - renderManager.getRenderPosY();
 		   	var10000 = item.posZ;
 		   	Minecraft.getMinecraft().getRenderManager();
 		   	double z = var10000 - renderManager.getRenderPosZ();
 		   	GL11.glEnable(3042);
 		   	GL11.glLineWidth(2.0F);
 		   	GL11.glColor4f(1, 1, 1, .75F);
 		   	GL11.glDisable(3553);
 		   	GL11.glDisable(2929);
 		   	GL11.glDepthMask(false);
 	   		//RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x - .2D, y - 0.3d, z - .2D, x + .2D, y - 0.4d, z + .2D));
 	   		GL11.glColor4f(1, 1, 1, 0.15f);
 	   		RenderUtil.drawBoundingBox(new AxisAlignedBB(x - .2D, y - 0.3d, z - .2D, x + .2D, y - 0.4d, z + .2D));
 	   		GL11.glEnable(3553);
 	   		GL11.glEnable(2929);
 	   		GL11.glDepthMask(true);
 	   		GL11.glDisable(3042);
    	}
	}
}
