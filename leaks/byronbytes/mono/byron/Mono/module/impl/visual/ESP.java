package byron.Mono.module.impl.visual;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import byron.Mono.event.impl.Event3D;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import byron.Mono.utils.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import com.google.common.eventbus.Subscribe;

@ModuleInterface(name = "ESP", description = "perhaps.", category = Category.Visual)
public class ESP extends Module{

	   @Subscribe
	    public void onUpdate(Event3D event) {
	            int amount = 0;
	            for (final EntityPlayer entity : mc.theWorld.playerEntities) {
	                if (entity != null) {
	                    final String name = entity.getName();
	                    if (!entity.isDead && entity != mc.thePlayer && !name.isEmpty() && !name.equals(" ")) {
	                        final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosX;
	                        final double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosY);
	                        final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - (mc.getRenderManager()).renderPosZ;

	                        GL11.glPushMatrix();
	                        GL11.glTranslated(x, y - 0.2, z);
	                        GlStateManager.disableDepth();

	                        GL11.glRotated(-(mc.getRenderManager()).playerViewY, 0.0D, 1.0D, 0.0D);

	                        final float width = 1.1f;
	                        final float height = 2.2f;

	                        final float lineWidth = 0.07f;

	                        draw2DBox(width, height, lineWidth, 0.04f, new Color(0, 0, 0, 165));

	                        if (entity.hurtTime > 0)
	                            draw2DBox(width, height, lineWidth, 0, new Color(255, 30, 30, 255));
	                        else
	                            draw2DBox(width, height, lineWidth, 0, new Color(255,255,255));

	                        GlStateManager.enableDepth();
	                        GL11.glPopMatrix();
	                        amount++;
	                    }
	                }
	            }
	        }
	   
	   private void draw2DBox(final float width, final float height, final float lineWidth, final float offset, final Color c) {
	        RenderUtil.rect(-width / 2 - offset, -offset, width / 4, lineWidth, c);
	        RenderUtil.rect(width / 2 - offset, -offset, -width / 4, lineWidth, c);
	        RenderUtil.rect(width / 2 - offset, height - offset, -width / 4, lineWidth, c);
	        RenderUtil.rect(-width / 2 - offset, height - offset, width / 4, lineWidth, c);

	        RenderUtil.rect(-width / 2 - offset, height - offset, lineWidth, -height / 4, c);
	        RenderUtil.rect(width / 2 - lineWidth - offset, height - offset, lineWidth, -height / 4, c);
	        RenderUtil.rect(width / 2 - lineWidth - offset, -offset, lineWidth, height / 4, c);
	        RenderUtil.rect(-width / 2 - offset, -offset, lineWidth, height / 4, c);
	    }
	   
		@Override
	    public void onEnable() {
	    	
	        super.onEnable();
	    }
		
	    
}
