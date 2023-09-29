package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import de.verschwiegener.atero.util.render.RenderUtil;

public class RenderArrow extends Render<EntityArrow>
{
    private static final ResourceLocation arrowTextures = new ResourceLocation("textures/entity/arrow.png");

    public RenderArrow(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     */
    public void doRender(EntityArrow entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.bindEntityTexture(entity);
       
        //TODO Draw Arrow trajectory after shot
        
        if(!entity.inGround && !entity.isDead) {
            GL11.glDepthMask(false);
            //GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            
            GlStateManager.resetColor();
            
            GL11.glLineWidth(2f);
            
            Tessellator tessellator2 = Tessellator.getInstance();
            WorldRenderer arrowlinerenderer = tessellator2.getWorldRenderer();
            
            boolean landed = false;
            double posX = entity.posX, posY = entity.posY, posZ = entity.posZ;
            
            double motionX = entity.motionX, motionY = entity.motionY, motionZ = entity.motionZ;
            
            arrowlinerenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
            int r = 255;
            
	    while (!landed && posY > 0.0) {
		GlStateManager.resetColor();
		Vec3 vec31 = new Vec3(posX, posY, posZ);
		Vec3 vec3 = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
		MovingObjectPosition movingobjectposition = Minecraft.theWorld.rayTraceBlocks(vec31, vec3, false, true,
			false);
		 
		vec31 = new Vec3(entity.posX, entity.posY, entity.posZ);
		vec3 = new Vec3(entity.posX + entity.motionX, entity.posY + entity.motionY, entity.posZ + entity.motionZ);
		
		if (movingobjectposition != null) {
		    landed = true;
		    vec3 = new Vec3(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord,
			    movingobjectposition.hitVec.zCoord);
		    break;
		}

		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		r --;
		if(r <= 127) {
		    r = 127;
		}

		motionY -= 0.05F;
		RenderUtil.setColor(new Color(255, r, 80));
		
		arrowlinerenderer.pos(posX - renderManager.renderPosX, posY - renderManager.renderPosY,
			posZ - renderManager.renderPosZ).endVertex();
	    }
            tessellator2.draw();
            GL11.glPushMatrix();
            GL11.glTranslated(posX - renderManager.renderPosX, posY - renderManager.renderPosY, posZ - renderManager.renderPosZ);
            GL11.glPopMatrix();
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glDepthMask(true);
            
            
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
        
        
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        int i = 0;
        float f = 0.0F;
        float f1 = 0.5F;
        float f2 = (float)(0 + i * 10) / 32.0F;
        float f3 = (float)(5 + i * 10) / 32.0F;
        float f4 = 0.0F;
        float f5 = 0.15625F;
        float f6 = (float)(5 + i * 10) / 32.0F;
        float f7 = (float)(10 + i * 10) / 32.0F;
        float f8 = 0.05625F;
        GlStateManager.enableRescaleNormal();
        float f9 = (float)entity.arrowShake - partialTicks;

        if (f9 > 0.0F)
        {
            float f10 = -MathHelper.sin(f9 * 3.0F) * f9;
            GlStateManager.rotate(f10, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(f8, f8, f8);
        GlStateManager.translate(-4.0F, 0.0F, 0.0F);
        GL11.glNormal3f(f8, 0.0F, 0.0F);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-7.0D, -2.0D, -2.0D).tex((double)f4, (double)f6).endVertex();
        worldrenderer.pos(-7.0D, -2.0D, 2.0D).tex((double)f5, (double)f6).endVertex();
        worldrenderer.pos(-7.0D, 2.0D, 2.0D).tex((double)f5, (double)f7).endVertex();
        worldrenderer.pos(-7.0D, 2.0D, -2.0D).tex((double)f4, (double)f7).endVertex();
        tessellator.draw();
        GL11.glNormal3f(-f8, 0.0F, 0.0F);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos(-7.0D, 2.0D, -2.0D).tex((double)f4, (double)f6).endVertex();
        worldrenderer.pos(-7.0D, 2.0D, 2.0D).tex((double)f5, (double)f6).endVertex();
        worldrenderer.pos(-7.0D, -2.0D, 2.0D).tex((double)f5, (double)f7).endVertex();
        worldrenderer.pos(-7.0D, -2.0D, -2.0D).tex((double)f4, (double)f7).endVertex();
        tessellator.draw();

        for (int j = 0; j < 4; ++j)
        {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, f8);
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldrenderer.pos(-8.0D, -2.0D, 0.0D).tex((double)f, (double)f2).endVertex();
            worldrenderer.pos(8.0D, -2.0D, 0.0D).tex((double)f1, (double)f2).endVertex();
            worldrenderer.pos(8.0D, 2.0D, 0.0D).tex((double)f1, (double)f3).endVertex();
            worldrenderer.pos(-8.0D, 2.0D, 0.0D).tex((double)f, (double)f3).endVertex();
            tessellator.draw();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityArrow entity)
    {
        return arrowTextures;
    }
}
