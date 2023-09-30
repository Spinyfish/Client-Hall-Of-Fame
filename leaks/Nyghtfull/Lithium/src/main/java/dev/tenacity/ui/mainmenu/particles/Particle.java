package dev.tenacity.ui.mainmenu.particles;

import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.render.RenderUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * @author cedo
 * @since 05/23/2022
 */
@Getter
@Setter
public class Particle {
    private Animation fadeInAnimation;
    private Animation rotateAnimation = new DecelerateAnimation(10000, 1);
    private float x, y;
    private final float initialX, initialY, speed, rotation;
    private float ticks;
    private final ParticleImage particleImage;

    static int seed = 0;
    public Particle(ScaledResolution sr, ParticleImage particleImage) {
        Random random = new Random();
        this.particleImage = particleImage;
        float randomX = (sr.getScaledWidth()) + ((random.nextFloat() * (sr.getScaledWidth() / 2f)));
        float randomY = random.nextFloat() * (-sr.getScaledHeight());
        initialX = randomX + (randomX * (seed * .1f));
        initialY = randomY + (randomY * (seed * .1f));
        //Idk what this does
        ticks = random.nextFloat() * (sr.getScaledHeight() / 4f);


        speed = particleImage.getParticleType().equals(ParticleType.BIG) ? 1.5f : 3;
        rotation = random.nextFloat() * 360;

        seed++;
        if(seed > 7){
            seed = 0;
        }
    }

    public void draw() {
        if(fadeInAnimation == null) fadeInAnimation = new DecelerateAnimation(1000, 1);
        rotateAnimation.setDirection(fadeInAnimation.finished(Direction.FORWARDS) ? Direction.FORWARDS : Direction.BACKWARDS);
        float imgWidth = particleImage.getDimensions().getFirst() / 2f;
        float imgHeight = particleImage.getDimensions().getSecond() / 2f;

        float particleX = x + imgWidth/2f;
        float particleY = y + imgHeight/2f;
        RenderUtil.resetColor();
        RenderUtil.setAlphaLimit(0);
        RenderUtil.color(-1, (float) fadeInAnimation.getOutput().floatValue());
        GlStateManager.enableBlend();
        GL11.glPushMatrix();
        GL11.glTranslatef(particleX, particleY, 0);
        GL11.glRotatef((float) (rotation * rotateAnimation.getOutput().floatValue()), 0, 0, 1);
        GL11.glTranslatef(-particleX, -particleY, 0);
        RenderUtil.drawImage(particleImage.getLocation(), x, y, imgWidth, imgHeight);
        GL11.glPopMatrix();
    }


}

