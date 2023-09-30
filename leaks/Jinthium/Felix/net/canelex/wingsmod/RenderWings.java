//This was translated by Autumn#4768 ALL THE RIGHTS FOR THE TRANSLATION GO TO ME, CREDITS FOR THE MOD TO CANELEX.
//RenderWings.class:
package net.canelex.wingsmod;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import felix.api.annotations.Handler;
import felix.events.render.EventRender3D;
import felix.module.Module;
import felix.module.impl.visuals.DragonWings;
import felix.util.visual.RenderUtil;
import felix.value.impl.BooleanValue;
import felix.value.impl.NumberValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class RenderWings extends ModelBase {
	
	public Minecraft mc;
    
    private ResourceLocation location;
    
    private ModelRenderer wing;
    
    private ModelRenderer wingTip;
    
    private final DragonWings dragonWings;
    
    public RenderWings(final DragonWings dragonWings) {
    	this.dragonWings = dragonWings;
        this.mc = Minecraft.getMinecraft();
        this.location = new ResourceLocation("minecraft", "wings.png");
        this.setTextureOffset("wing.bone", 0, 0);
        this.setTextureOffset("wing.skin", -10, 8);
        this.setTextureOffset("wingtip.bone", 0, 5);
        this.setTextureOffset("wingtip.skin", -10, 18);
        (this.wing = new ModelRenderer(this, "wing")).setTextureSize(30, 30);
        this.wing.setRotationPoint(-2.0f, 0.0f, 0.0f);
        this.wing.addBox("bone", -10.0f, -1.0f, -1.0f, 10, 2, 2);
        this.wing.addBox("skin", -10.0f, 0.0f, 0.5f, 10, 0, 10);
        (this.wingTip = new ModelRenderer((ModelBase)this, "wingtip")).setTextureSize(30, 30);
        this.wingTip.setRotationPoint(-10.0f, 0.0f, 0.0f);
        this.wingTip.addBox("bone", -10.0f, -0.5f, -0.5f, 10, 1, 1);
        this.wingTip.addBox("skin", -10.0f, 0.0f, 0.5f, 10, 0, 10);
        this.wing.addChild(this.wingTip);
    }
    
    public void onRenderPlayer(final EventRender3D event) {
        final EntityPlayer player = mc.thePlayer;
        if (player.equals((Object)this.mc.thePlayer) && !player.isInvisible()) {
            this.renderWings(player, event.getPartialTicks());
        }
    }
    
    private void renderWings(final EntityPlayer player, final float partialTicks) {
        final double scale = dragonWings.scale.getValue() / 100.0;
        EntityPlayerSP entityPlayer = (EntityPlayerSP) player;
        boolean rotating = entityPlayer.playerEvent.isRotating();
        final double rotate = rotating ? this.interpolate(entityPlayer.playerEvent.getPrevYaw(), entityPlayer.playerEvent.getYaw(), partialTicks) : this.interpolate(player.prevRenderYawOffset, player.renderYawOffset, partialTicks);
        GL11.glPushMatrix();
        GL11.glScaled(-scale, -scale, scale);
        GL11.glRotated(180.0 + rotate, 0.0, 1.0, 0.0);
        GL11.glTranslated(0.0, -(1.25) / scale, 0.0);
        GL11.glTranslated(0.0, 0.0, 0.2 / scale);
        if (player.isSneaking()) {
            GL11.glTranslated(0.0, 0.125 / scale, 0.0);
        }
        final float[] colors = this.getColors();
        GL11.glColor3f(colors[0], colors[1], colors[2]);
        this.mc.getTextureManager().bindTexture(this.location);
        for (int j = 0; j < 2; ++j) {
            GL11.glEnable(2884);
            final float f11 = System.currentTimeMillis() % 1000L / 1000.0f * 3.1415927f * 2.0f;
            this.wing.rotateAngleX = (float)Math.toRadians(-80.0) - (float)Math.cos(f11) * 0.2f;
            this.wing.rotateAngleY = (float)Math.toRadians(20.0) + (float)Math.sin(f11) * 0.4f;
            this.wing.rotateAngleZ = (float)Math.toRadians(20.0);
            this.wingTip.rotateAngleZ = -(float)(Math.sin(f11 + 2.0f) + 0.5) * 0.75f;
            this.wing.render(0.0625f);
            GL11.glScalef(-1.0f, 1.0f, 1.0f);
            if (j == 0) {
                GL11.glCullFace(1028);
            }
        }
        GL11.glCullFace(1029);
        GL11.glDisable(2884);
        GL11.glColor3f(255.0f, 255.0f, 255.0f);
        GL11.glPopMatrix();
    }
    
    public float[] getColors() {
        if (!dragonWings.colored.isEnabled())
          return new float[] { 1.0F, 1.0F, 1.0F }; 
        Color color = Color.getHSBColor(dragonWings.chroma.isEnabled() ? ((float)(System.currentTimeMillis() % 1000L) / 1000.0F) : (dragonWings.hue.getValue() / 100.0F), 0.8F, 1.0F);
        return new float[] { color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F };
      }
    
    private float interpolate(final float yaw1, final float yaw2, final float percent) {
        float f = (yaw1 + (yaw2 - yaw1) * percent) % 360.0f;
        if (f < 0.0f) {
            f += 360.0f;
        }
        return f;
    }
}
