package tech.dort.dortware.impl.gui.vaziakclick;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import tech.dort.dortware.impl.gui.vaziakclick.element.Element;
import tech.dort.dortware.impl.gui.vaziakclick.element.impl.CategoryPane;

public class GuiUtils {

    public static boolean isHeaderHovering(int mouseX, int mouseY, CategoryPane pane) {
        if (pane == null)
            return false;
        return mouseX >= pane.getPosX() && mouseX < pane.getPosX() + pane.getWidth() &&
                mouseY >= pane.getPosY() && mouseY < pane.getPosY() + pane.headerHeight;
    }

    public static void drawRect1(double x, double y, double width, double height, int color) {
        float f = (color >> 24 & 0xFF) / 255.0F;
        float f1 = (color >> 16 & 0xFF) / 255.0F;
        float f2 = (color >> 8 & 0xFF) / 255.0F;
        float f3 = (color & 0xFF) / 255.0F;
        GL11.glColor4f(f1, f2, f3, f);
        Gui.drawRect(x, y, x + width, y + height, color);
    }

//    public static void drawFace(int x, int y, float u, float v, int uWidth, int vHeight, int width, int height, float tileWidth, float tileHeight, AbstractClientPlayer target) {
//        try {
//            ResourceLocation skin = target.getLocationSkin();
//            Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
//            GL11.glEnable(GL11.GL_BLEND);
//            GL11.glColor4f(1, 1, 1, 1);
//            Gui.drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
//            GL11.glDisable(GL11.GL_BLEND);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static boolean isHovering(int mouseX, int mouseY, Element element) {
        if (element == null)
            return false;
        return mouseX >= element.getPosX() && mouseX < element.getPosX() + element.getWidth() &&
                mouseY >= element.getPosY() && mouseY < element.getPosY() + element.getHeight();
    }

    public static boolean isHovering(int mouseX, int mouseY, int posX, int posY, int width, int height) {
        return mouseX >= posX && mouseX < posX + width &&
                mouseY >= posY && mouseY < posY + height;
    }


    public static void glResets() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
    }

//    //gets broken by font renderer for some reason, works fine without custom font IMPORTANT: DOESN'T SCALE
//    public static void drawStraightLineTest(int startPos, int endPos, int yPos, int thickness, int color) {
//        float a = (float) (color >> 24 & 255) / 255.0F;
//        float r = (float) (color >> 16 & 255) / 255.0F;
//        float g = (float) (color >> 8 & 255) / 255.0F;
//        float b = (float) (color & 255) / 255.0F;
//        GL11.glColor3f(r, g, b);
//        GL11.glLineWidth(thickness);
//        GL11.glBegin(GL_LINE_STRIP);
//        GL11.glVertex2d(startPos, yPos);
//        GL11.glVertex2d(endPos, yPos);
//        GL11.glEnd();
//    }

    public static void drawStraightLine(int startPos, int endPos, int yPos, int thickness, int color) {
        Gui.drawRect(startPos, yPos + thickness, endPos, yPos, color);
    }


    public static void glStartFontRenderer() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
    }

    public static void drawImage(ResourceLocation resourceLocation, float x, float y, float width, float height, int rgba) {
        float a = (float) (rgba >> 24 & 255) / 255.0F;
        float r = (float) (rgba >> 16 & 255) / 255.0F;
        float g = (float) (rgba >> 8 & 255) / 255.0F;
        float b = (float) (rgba & 255) / 255.0F;
        GL11.glPushMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        GlStateManager.color(r, g, b, a);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        Gui.drawScaledCustomSizeModalRect(x, y, 0, 0, width, height, width, height, width, height);
        GL11.glPopMatrix();
    }

    public static void drawRect(double left, double top, double right, double bottom, float opacity) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(.1F, .1F, .1F, opacity);
        worldrenderer.startDrawing(7);
        worldrenderer.addVertex(left, bottom, 0.0D);
        worldrenderer.addVertex(right, bottom, 0.0D);
        worldrenderer.addVertex(right, top, 0.0D);
        worldrenderer.addVertex(left, top, 0.0D);
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}
