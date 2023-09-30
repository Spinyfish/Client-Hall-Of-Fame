/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.impl.module.visual;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.util.misc.MathUtils;
import wtf.monsoon.impl.event.EventPlayerHurtSound;
import wtf.monsoon.impl.event.EventRender2D;

public class HitMarkers
extends Module {
    private double currentAlpha = 255.0;
    @EventLink
    private final Listener<EventRender2D> onRender2D = e -> {
        double dif = Math.abs(this.currentAlpha);
        HitMarkers hitMarkers = this;
        int fps = hitMarkers.mc.getDebugFPS();
        if (dif > 0.0) {
            double animationSpeed = MathUtils.roundToPlace(Math.min(10.0, Math.max(0.005, 144.0 / (double)fps * 4.0)), 3);
            if (dif < animationSpeed) {
                animationSpeed = dif;
            }
            if (this.currentAlpha < 0.0) {
                this.currentAlpha += animationSpeed;
            }
            if (this.currentAlpha > 0.0) {
                this.currentAlpha -= animationSpeed;
            }
        }
        for (int i = 0; i < 4; ++i) {
            this.drawHitMarker(e.getSr());
        }
    };
    @EventLink
    private final Listener<EventPlayerHurtSound> eventPlayerHurtSoundListener = e -> {
        if (this.mc.thePlayer.getDistanceToEntity(e.getEntity()) <= 6.0f && this.mc.thePlayer.isSwingInProgress) {
            this.currentAlpha = 255.0;
            this.mc.thePlayer.playSound(this.mc.thePlayer.getHurtSound(), e.getEntity().getSoundVolume() / 1.5f, 1.0f);
            this.mc.thePlayer.playSound(this.mc.thePlayer.getHurtSound(), e.getEntity().getSoundVolume(), 2.0f);
            if (e.getEntity() instanceof EntityPlayer) {
                e.setCancelled(true);
            }
        }
    };

    public HitMarkers() {
        super("Hit Markers", "Show hit markers when you attack an entity.", Category.VISUAL);
    }

    private void drawHitMarker(ScaledResolution scaledResolution) {
        GL11.glDisable((int)3553);
        GL11.glEnable((int)3042);
        GL11.glEnable((int)2848);
        GL11.glLineWidth((float)2.0f);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)((float)this.currentAlpha / 255.0f));
        GL11.glBegin((int)1);
        GL11.glVertex2d((double)((double)scaledResolution.getScaledWidth() / 2.0 - 7.0), (double)((double)scaledResolution.getScaledHeight() / 2.0 - 7.0));
        GL11.glVertex2d((double)((double)scaledResolution.getScaledWidth() / 2.0 - 4.0), (double)((double)scaledResolution.getScaledHeight() / 2.0 - 4.0));
        GL11.glVertex2d((double)((double)scaledResolution.getScaledWidth() / 2.0 + 7.0), (double)((double)scaledResolution.getScaledHeight() / 2.0 + 7.0));
        GL11.glVertex2d((double)((double)scaledResolution.getScaledWidth() / 2.0 + 4.0), (double)((double)scaledResolution.getScaledHeight() / 2.0 + 4.0));
        GL11.glVertex2d((double)((double)scaledResolution.getScaledWidth() / 2.0 - 7.0), (double)((double)scaledResolution.getScaledHeight() / 2.0 + 7.0));
        GL11.glVertex2d((double)((double)scaledResolution.getScaledWidth() / 2.0 - 4.0), (double)((double)scaledResolution.getScaledHeight() / 2.0 + 4.0));
        GL11.glVertex2d((double)((double)scaledResolution.getScaledWidth() / 2.0 + 7.0), (double)((double)scaledResolution.getScaledHeight() / 2.0 - 7.0));
        GL11.glVertex2d((double)((double)scaledResolution.getScaledWidth() / 2.0 + 4.0), (double)((double)scaledResolution.getScaledHeight() / 2.0 - 4.0));
        GL11.glEnd();
        GL11.glEnable((int)3553);
    }
}

