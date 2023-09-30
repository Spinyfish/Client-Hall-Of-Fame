/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.awt.Color;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.EntityUtil;
import wtf.monsoon.impl.event.EventRender3D;

public class Tracers
extends Module {
    public Setting<Boolean> players = new Setting<Boolean>("Players", true).describedBy("Show tracers for players");
    public Setting<Boolean> mobs = new Setting<Boolean>("Mobs", false).describedBy("Show tracers for mobs");
    public Setting<Boolean> passives = new Setting<Boolean>("Passives", false).describedBy("Show tracers for passives");
    public Setting<Boolean> neutral = new Setting<Boolean>("Neutral", false).describedBy("Show tracers for neutral entities");
    public Setting<Double> lineWidth = new Setting<Double>("LineWidth", 0.1).minimum(0.1).maximum(3.0).incrementation(0.1).describedBy("The width of the tracer line");
    public Setting<Float> alpha = new Setting<Float>("Alpha", Float.valueOf(255.0f)).minimum(Float.valueOf(0.0f)).maximum(Float.valueOf(255.0f)).incrementation(Float.valueOf(1.0f)).describedBy("The alpha of the tracer line");
    @EventLink
    private final Listener<EventRender3D> render3DListener = event -> this.mc.theWorld.loadedEntityList.forEach(entity -> {
        if (this.isAllowed((Entity)entity) && !entity.isInvisible()) {
            Vec3 vec = EntityUtil.getInterpolatedPosition(entity);
            double x = vec.x - this.mc.getRenderManager().viewerPosX;
            double y = vec.y - this.mc.getRenderManager().viewerPosY;
            double z = vec.z - this.mc.getRenderManager().viewerPosZ;
            Vec3 eyes = new Vec3(0.0, 0.0, 1.0).rotatePitch(-((float)Math.toRadians(this.mc.thePlayer.rotationPitch))).rotateYaw(-((float)Math.toRadians(this.mc.thePlayer.rotationYaw)));
            GL11.glDepthMask((boolean)false);
            GL11.glDisable((int)2929);
            GL11.glDisable((int)3008);
            GL11.glEnable((int)3042);
            GL11.glDisable((int)3553);
            GL11.glBlendFunc((int)770, (int)771);
            GL11.glEnable((int)2848);
            GL11.glHint((int)3154, (int)4354);
            Color colour = this.getColour((Entity)entity);
            GL11.glColor4f((float)((float)colour.getRed() / 255.0f), (float)((float)colour.getGreen() / 255.0f), (float)((float)colour.getBlue() / 255.0f), (float)(this.alpha.getValue().floatValue() / 255.0f));
            GL11.glLineWidth((float)this.lineWidth.getValue().floatValue());
            GL11.glBegin((int)1);
            GL11.glVertex3d((double)eyes.x, (double)(eyes.y + (double)this.mc.thePlayer.getEyeHeight()), (double)eyes.z);
            GL11.glVertex3d((double)x, (double)y, (double)z);
            GL11.glEnd();
            GL11.glDepthMask((boolean)true);
            GL11.glEnable((int)2929);
            GL11.glEnable((int)3553);
            GL11.glDisable((int)3042);
            GL11.glEnable((int)3008);
            GL11.glDisable((int)2848);
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
    });

    public Tracers() {
        super("Tracers", "Draws lines to entities", Category.VISUAL);
    }

    public Color getColour(Entity entity) {
        if (entity instanceof EntityOtherPlayerMP) {
            return new Color(0, 140, 255);
        }
        if (EntityUtil.isPassive(entity)) {
            return new Color(0, 255, 0);
        }
        if (EntityUtil.isHostile(entity)) {
            return new Color(255, 0, 0);
        }
        if (EntityUtil.isNeutral(entity)) {
            return new Color(255, 255, 255);
        }
        return new Color(0, 0, 0);
    }

    public boolean isAllowed(Entity entity) {
        if (entity instanceof EntityOtherPlayerMP) {
            return this.players.getValue();
        }
        if (EntityUtil.isPassive(entity)) {
            return this.passives.getValue();
        }
        if (EntityUtil.isHostile(entity)) {
            return this.mobs.getValue();
        }
        if (EntityUtil.isNeutral(entity)) {
            return this.neutral.getValue();
        }
        return false;
    }
}

