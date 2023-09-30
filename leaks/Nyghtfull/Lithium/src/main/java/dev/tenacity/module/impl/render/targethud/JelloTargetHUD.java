package dev.tenacity.module.impl.render.targethud;

import dev.tenacity.utils.animations.ContinualAnimation;
import dev.tenacity.utils.font.FontUtil;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import dev.tenacity.utils.render.StencilUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class JelloTargetHUD extends TargetHUD {

    private final ContinualAnimation animation = new ContinualAnimation();

    public JelloTargetHUD() {
        super("Jello");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        setWidth(Math.max(150, FontUtil.lithiumBoldFont26.getStringWidth(target.getName()) + 45));
        setHeight(42);

        Color c1 = ColorUtil.applyOpacity(new Color(255, 255, 255), alpha);
        Color c2 = ColorUtil.applyOpacity(new Color(215, 215, 215), alpha);

        Color color = new Color(255, 255, 255, (int) (25 * alpha));

        int textColor = ColorUtil.applyOpacity(-1, alpha);

        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 4, color);

        if (target instanceof AbstractClientPlayer) {
            StencilUtil.initStencilToWrite();
            RenderUtil.renderRoundedRect(x + 3, y + 3, 36, 36, 4, -1);
            StencilUtil.readStencilBuffer(1);
            RenderUtil.color(-1, alpha);
            renderPlayer2D(x + 3, y + 3, 36, 36, (AbstractClientPlayer) target);
            StencilUtil.uninitStencilBuffer();
            GlStateManager.disableBlend();
        } else {
            FontUtil.lithiumBoldFont32.drawCenteredStringWithShadow("?", x + 20, y + 17 - FontUtil.lithiumBoldFont32.getHeight() / 2f, textColor);
        }

        FontUtil.lithiumBoldFont26.drawStringWithShadow(target.getName(), x + 43.5F, y + 4, textColor);

        FontUtil.lithiumBoldFont16.drawStringWithShadow(
                target.getHealth() >= mc.thePlayer.getHealth() ? "Losing" : "Winning",
                x + 44F, y + 18, textColor
        );

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
        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 4, ColorUtil.applyOpacity(Color.BLACK, alpha));
    }

}
