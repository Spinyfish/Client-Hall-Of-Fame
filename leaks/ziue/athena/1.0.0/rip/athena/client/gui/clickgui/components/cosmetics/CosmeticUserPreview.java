package rip.athena.client.gui.clickgui.components.cosmetics;

import rip.athena.client.gui.framework.*;
import rip.athena.client.gui.framework.draw.*;
import rip.athena.client.gui.clickgui.*;
import java.awt.*;
import rip.athena.client.utils.render.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import net.minecraft.entity.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;

public class CosmeticUserPreview extends MenuComponent
{
    public CosmeticUserPreview(final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(46, 46, 48, IngameMenu.MENU_ALPHA));
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int mouseX = this.parent.getMouseX();
        final int width = this.width;
        final int lineColor = this.getColor(DrawType.LINE, ButtonState.NORMAL);
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, ButtonState.NORMAL);
        DrawUtils.drawRoundedRect(x, y, x + width, y + this.height, 4.0f, new Color(50, 50, 50, 255).getRGB());
        DrawUtils.drawRoundedRect(x + 1, y + 1, x + width - 1, y + this.height - 1, 4.0f, new Color(35, 35, 35, 255).getRGB());
        GL11.glPushMatrix();
        GlStateManager.translate((float)(x + width / 2), (float)(y + this.height / 2 + 120), 125.0f);
        GlStateManager.scale(1.25, 1.25, 1.25);
        final int speed = 15;
        final float rotateDelta = (float)(System.currentTimeMillis() % (360 * speed) / speed);
        this.drawEntityOnScreen(100, rotateDelta, Minecraft.getMinecraft().thePlayer);
        GL11.glPopMatrix();
    }
    
    private void drawEntityOnScreen(final int scale, final float rotate, final EntityPlayerSP ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(rotate, 0.0f, 1.0f, 0.0f);
        final float f = ent.renderYawOffset;
        final float f2 = ent.rotationYaw;
        final float f3 = ent.rotationPitch;
        final float f4 = ent.prevRotationYawHead;
        final float f5 = ent.rotationYawHead;
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-(float)Math.atan(0.0) * 20.0f, 1.0f, 0.0f, 0.0f);
        ent.renderYawOffset = (float)Math.atan(0.0) * 20.0f;
        ent.rotationYaw = (float)Math.atan(0.0) * 40.0f;
        ent.rotationPitch = -(float)Math.atan(0.0) * 20.0f;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        ent.dontRenderNameTag = true;
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        GlStateManager.disableLighting();
        GlStateManager.color(0.5f, 0.5f, 0.5f, 0.5f);
        rendermanager.setPlayerViewY(180.0f);
        rendermanager.setRenderShadow(false);
        rendermanager.doRenderEntity(ent, 0.0, 0.0, 0.0, 0.0f, 1.0f, false);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f2;
        ent.rotationPitch = f3;
        ent.prevRotationYawHead = f4;
        ent.rotationYawHead = f5;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
