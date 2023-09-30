package dev.echo.module.impl.render.targethud;

import dev.echo.module.impl.render.HUDMod;
import dev.echo.utils.animations.ContinualAnimation;
import dev.echo.utils.font.CustomFont;
import dev.echo.utils.misc.MathUtils;
import dev.echo.utils.render.ColorUtil;
import dev.echo.utils.render.GLUtil;
import dev.echo.utils.render.RenderUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class ExireTargetHUD extends TargetHUD {

    private final ContinualAnimation animation = new ContinualAnimation();

    public ExireTargetHUD() {
        super("Exire");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        CustomFont fr = sansFont.boldSize(28);
        setWidth(fr.getStringWidth(target.getName()) + 27);
        setHeight(30);
        double healthPercentage = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);
        Color c1 = ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), alpha);
        Color c2 = ColorUtil.applyOpacity(HUDMod.getClientColors().getSecond(), alpha);
        float width = fr.getStringWidth(target.getName()) + 27;
        float endWidth = (float) Math.max(0, (width - 34) * healthPercentage);
        animation.animate(endWidth, 15);
        float healthWidth = animation.getOutput();
        int alphaInt = (int) (alpha * 255);
        // Draw BackGround
        Gui.drawRect2(x + 1, y + 1, getWidth() - 2 + 3 + 2, getHeight() - 2 - 2, new Color(40, 40, 40, alphaInt).getRGB());

        // Draw health bar
        RenderUtil.drawGradientRect(x + 25.5F + 3, y + 19, x + width - 4 + 2 + 1 + 2, y + 25, new Color(0, 0, 0, 150).getRGB(), new Color(0, 0, 0, 150).getRGB());
        RenderUtil.drawGradientRect(x + 25.5F + 3, y + 19, x + 30 + healthWidth + 2 + 1 + 2, y + 25, c1.darker().darker().getRGB(), c2.darker().darker().getRGB());
        RenderUtil.drawGradientRect(x + 25.5F + 3, y + 19, x + 30 + Math.min(endWidth, healthWidth) + 2 + 2, y + 25, c1.getRGB(), c2.getRGB());

        // draw head
        int textColor = ColorUtil.applyOpacity(-1, alpha);
        int mcTextColor = ColorUtil.applyOpacity(-1, (float) Math.max(.1, alpha));
        GLUtil.startBlend();
        if (target instanceof AbstractClientPlayer) {
            RenderUtil.color(textColor);
            renderPlayer2D(x + 3.5f, y + 3f, 22F, 22F, (AbstractClientPlayer) target);
        }
        GLUtil.startBlend();

        // draw player name
        fr.drawStringWithShadow(target.getName(), x + 25.5F + 3, y + 4, mcTextColor);
    }

    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        Gui.drawRect2(x, y, getWidth(), getHeight(), ColorUtil.applyOpacity(Color.BLACK.getRGB(), alpha));
    }
}
