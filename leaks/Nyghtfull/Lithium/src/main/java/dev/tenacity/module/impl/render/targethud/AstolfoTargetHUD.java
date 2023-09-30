package dev.tenacity.module.impl.render.targethud;

import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.utils.animations.ContinualAnimation;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.GLUtil;
import dev.tenacity.utils.render.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.text.DecimalFormat;

public class AstolfoTargetHUD extends TargetHUD {

    private final ContinualAnimation animation = new ContinualAnimation();
    private final DecimalFormat DF_1O = new DecimalFormat("0.#");

    public AstolfoTargetHUD() {
        super("Astolfo");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        IFontRenderer fr = mc.fontRendererObj;
        //or 90
        float width = Math.max(50, fr.getStringWidth(target.getName()) + 130);
        double healthPercentage = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);
        setWidth(width);
        setHeight(55);

        Color c1 = ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), alpha);
        Color c2 = ColorUtil.applyOpacity(HUDMod.getClientColors().getSecond(), alpha);

        // Draw background
        Gui.drawRect2(x, y, width, 55, new Color(0, 0, 0, (0.6F * alpha)).getRGB());

        // Draw health bar (high quality code)
        RenderUtil.drawGradientRect(x + 34, y + 45, x + width - 4, y + 40, c1.darker().darker().darker().darker().getRGB(), c2.darker().darker().darker().darker().getRGB());

        // damage anim
        float endWidth = (float) Math.max(0, (width - 34) * healthPercentage);
        animation.animate(endWidth, 18);
        float healthWidth = animation.getOutput();

        RenderUtil.drawGradientRect(x + 34, y + 45, x + 30 + healthWidth, y + 53, c1.darker().darker().getRGB(), c2.darker().darker().getRGB());
        RenderUtil.drawGradientRect(x + 34, y + 45, x + 30 + Math.min(endWidth, healthWidth), y + 53, c1.getRGB(), c2.getRGB());

        // Draw player
        RenderUtil.resetColor();
        RenderUtil.color(-1, alpha);
        GuiInventory.drawEntityOnScreen((int) x + 17, (int) y + 50, 25, target.rotationYaw, target.rotationPitch, target);

        // Draw name
        RenderUtil.resetColor();
        GLUtil.startBlend();
        fr.drawStringWithShadow(target.getName(), x + 34, y + 4, ColorUtil.applyOpacity(-1, (float) Math.max(.1, alpha)));

        // Draw health
        float scale = 2.2F;
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, scale);
        RenderUtil.setAlphaLimit(0);
        RenderUtil.resetColor();
        fr.drawStringWithShadow(DF_1O.format(target.getHealth()) + " ❤", (x + 34) / scale, (y + 20) / scale,
                ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), (float) Math.max(.1, alpha)));
        GlStateManager.popMatrix();
    }


    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        Gui.drawRect2(x, y, getWidth(), 45, ColorUtil.applyOpacity(Color.BLACK.getRGB(), alpha));
    }

}
