package dev.tenacity.module.impl.render.targethud;

import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.utils.animations.ContinualAnimation;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.GLUtil;
import dev.tenacity.utils.render.RenderUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class NovolineTargetHUD extends TargetHUD {

    private final ContinualAnimation animation = new ContinualAnimation();

    public NovolineTargetHUD() {
        super("Novoline");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        IFontRenderer fr = mc.fontRendererObj;
        double healthPercentage = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);

        Color c1 = ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), alpha);
        Color c2 = ColorUtil.applyOpacity(HUDMod.getClientColors().getSecond(), alpha);

        setWidth(Math.max(120, fr.getStringWidth(target.getName()) + 50));
        setHeight(34);

        int alphaInt = (int) (alpha * 255);

        Gui.drawRect2(x, y, getWidth(), getHeight(), new Color(29, 29, 29, alphaInt).getRGB());

        Gui.drawRect2(x + 1, y + 1, getWidth() - 2, getHeight() - 2, new Color(40, 40, 40, alphaInt).getRGB());

        Gui.drawRect2(x + 34, y + 15, 83, 10, ColorUtil.applyOpacity(0xFF271E1D, alpha));

        float f = (float) (83 * healthPercentage);
        animation.animate(f, 40);

        RenderUtil.drawGradientRect(x + 34, y + 15, x + 34 + animation.getOutput(), y + 25, c1.darker().darker().getRGB(), c2.darker().darker().getRGB());
        RenderUtil.drawGradientRect(x + 34, y + 15, x + 34 + f, y + 25, c1.getRGB(), c2.getRGB());

        int textColor = ColorUtil.applyOpacity(-1, alpha);
        int mcTextColor = ColorUtil.applyOpacity(-1, (float) Math.max(.1, alpha));
        GLUtil.startBlend();
        if (target instanceof AbstractClientPlayer) {
            RenderUtil.color(textColor);
            renderPlayer2D(x + 3.5f, y + 3f, 28F, 28F, (AbstractClientPlayer) target);
        } else {
            fr.drawStringWithShadow("?", x + 17 - fr.getStringWidth("?") / 2f, y + 17 - fr.FONT_HEIGHT / 2f, mcTextColor);
        }

        GLUtil.startBlend();
        fr.drawStringWithShadow(target.getName(), x + 34, y + 5, mcTextColor);

        String healthText = MathUtils.round(healthPercentage * 100, .01) + "%";
        fr.drawStringWithShadow(healthText, x + 17 + (getWidth() / 2) - fr.getStringWidth(healthText) / 2f, y + 16, mcTextColor);
    }


    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        Gui.drawRect2(x, y, getWidth(), getHeight(), ColorUtil.applyOpacity(Color.BLACK.getRGB(), alpha));
    }

}
