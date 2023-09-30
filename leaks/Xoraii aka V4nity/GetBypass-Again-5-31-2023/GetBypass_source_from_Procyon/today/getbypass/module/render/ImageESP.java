// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.render;

import java.util.Iterator;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import today.getbypass.module.Category;
import net.minecraft.util.ResourceLocation;
import today.getbypass.module.Module;

public final class ImageESP extends Module
{
    private final ResourceLocation paris;
    
    public ImageESP() {
        super("ImageESP", 0, "ImageESP", Category.RENDER);
        this.paris = new ResourceLocation("GetBypass/esp/paris.jpg");
    }
    
    @Override
    public void onRender() {
        ResourceLocation optimizedResource = null;
        optimizedResource = this.paris;
        for (final EntityPlayer player : this.mc.theWorld.playerEntities) {
            if (player.isEntityAlive() && player != this.mc.thePlayer && !player.isInvisible()) {
                final double x = interp(player.posX, player.lastTickPosX) - Minecraft.getMinecraft().getRenderManager().renderPosX;
                final double y = interp(player.posY, player.lastTickPosY) - Minecraft.getMinecraft().getRenderManager().renderPosY;
                final double z = interp(player.posZ, player.lastTickPosZ) - Minecraft.getMinecraft().getRenderManager().renderPosZ;
                GlStateManager.pushMatrix();
                GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
                GL11.glDisable(2929);
                final float distance = MathHelper.clamp_float(this.mc.thePlayer.getDistanceToEntity(player), 20.0f, Float.MAX_VALUE);
                final double scale = 0.005 * distance;
                GlStateManager.translate(x, y, z);
                GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
                GlStateManager.scale(-0.1, -0.1, 0.0);
                this.mc.getTextureManager().bindTexture(this.paris);
                Gui.drawScaledCustomSizeModalRect(player.width / 2.0f - distance / 3.0f, -player.height - distance, 0.0f, 0.0f, 1.0, 1.0, 252.0 * (scale / 2.0), 476.0 * (scale / 2.0), 1.0f, 1.0f);
                GL11.glEnable(2929);
                GlStateManager.popMatrix();
            }
        }
    }
    
    public static double interp(final double newPos, final double oldPos) {
        return oldPos + (newPos - oldPos) * Minecraft.getMinecraft().timer.renderPartialTicks;
    }
}
