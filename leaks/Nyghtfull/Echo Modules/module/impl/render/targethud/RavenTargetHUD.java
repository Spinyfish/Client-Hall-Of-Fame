package dev.echo.module.impl.render.targethud;

import dev.echo.module.impl.render.HUDMod;
import dev.echo.utils.animations.ContinualAnimation;
import dev.echo.utils.render.ColorUtil;
import dev.echo.utils.render.RenderUtil;
import dev.echo.utils.render.RoundedUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.IFontRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.text.DecimalFormat;

public class RavenTargetHUD extends TargetHUD {
    String display;
    String health;
    DecimalFormat decimalFormat = new DecimalFormat("0.0");
    private final ContinualAnimation animation = new ContinualAnimation();

    public RavenTargetHUD() {
        super("Raven");
    }

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        IFontRenderer fr = mc.fontRendererObj;
        setWidth(fr.getStringWidth(display) + 5);
        setHeight(34);

        Color c1 = ColorUtil.applyOpacity(HUDMod.getClientColors().getFirst(), alpha);
        Color c2 = ColorUtil.applyOpacity(HUDMod.getClientColors().getSecond(), alpha);
        if (target instanceof AbstractClientPlayer) {
            if (target.getHealth() > 15) {
                health = String.valueOf(EnumChatFormatting.GREEN + decimalFormat.format(target.getHealth()));
            } else if (target.getHealth() > 10 && target.getHealth() < 15) {
                health = String.valueOf(EnumChatFormatting.YELLOW + decimalFormat.format(target.getHealth()));
            } else if (target.getHealth() > 5 && target.getHealth() < 10) {
                health = String.valueOf(EnumChatFormatting.RED + decimalFormat.format(target.getHealth()));
            } else if (target.getHealth() > 0 && target.getHealth() < 5) {
                health = String.valueOf(EnumChatFormatting.RED + decimalFormat.format(target.getHealth()));
            }
            if (target.getHealth() > mc.thePlayer.getHealth() && target.getHealth() != mc.thePlayer.getHealth()) {
                display = target.getName() + " " + health + EnumChatFormatting.RED + " L " + health;
            }
            if (target.getHealth() <= mc.thePlayer.getHealth()) {
                display = target.getName() + " " + health + EnumChatFormatting.GREEN + " W ";
            }
            RoundedUtil.drawRoundOutline(x, y, getWidth(), getHeight(), 8, 0.2F, new Color(0, 0, 0, 150), c1);
            fr.drawStringWithShadow(display, x + 5, y + 5, -1);
            double healthPercentage = MathHelper.clamp_float((target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount()), 0, 1);
            float f = (float) (83 * healthPercentage);
            animation.animate(f, 40);
            RenderUtil.drawGradientRect(x + 5, y + 20, x + animation.getOutput(), getHeight() - 27, c1.darker().darker().getRGB(), c2.darker().darker().getRGB());
             RoundedUtil.drawGradientHorizontal(x + 5, y + 20, getWidth() + animation.getOutput(), getHeight() - 27, 3, Color.white, c1);
        }
    }

    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
    }
}
