package dev.tenacity.module.impl.render.targethud;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.utils.animations.ContinualAnimation;
import dev.tenacity.utils.render.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.text.DecimalFormat;

public class ZeroDayTargetHUD extends TargetHUD {

    private final ContinualAnimation animation = new ContinualAnimation();
    private final DecimalFormat DF_1O = new DecimalFormat("0.#");

    public ZeroDayTargetHUD() {
        super("ZeroDay");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        double healthPercentage = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);

        setWidth(145);
        setHeight(60);

        // Draw background
        Gui.drawRect2(x, y, getWidth(), getHeight(), new Color(0, 0, 0, (0.6F * alpha)).getRGB());

        // Draw player
        RenderUtil.resetColor();
        GuiInventory.drawEntityOnScreen((int) x + 18, (int) y + 48, 20, -target.rotationYaw, target.rotationPitch, target);
        RenderUtil.resetColor();

        // Colors
        int nameColor = new Color(255, 255, 255).getRGB();
        int infoColor = new Color(225, 225, 225).getRGB();

        // Draw name & info
        lithiumFont24.drawString(target.getName(), x + 36, y + 8, nameColor);

        lithiumFont16.drawString(
                "Health: " + Math.round(target.getHealth()),
                x + 36, y + 22,
                infoColor
        );

        lithiumFont16.drawString(
                "Range: " + Math.round(mc.thePlayer.getDistanceToEntity(target) - 0.5),
                x + 36, y + 30,
                infoColor
        );

        lithiumFont22.drawString(target.getHealth() < mc.thePlayer.getHealth() ? "Winning" : "Losing", x + 36, y + 40, nameColor);

        // damage anim
        float endWidth = (float) Math.max(0, getWidth() * healthPercentage);
        animation.animate(endWidth, 50);

        float healthWidth = animation.getOutput();

        RenderUtil.scissorStart(
                x, y + getHeight() - 2,
                healthWidth, 2
        );

        Gui.drawGradientRectSideways2(
                x, y + getHeight() - 2,
                getWidth() / 2, 2,
                new Color(255, 0, 0).getRGB(),
                new Color(255, 255, 0).getRGB()
        );

        Gui.drawGradientRectSideways2(
                x + getWidth() / 2, y + getHeight() - 2,
                getWidth() / 2, 2,
                new Color(255, 255, 0).getRGB(),
                new Color(0, 255, 0).getRGB()
        );

        RenderUtil.scissorEnd();
    }


    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        if (glow) {
            Gui.drawRect2(x, y, getWidth(), getHeight(), ColorUtil.applyOpacity(Color.BLACK.getRGB(), alpha));
        }
    }

}
