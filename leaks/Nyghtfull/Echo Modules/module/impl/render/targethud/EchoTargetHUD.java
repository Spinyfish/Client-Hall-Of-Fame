package dev.echo.module.impl.render.targethud;

import dev.echo.module.impl.render.HUDMod;
import dev.echo.module.impl.render.Streamer;
import dev.echo.utils.animations.ContinualAnimation;
import dev.echo.utils.font.FontUtil;
import dev.echo.utils.render.ColorUtil;
import dev.echo.utils.render.RenderUtil;
import dev.echo.utils.render.RoundedUtil;
import dev.echo.utils.render.StencilUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class EchoTargetHUD extends TargetHUD {

    public EchoTargetHUD() {
        super("Echo");
    }

    private final ContinualAnimation animatedHealthBar = new ContinualAnimation();

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        setWidth(Math.max(145, echoBoldFont24.getStringWidth(target.getName()) + 40));
        setHeight(50);

        Color firstColor = ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), alpha);
        Color secondColor = ColorUtil.applyOpacity(HUDMod.getClientColors().getSecond(), alpha);

        Color color = ColorUtil.applyOpacity(Color.BLACK, 0.3F);

        int textColor = ColorUtil.applyOpacity(-1, alpha);

        StencilUtil.initStencilToWrite();
        RenderUtil.renderRoundedRect(x, y, getWidth(), getHeight(), 4, Color.WHITE.getRGB());
        StencilUtil.readStencilBuffer(1);

        Gui.drawRect2(x, y, getWidth(), getHeight(), color.getRGB());
        Gui.drawRect2(x, y, getWidth(), 15, color.getRGB());

        FontUtil.echoFont20.drawStringWithShadow(
                "Target",
                x + getWidth() / 2.0F - echoFont20.getStringWidth("Target") / 1.2F,
                y + echoFont20.getMiddleOfBox(15),
                textColor
        );

        FontUtil.echoBoldFont20.drawStringWithShadow(
                "Info",
                x + getWidth() / 2.0F + echoBoldFont20.getStringWidth("Info") / 2.2F,
                y + echoBoldFont20.getMiddleOfBox(15),
                textColor
        );

        if (target instanceof AbstractClientPlayer) {
            renderPlayer2D(x, y + 15, 35, 35, (AbstractClientPlayer) target);
        } else {
            Gui.drawRect2(x, y + 15, 35, 35, ColorUtil.applyOpacity(Color.WHITE.getRGB(), 0.5F));

            FontUtil.echoBoldFont32.drawCenteredStringWithShadow(
                    "?",
                    x + 20.5F - echoBoldFont32.getStringWidth("?") / 2.0F,
                    y + 32.5F - echoBoldFont32.getHeight() / 2.0F,
                    textColor
            );
        }

        echoBoldFont24.drawStringWithShadow(
                Streamer.enabled ? Streamer.customName.getString() : target.getName(),
                x + 40.0F,
                y + 20.5F,
                textColor
        );

        float healthPercent = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);

        float realHealthWidth = getWidth() - 45;
        float realHealthHeight = 5;
        animatedHealthBar.animate(realHealthWidth * healthPercent, 50);

        float healthWidth = animatedHealthBar.getOutput();

        RoundedUtil.drawRound(
                x + 40,
                y + getHeight() - 10,
                realHealthWidth,
                realHealthHeight,
                1.0F,
                color
        );

        RoundedUtil.drawGradientHorizontal(
                x + 40,
                y + getHeight() - 10,
                healthWidth,
                realHealthHeight,
                1.0F,
                firstColor,
                secondColor
        );

        StencilUtil.uninitStencilBuffer();
    }


    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        RoundedUtil.drawRound(
                x + 0.75F,
                y + 0.75F,
                getWidth() - 1.5F,
                getHeight() - 1.5F,
                4,
                Color.BLACK
        );
    }
}