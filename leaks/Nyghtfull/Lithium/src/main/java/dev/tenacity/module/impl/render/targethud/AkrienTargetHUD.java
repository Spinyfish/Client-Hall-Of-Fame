package dev.tenacity.module.impl.render.targethud;

import dev.tenacity.utils.animations.ContinualAnimation;
import dev.tenacity.utils.font.CustomFont;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.text.DecimalFormat;

public class AkrienTargetHUD extends TargetHUD {

    private final ContinualAnimation animation = new ContinualAnimation();
    private final DecimalFormat DF_1 = new DecimalFormat("0.0");

    public AkrienTargetHUD() {
        super("Akrien");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        CustomFont rubikBold = rubikFont.boldSize(18);
        CustomFont rubikSmall = rubikFont.size(13);
        setWidth(Math.max(100, rubikBold.getStringWidth(target.getName()) + 45));
        setHeight(39.5F);

        double healthPercentage = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);
        int bg = new Color(0, 0, 0, 0.4F * alpha).getRGB();

        // Draw background
        Gui.drawRect(x, y, x + getWidth(), y + 39.5F, bg);

        // Draw health bar
        Gui.drawRect2(x + 2.5, y + 31, getWidth() - 4.5, 2.5, bg);
        Gui.drawRect2(x + 2.5, y + 34.5, getWidth() - 4.5, 2.5, bg);

        // damage anim
        float endWidth = (float) Math.max(0, (getWidth() - 3.5) * healthPercentage);
        animation.animate(endWidth, 18);

        if (animation.getOutput() > 0) {
            RenderUtil.drawGradientRectBordered(x + 2.5, y + 31, x + 1.5 + animation.getOutput(), y + 33.5, 0.74,
                    ColorUtil.applyOpacity(0xFF009C41, alpha),
                    ColorUtil.applyOpacity(0xFF8EFFC1, alpha), bg, bg);
        }
        double armorValue = target.getTotalArmorValue() / 20.0;
        if (armorValue > 0) {
            RenderUtil.drawGradientRectBordered(x + 2.5, y + 34.5, x + 1.5 + ((getWidth() - 3.5) * armorValue), y + 37, 0.74,
                    ColorUtil.applyOpacity(0xFF0067B0, alpha),
                    ColorUtil.applyOpacity(0xFF39D5FF, alpha), bg, bg);
        }

        // Draw head
        GlStateManager.pushMatrix();
        RenderUtil.setAlphaLimit(0);
        int textColor = ColorUtil.applyOpacity(-1, alpha);
        if (target instanceof AbstractClientPlayer) {
            RenderUtil.color(textColor);
            float f = 0.8125F;
            GlStateManager.scale(f, f, f);
            renderPlayer2D(x / f + 3, y / f + 3, 32, 32, (AbstractClientPlayer) target);
        } else {
            Gui.drawRect2(x + 3, y + 3, 25, 25, bg);
            GlStateManager.scale(2, 2, 2);
            rubikBold.drawStringWithShadow("?", (x + 11) / 2.0F, (y + 11) / 2.0F, textColor);
        }
        GlStateManager.popMatrix();

        // Draw name
        rubikBold.drawString(target.getName(), x + 31, y + 5, textColor);
        rubikSmall.drawString("Health: " + DF_1.format(target.getHealth()), x + 31, y + 15, textColor);
        rubikSmall.drawString("Distance: " + DF_1.format(mc.thePlayer.getDistanceToEntity(target)) + "m", x + 31, y + 22, textColor);
    }


    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        Gui.drawRect(x, y, x + getWidth(), y + 39.5F, ColorUtil.applyOpacity(Color.BLACK.getRGB(), alpha));
    }

}
