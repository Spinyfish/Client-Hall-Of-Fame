package rip.athena.client.gui.clickgui.components.cosmetics;

import rip.athena.client.gui.framework.*;
import rip.athena.client.gui.clickgui.*;
import java.awt.*;
import rip.athena.client.gui.framework.draw.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import net.minecraft.entity.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;

public class CosmeticCapeView extends MenuComponent
{
    public CosmeticCapeView(final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.setPriority(MenuPriority.LOW);
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(46, 46, 48, IngameMenu.MENU_ALPHA));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(255, 255, 255, IngameMenu.MENU_ALPHA));
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int mouseX = this.parent.getMouseX();
        final int width = this.width;
        final int lineColor = this.getColor(DrawType.LINE, ButtonState.NORMAL);
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, ButtonState.NORMAL);
        final int textColor = this.getColor(DrawType.TEXT, ButtonState.NORMAL);
        DrawImpl.drawRect(x, y, width, this.height, backgroundColor);
        this.drawHorizontalLine(x, y, width + 1, 1, lineColor);
        this.drawVerticalLine(x, y + 1, this.height - 1, 1, lineColor);
        this.drawHorizontalLine(x, y + this.height, width + 1, 1, lineColor);
        this.drawVerticalLine(x + width, y + 1, this.height - 1, 1, lineColor);
        this.drawShadowUp(x, y, width + 1);
        this.drawShadowLeft(x, y, this.height + 1);
        this.drawShadowDown(x, y + this.height + 1, width + 1);
        this.drawShadowRight(x + width + 1, y, this.height + 1);
        GL11.glPushMatrix();
        GlStateManager.translate((float)(x + width / 2), (float)(y + this.height / 2 + 50), 125.0f);
        GlStateManager.scale(0.6, 0.6, 0.6);
        this.drawEntityOnScreen(100, -160.0f, Minecraft.getMinecraft().thePlayer);
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
