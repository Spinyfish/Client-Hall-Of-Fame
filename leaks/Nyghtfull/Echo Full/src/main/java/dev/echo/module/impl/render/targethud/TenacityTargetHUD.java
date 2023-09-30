package dev.echo.module.impl.render.targethud;

import dev.echo.utils.animations.ContinualAnimation;
import dev.echo.utils.misc.MathUtils;
import dev.echo.utils.render.ColorUtil;
import dev.echo.utils.render.RenderUtil;
import dev.echo.utils.render.RoundedUtil;
import dev.echo.utils.render.StencilUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;

public class TenacityTargetHUD extends TargetHUD {

    public TenacityTargetHUD() {
        super("Echo");
    }

    private final ContinualAnimation animatedHealthBar = new ContinualAnimation();

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        int textColor = ColorUtil.applyOpacity(-1, alpha);
        setWidth(Math.max(155, echoBoldFont22.getStringWidth(target.getName()) + 75));
        setHeight(50);
        RenderUtil.setAlphaLimit(0);

        float colorAlpha = .8f * alpha;
        RoundedUtil.drawGradientRound(x, y, getWidth(), getHeight(), 6,
                ColorUtil.applyOpacity(colorWheel.getColor1(), colorAlpha),
                ColorUtil.applyOpacity(colorWheel.getColor4(), colorAlpha),
                ColorUtil.applyOpacity(colorWheel.getColor2(), colorAlpha),
                ColorUtil.applyOpacity(colorWheel.getColor3(), colorAlpha));


        float size = 38;
        if (target instanceof AbstractClientPlayer) {
            StencilUtil.initStencilToWrite();
            RenderUtil.circleNoSmoothRGB(x + 10, y + (getHeight() / 2f) - (size / 2f), size, -1);
            StencilUtil.readStencilBuffer(1);
            RenderUtil.resetColor();
            RenderUtil.setAlphaLimit(0);

            RenderUtil.color(textColor);
            renderPlayer2D(x + 10, y + (getHeight() / 2f) - (size / 2f), size, size, (AbstractClientPlayer) target);
            StencilUtil.uninitStencilBuffer();
        } else {
            echoBoldFont32.drawCenteredStringWithShadow("?", x + 30, y + 25 - echoBoldFont32.getHeight() / 2f, textColor);
        }

        echoBoldFont22.drawCenteredString(target.getName(), x + 10 + size + ((getWidth() - (10 + size)) / 2f), y + 10, textColor);

        float healthPercentage = (target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount());

        float healthBarWidth = getWidth() - (size + 30);

        float newHealthWidth = (healthBarWidth) * healthPercentage;
        animatedHealthBar.animate(newHealthWidth, 18);


        RoundedUtil.drawRound(x + 20 + size, y + 25, healthBarWidth, 4, 2, ColorUtil.applyOpacity(Color.BLACK, .3f * alpha));
        RoundedUtil.drawRound(x + 20 + size, y + 25, animatedHealthBar.getOutput(), 4, 2, ColorUtil.applyOpacity(Color.WHITE, alpha));

        String healthText = MathUtils.DF_0.format(healthPercentage * 100) + "%";

        echoFont18.drawCenteredString(healthText + " - " + Math.round(target.getDistanceToEntity(mc.thePlayer)) + "m",
                x + 10 + size + ((getWidth() - (10 + size)) / 2f), y + 35, textColor);
    }

    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        if (glow) {
            RoundedUtil.drawGradientRound(x, y, getWidth(), getHeight(), 6,
                    ColorUtil.applyOpacity(colorWheel.getColor1(), alpha),
                    ColorUtil.applyOpacity(colorWheel.getColor4(), alpha),
                    ColorUtil.applyOpacity(colorWheel.getColor2(), alpha),
                    ColorUtil.applyOpacity(colorWheel.getColor3(), alpha));
        } else {
            RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 6, ColorUtil.applyOpacity(Color.BLACK, alpha));
        }
    }
}
