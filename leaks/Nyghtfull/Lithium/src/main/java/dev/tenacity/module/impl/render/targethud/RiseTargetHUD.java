package dev.tenacity.module.impl.render.targethud;

import dev.tenacity.utils.animations.ContinualAnimation;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.render.*;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RiseTargetHUD extends TargetHUD {

    public RiseTargetHUD() {
        super("Rise");
    }

    private boolean sentParticles;
    public final List<Particle> particles = new ArrayList<>();
    private final TimerUtil timer = new TimerUtil();

    private final ContinualAnimation animatedHealthBar = new ContinualAnimation();

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        setWidth(Math.max(128, lithiumFont20.getStringWidth("Name: " + target.getName()) + 60));
        setHeight(50);

        int textColor = ColorUtil.applyOpacity(-1, alpha);

        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 6, new Color(0, 0, 0, (int) (110 * alpha)));
        final int scaleOffset = (int) (target.hurtTime * 0.35f);

        float healthPercent = (target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount());
        float var = (getWidth() - 28) * healthPercent;

        animatedHealthBar.animate(var, 18);

        GLUtil.startBlend();
        GradientUtil.drawGradientLR(x + 5, y + 40, animatedHealthBar.getOutput(), 5, alpha, colorWheel.getColor1(),
                colorWheel.getColor2());


        for (Particle p : particles) {
            //If tracking then the x value changes so we want to make it track the target aswell
            p.x = x + 20;
            p.y = y + 20;
            GlStateManager.color(1, 1, 1, 1);
            if (p.opacity > 4) p.render2D();
        }

        if (target instanceof AbstractClientPlayer) {
            final double offset = -(target.hurtTime * 23);
            RenderUtil.color(ColorUtil.applyOpacity(new Color(255, (int) (255 + offset), (int) (255 + offset)), alpha).getRGB());
            //renders face
            renderPlayer2D(x + 5 + scaleOffset / 2f, y + 5 + scaleOffset / 2f, 30 - scaleOffset, 30 - scaleOffset, (AbstractClientPlayer) target);
            GlStateManager.color(1, 1, 1, 1);
        }


        if (timer.hasTimeElapsed(1000 / 60, true)) {
            for (Particle p : particles) {
                p.updatePosition();
                if (p.opacity < 1) particles.remove(p);
            }
        }

        double healthNum = MathUtils.round(target.getHealth() + target.getAbsorptionAmount(), 1);
        GlStateManager.color(1, 1, 1, 1);
        lithiumFont18.drawString(String.valueOf(healthNum), x + animatedHealthBar.getOutput() + 8, y + 38, textColor);
        GlStateManager.color(1, 1, 1, 1);
        lithiumFont20.drawString("Name: " + target.getName(), x + 40, y + 10, textColor);
        GlStateManager.color(1, 1, 1, 1);
        lithiumFont20.drawString("Distance: " + MathUtils.round(mc.thePlayer.getDistanceToEntity(target), 1) + " Hurt: " + target.hurtTime,
                x + 40, y + 22, textColor);

        if (target.hurtTime == 9 && !sentParticles) {
            for (int i = 0; i <= 15; i++) {
                Particle particle = new Particle();
                particle.init(x + 20, y + 20, (float) (((Math.random() - 0.5) * 2) * 1.4), (float) (((Math.random() - 0.5) * 2) * 1.4),
                        (float) (Math.random() * 4), i % 2 == 0 ? colorWheel.getColor1() : colorWheel.getColor2());
                particles.add(particle);
            }
            sentParticles = true;
        }
        if (target.hurtTime == 8) sentParticles = false;
    }


    public static class Particle {
        public float x, y, adjustedX, adjustedY, deltaX, deltaY, size, opacity;
        public Color color;

        public void render2D() {
            RoundedUtil.drawRound(x + adjustedX, y + adjustedY, size, size, (size / 2f) - .5f, ColorUtil.applyOpacity(color, opacity / 255f));
        }

        public void updatePosition() {
            for (int i = 1; i <= 2; i++) {
                adjustedX += deltaX;
                adjustedY += deltaY;
                deltaY *= 0.97;
                deltaX *= 0.97;
                opacity -= 1f;
                if (opacity < 1) opacity = 1;
            }
        }

        public void init(float x, float y, float deltaX, float deltaY, float size, Color color) {
            this.x = x;
            this.y = y;
            this.deltaX = deltaX;
            this.deltaY = deltaY;
            this.size = size;
            this.opacity = 254;
            this.color = color;
        }
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
