package de.verschwiegener.atero.module.modules.render;

import java.awt.Color;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.events.callables.EventRender2D;

import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.Util;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSnow;
import net.minecraft.item.ItemSnowball;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class Trajectories extends Module{

    public Trajectories() {
	super("Trajectories", "Trajectories", Keyboard.KEY_NONE, Category.Render);
    }
    
    public void onRender() {
	//System.out.println("Event: " + event);
	double gravity = 0.05F;
	double velocity = 1.5F;
	
	float yaw = Util.toRadians(mc.thePlayer.rotationYaw);
	float pitch = Util.toRadians(mc.thePlayer.rotationPitch);
	
	//Entity Throwable L.70
	double posX = renderManager.renderPosX - MathHelper.cos((float) yaw) * 0.16F;
	double posY = renderManager.renderPosY + mc.thePlayer.getEyeHeight() - 0.10000000149011612D;
	double posZ = renderManager.renderPosZ - MathHelper.sin((float) yaw) * 0.16F;
	
	double motionX = 0, motionY = 0, motionZ = 0;
	
	Item helditem = mc.thePlayer.getCurrentEquippedItem().getItem();
	
	if(helditem instanceof ItemBow) {
	    if(mc.thePlayer.isUsingItem()) {
		System.out.println("Bow");
		//ItemBow L.33
		float f = mc.thePlayer.getItemInUseDuration() / 20.0F;
	        f = (f * f + f * 2.0F) / 3.0F;
		if ((double) f < 0.1D) {
		    return;
		}

		if (f > 1.0F) {
		    f = 1.0F;
		}
		velocity = f * 2.0F;
		// Gravity EntityArrow L.434
		gravity = 0.05F;
		
		motionX = -MathHelper.sin(yaw) * MathHelper.cos(pitch);
		motionZ = MathHelper.sin(yaw) * MathHelper.cos(pitch);
		motionY = -MathHelper.sin(pitch);
		
	    }
	}else if(helditem instanceof ItemPotion) {
	    if(((ItemPotion) helditem).isSplash(mc.thePlayer.getCurrentEquippedItem().getMetadata())) {
		gravity = 0.05D;
	    }
	}else if(helditem instanceof ItemSnowball || helditem instanceof ItemEnderPearl || helditem instanceof ItemEgg) {
	    //EntityThrowable L294
	    gravity = 0.03F;
	    motionX = -MathHelper.sin(yaw) * MathHelper.cos(pitch) * 0.4F;
	    motionZ = MathHelper.sin(yaw) * MathHelper.cos(pitch) * 0.4F;
	    motionY = -MathHelper.sin(pitch) * 0.4F;
	}
	
	//EntityThrowable L.104
	float f = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
	motionX = motionX / f;
	motionY = motionY / f;
	motionZ = motionZ / f;
	motionX *= velocity;
	motionY *= velocity;
	motionZ *= velocity;

	
	//Draw
	
	GL11.glDepthMask(false);
	//GL11.glEnable(GL11.GL_BLEND);
	GL11.glEnable(GL11.GL_LINE_SMOOTH);
	GL11.glDisable(GL11.GL_DEPTH_TEST);
	GL11.glDisable(GL11.GL_TEXTURE_2D);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);

	RenderUtil.setColor(Color.GREEN);

	GL11.glLineWidth(4f);

	Tessellator tessellator2 = Tessellator.getInstance();
	WorldRenderer arrowlinerenderer = tessellator2.getWorldRenderer();

	boolean landed = false;

	arrowlinerenderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);

	while (!landed && posY > 0.0) {
	    Vec3 vec31 = new Vec3(posX, posY, posZ);
	    Vec3 vec3 = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
	    MovingObjectPosition movingobjectposition = Minecraft.theWorld.rayTraceBlocks(vec31, vec3, false, true,
		    false);

	    vec31 = new Vec3(posX, posY, posZ);
	    vec3 = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);

	    if (movingobjectposition != null) {
		landed = true;
		vec3 = new Vec3(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord,
			movingobjectposition.hitVec.zCoord);
	    }

	    posX += motionX;
	    posY += motionY;
	    posZ += motionZ;

	    motionY -= gravity;
	    
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
	
    }
    

}
