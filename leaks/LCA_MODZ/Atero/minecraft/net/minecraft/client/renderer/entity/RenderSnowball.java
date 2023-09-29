package net.minecraft.client.renderer.entity;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

public class RenderSnowball<T extends Entity> extends Render<T>
{
    protected final Item field_177084_a;
    private final RenderItem field_177083_e;

    public RenderSnowball(RenderManager renderManagerIn, Item p_i46137_2_, RenderItem p_i46137_3_)
    {
        super(renderManagerIn);
        this.field_177084_a = p_i46137_2_;
        this.field_177083_e = p_i46137_3_;
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     */
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
	
	GL11.glDepthMask(false);
	// GL11.glEnable(GL11.GL_BLEND);
	GL11.glEnable(GL11.GL_LINE_SMOOTH);
	GL11.glDisable(GL11.GL_DEPTH_TEST);
	GL11.glDisable(GL11.GL_TEXTURE_2D);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

	RenderUtil.setColor(Color.RED);

	GL11.glLineWidth(2f);

	Tessellator tessellator2 = Tessellator.getInstance();
	WorldRenderer arrowlinerenderer = tessellator2.getWorldRenderer();

	boolean landed = false;
	double posX = entity.posX, posY = entity.posY, posZ = entity.posZ;

	double motionX = entity.motionX, motionY = entity.motionY, motionZ = entity.motionZ;

	arrowlinerenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);

	while (!landed && posY > 0.0) {
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
	    }

	    posX += motionX;
	    posY += motionY;
	    posZ += motionZ;

	    motionY -= 0.05F;

	    arrowlinerenderer.pos(posX - renderManager.renderPosX, posY - renderManager.renderPosY,
		    posZ - renderManager.renderPosZ).endVertex();
	}
	tessellator2.draw();
	GL11.glPushMatrix();
	GL11.glTranslated(posX - renderManager.renderPosX, posY - renderManager.renderPosY,
		posZ - renderManager.renderPosZ);
	GL11.glPopMatrix();
	GL11.glColor4f(1F, 1F, 1F, 1F);
	GL11.glDepthMask(true);

	GL11.glEnable(GL11.GL_DEPTH_TEST);
	GL11.glEnable(GL11.GL_TEXTURE_2D);
            
	
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        this.bindTexture(TextureMap.locationBlocksTexture);
        this.field_177083_e.func_181564_a(this.func_177082_d(entity), ItemCameraTransforms.TransformType.GROUND);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public ItemStack func_177082_d(T entityIn)
    {
        return new ItemStack(this.field_177084_a, 1, 0);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity entity)
    {
        return TextureMap.locationBlocksTexture;
    }
}
