package crispy.features.hacks.impl.render;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.impl.render.Event3D;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.features.hacks.impl.combat.Aura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.NumberValue;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

@HackInfo(name = "DamageParticles", category = Category.RENDER)
public class DamageParticles extends Hack {

    private final ArrayList<hit> hits = new ArrayList<hit>();
    private float lastHealth;
    private EntityLivingBase lastTarget = null;
    public static NumberValue<Float> hue = new NumberValue<Float>("Hue", 0.8F, 0F, 1.0F);
    public static BooleanValue superHero = new BooleanValue("SuperHero", true);

    private final static String[] heroEff = {
            "POW!", "WOW!", "BANG!", "BAM!", "POOOW!", "KA-POW!", "KABOOM", "POP", "BOOM!", "SUPER!", "ZAP!"
    };
    @Override
    public void onEvent(Event e) {
        if(e instanceof EventUpdate) {
            if (Aura.target == null) {
                this.lastHealth = 20;
                lastTarget = null;
                return;
            }
            if (this.lastTarget == null || Aura.target != this.lastTarget) {
                this.lastTarget = Aura.target;
                this.lastHealth = Aura.target.getHealth();
                return;
            }
            if (Aura.target.getHealth() != this.lastHealth) {
                if (Aura.target.getHealth() < this.lastHealth) {
                    this.hits.add(new hit(Aura.target.getPosition().add(ThreadLocalRandom.current().nextDouble(-0.5, 0.5), ThreadLocalRandom.current().nextDouble(1, 1.5), ThreadLocalRandom.current().nextDouble(-0.5, 0.5)), this.lastHealth - Aura.target.getHealth(), shuffleEff()));
                }
                this.lastHealth = Aura.target.getHealth();
            }

        } else if(e instanceof Event3D) {
            try {
                for (hit h : hits) {
                    if (h.isFinished()) {
                        hits.remove(h);
                    } else {
                        h.onRender();
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    public String shuffleEff() {
        return heroEff[Aura.randomNumber(heroEff.length - 1, 0)];
    }

}
class hit {
    protected static Minecraft mc = Minecraft.getMinecraft();
    private final BlockPos pos;
    private long startTime = System.currentTimeMillis();
    private final double healthVal;
    private final long maxTime = 1000;
    private final String pow;

    public hit(BlockPos pos, double healthVal, String pow) {
        this.startTime = System.currentTimeMillis();
        this.pos = pos;
        this.pow = pow;
        this.healthVal = healthVal;
    }

    public void onRender() {
        final double x = this.pos.getX() + (this.pos.getX() - this.pos.getX()) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX + 1.5;
        final double y = this.pos.getY() + (this.pos.getY() - this.pos.getY()) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY;
        final double z = this.pos.getZ() + (this.pos.getZ() - this.pos.getZ()) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;

        final float var10001 = (mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f;
        final double size = (2.5);
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        Minecraft.getMinecraft().entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 0);
        GL11.glTranslated(x, y, z);
        GL11.glNormal3f(0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-RenderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(RenderManager.playerViewX, var10001, 0.0f, 0.0f);
        GL11.glScaled(-0.01666666753590107 * size, -0.01666666753590107 * size, 0.01666666753590107 * size);
        float sizePercentage;
        long timeLeft = (this.startTime + this.maxTime) - System.currentTimeMillis();
        float yPercentage = 0;
        if (timeLeft < 75) {
            sizePercentage = Math.min((float) timeLeft / 75F, 1F);
            yPercentage = Math.min((float) timeLeft / 75F, 1F);
        } else {
            sizePercentage = Math.min((float) (System.currentTimeMillis() - this.startTime) / 300F, 1F);
            yPercentage = Math.min((float) (System.currentTimeMillis() - this.startTime) / 600F, 1F);
        }
        GlStateManager.scale(0.8 * sizePercentage, 0.8 * sizePercentage, 0.8 * sizePercentage);
        Gui.drawRect(-100, -100, 100, 100, new Color(255, 0, 0, 0).getRGB());
        Color c = Color.getHSBColor(DamageParticles.hue.getObject(), 1.0F, 1.0F);


        if(!DamageParticles.superHero.getObject()) {
            Crispy.INSTANCE.getFontManager().getFont("clean 25").drawStringWithShadow(new DecimalFormat("#.#").format(this.healthVal), 0, -(yPercentage * 1), c.getRGB());
        } else {
            int rainbow = getStaticRainbow(1000, 1);
            Minecraft.fontRendererObj.drawString(pow, 0, (int) -(yPercentage * 1), rainbow);
        }
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }

    private int getStaticRainbow(int speed, int offset) {
        float hue = 5000 + (System.currentTimeMillis() + offset) % speed;
        hue /= 5000;
        return Color.getHSBColor(hue, 0.65f, (float) .9).getRGB();
    }

    public boolean isFinished() {
        return System.currentTimeMillis() - this.startTime >= maxTime;
    }
}
