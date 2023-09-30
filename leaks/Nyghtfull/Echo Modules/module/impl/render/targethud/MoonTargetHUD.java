package dev.echo.module.impl.render.targethud;


import dev.echo.module.impl.render.HUDMod;
import dev.echo.utils.animations.ContinualAnimation;
import dev.echo.utils.font.FontUtil;
import dev.echo.utils.render.ColorUtil;
import dev.echo.utils.render.RenderUtil;
import dev.echo.utils.render.RoundedUtil;
import dev.echo.utils.render.StencilUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import dev.echo.utils.font.CustomFont;

import java.awt.*;

public class MoonTargetHUD extends TargetHUD {

    private final ContinualAnimation animation = new ContinualAnimation();

    public MoonTargetHUD() {
        super("Moon");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        setWidth(Math.max(150, FontUtil.echoBoldFont16.getStringWidth(target.getName()) + 45));
        setHeight(42);

        Color c1 = ColorUtil.applyOpacity(new Color(HUDMod.getClientColors().getFirst().getRed(), HUDMod.getClientColors().getFirst().getGreen(), HUDMod.getClientColors().getFirst().getBlue()), alpha);
        Color c2 = ColorUtil.applyOpacity(new Color(HUDMod.getClientColors().getSecond().getRed(), HUDMod.getClientColors().getSecond().getGreen(), HUDMod.getClientColors().getSecond().getBlue()), alpha);

        Color color = new Color(HUDMod.getClientColors().getFirst().getRed(), HUDMod.getClientColors().getFirst().getGreen(), HUDMod.getClientColors().getFirst().getBlue(), (int) (25 * alpha));

        int textColor = ColorUtil.applyOpacity(-1, alpha);
        // ColorUtil.interpolateColorsBackAndForth(15, 0, new Color(HUDMod.getClientColors().getFirst().getRed(), HUDMod.getClientColors().getFirst().getGreen(), HUDMod.getClientColors().getFirst().getBlue(),100),new Color(HUDMod.getClientColors().getSecond().getRed(), HUDMod.getClientColors().getSecond().getGreen(), HUDMod.getClientColors().getSecond().getBlue(), 100), false);
        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 9, new Color(HUDMod.getClientColors().getFirst().darker().getRed(), HUDMod.getClientColors().getFirst().darker().getGreen(), HUDMod.getClientColors().getFirst().darker().getBlue(),100));

        if (target instanceof AbstractClientPlayer) {
            StencilUtil.initStencilToWrite();
            RenderUtil.renderRoundedRect(x + 3, y + 3, 36, 36, 9, -1);
            StencilUtil.readStencilBuffer(1);
            RenderUtil.color(-1, alpha);
            renderPlayer2D(x + 3, y + 3, 36, 36, (AbstractClientPlayer) target);
            StencilUtil.uninitStencilBuffer();
            GlStateManager.disableBlend();
        } else {
            FontUtil.echoBoldFont32.drawCenteredStringWithShadow("?", x + 20, y + 17 - FontUtil.echoBoldFont32.getHeight() / 2f, textColor);
        }

        FontUtil.echoBoldFont26.drawStringWithShadow(target.getName(), x + 43.5F, y + 4, textColor);

        //FontUtil.lithiumBoldFont16.drawStringWithShadow(mc.thePlayer.getHealth(), x + 44F, y + 18, 1)
        FontUtil.echoBoldFont20.drawStringWithShadow(Math.round(target.getHealth() + target.getAbsorptionAmount()) + " HP" , x + 44F, y + 18, textColor);




        float healthPercent = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);

        float realHealthWidth = getWidth() - 48;
        float realHealthHeight = 3;
        animation.animate(realHealthWidth * healthPercent, 18);
        Color backgroundHealthColor = new Color(0, 0, 0, ((int) alpha * 110));

        float healthWidth = animation.getOutput();

        RoundedUtil.drawRound(x + 44, (y + getHeight() - 8), 98, realHealthHeight, 1.5f, backgroundHealthColor);
        RoundedUtil.drawGradientHorizontal(x + 44, (y + getHeight() - 8), healthWidth, realHealthHeight, 1.5f, c1, c2);

    }


    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 9, ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), alpha));
    }

}
