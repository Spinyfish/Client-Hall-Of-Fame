package me.jinthium.scripting.api.bindings;

import me.jinthium.scripting.api.objects.ScriptFramebuffer;
import me.jinthium.scripting.api.objects.ScriptShaderUtil;
import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.api.util.Util;
import me.jinthium.straight.impl.utils.animation.ContinualAnimation;
import me.jinthium.straight.impl.utils.font.AbstractFontRenderer;
import me.jinthium.straight.impl.utils.render.ColorUtil;
import me.jinthium.straight.impl.utils.render.GradientUtil;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import me.jinthium.straight.impl.utils.render.RoundedUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.openjdk.nashorn.api.scripting.JSObject;

import java.awt.*;

public class RenderBinding implements MinecraftInstance, Util {

    private AbstractFontRenderer getFont() {
        return normalFont20;
    }

    //
    //   Functions
    //

    public ScriptFramebuffer createFramebuffer() {
        return new ScriptFramebuffer();
    }

    public ContinualAnimation newContinualAnimation() {
        return new ContinualAnimation();
    }

    public Color applyOpacity(Color color, float alpha) {
        return ColorUtil.applyOpacity(color, alpha);
    }

    public Color interpolateColors(Color color1, Color color2, float percent) {
        return ColorUtil.interpolateColorC(color1, color2, percent);
    }

    public void drawGradientRect(float x, float y, float width, float height, float alpha, Color bottomLeft, Color topLeft, Color bottomRight, Color topRight) {
        GradientUtil.drawGradient(x, y, width, height, alpha, bottomLeft, topLeft, bottomRight, topRight);
    }

    public void drawGradientRectHorizontal(float x, float y, float width, float height, float alpha, Color left, Color right) {
        GradientUtil.drawGradientLR(x, y, width, height, alpha, left, right);
    }

    public void drawGradientRectVertical(float x, float y, float width, float height, float alpha, Color top, Color bottom) {
        GradientUtil.drawGradientTB(x, y, width, height, alpha, top, bottom);
    }

    public void applyGradient(float x, float y, float width, float height, float alpha, Color bottomLeft, Color topLeft, Color bottomRight, Color topRight, JSObject callback) {
        GradientUtil.applyGradient(x, y, width, height, alpha, bottomLeft, topLeft, bottomRight, topRight, () -> callback.call(null));
    }

    public void drawGradientRoundedRect(float x, float y, float width, float height, float radius, Color bottomLeft, Color topLeft, Color bottomRight, Color topRight) {
        RoundedUtil.drawGradientRound(x, y, width, height, radius, bottomLeft, topLeft, bottomRight, topRight);
    }

    public void drawGradientRoundHorizontal(float x, float y, float width, float height, float radius, Color left, Color right) {
        RoundedUtil.drawGradientHorizontal(x, y, width, height, radius, left, right);
    }

    public void drawGradientRoundVertical(float x, float y, float width, float height, float radius, Color top, Color bottom) {
        RoundedUtil.drawGradientVertical(x, y, width, height, radius, top, bottom);
    }

    public void drawRoundedRect(float x, float y, float width, float height, float radius, Color color) {
        RoundedUtil.drawRound(x, y, width, height, radius, color);
    }

    public void drawRect(float x, float y, float width, float height, Color color) {
        Gui.drawRect2(x, y, width, height, color.getRGB());
    }


    public void renderItem(int itemSlot, int x, int y) {
        ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(itemSlot).getStack();
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        RenderHelper.disableStandardItemLighting();
    }

    public float getPartialTicks() {
        return mc.timer.renderPartialTicks;
    }

    public void scissorStart(double x, double y, double width, double height) {
        RenderUtil.scissorStart(x, y, width, height);
    }

    public void scissorEnd() {
        RenderUtil.scissorEnd();
    }

    public void drawEntity3D(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
        GuiInventory.drawEntityOnScreen(posX, posY, scale, mouseX, mouseY, ent);
    }

    public void drawEntity2D(float x, float y, float width, float height, EntityLivingBase player) {
        if (player instanceof AbstractClientPlayer) {
            mc.getTextureManager().bindTexture(((AbstractClientPlayer) player).getLocationSkin());
            GL11.glEnable(GL11.GL_BLEND);
            RenderUtil.resetColor();
            Gui.drawScaledCustomSizeModalRect(x, y, (float) 8.0, (float) 8.0, 8, 8, width, height, (float) 64.0, (float) 64.0);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    public float getScaledWidth() {
        return ScaledResolution.fetchResolution(this.mc).getScaledWidth();
    }

    public float getScaledHeight() {
        return ScaledResolution.fetchResolution(this.mc).getScaledHeight();
    }


    public ScriptShaderUtil createShaderUtil(String fragSource) {
        return new ScriptShaderUtil(fragSource);
    }

    public void bindMinecraftFramebuffer(){
        mc.getFramebuffer().bindFramebuffer(false);
    }

}
