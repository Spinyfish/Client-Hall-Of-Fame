package dev.elysium.client.mods.impl.settings;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventMotion;
import dev.elysium.client.events.EventRenderHUD;
import dev.elysium.client.ui.elements.targethud.TargetInfo;
import dev.elysium.client.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.Display;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.util.Arrays;
import java.util.List;

public class TargetHUD extends Mod {

    long lastTime;
    public ModeSetting mode = new ModeSetting("Mode",this,"Elysium","Astolfo","Exhibition","Novoline","Test","Cross","Dev","Arad");
    public NumberSetting xs = new NumberSetting("X",-Display.getWidth()/2, Display.getWidth()/2,0,0.5,this);
    public NumberSetting ys = new NumberSetting("Y",-Display.getHeight()/2, Display.getHeight()/2,0,0.5,this);
    public BooleanSetting follow = new BooleanSetting("Follow",false, this);
    public boolean dragging;

    public TargetHUD() {
        super("TargetHUD","Displays your target", Category.SETTINGS);
    }

    @EventTarget
    public void onEventMotion(EventMotion e) {
        ScaledResolution sr = new ScaledResolution(mc);
        TargetInfo element = (TargetInfo) Elysium.getInstance().getHudManager().getByname(mode.getMode());
        if(element != null) {
            xs.min = -Display.getWidth() / 2 / sr.getScaleFactor();
            xs.max = Display.getWidth() / 2 / sr.getScaleFactor() - element.width;
            ys.min = -Display.getHeight() / 2 / sr.getScaleFactor();
            ys.max = Display.getHeight() / 2 / sr.getScaleFactor() - element.height;
        }
        if(mc.currentScreen instanceof GuiChat)
            Elysium.getInstance().addTarget(mc.thePlayer);
    }

    @EventTarget
    public void onEventRenderScreen(EventRenderHUD e) {

        ScaledResolution sr = new ScaledResolution(mc);
        float x = (float) (sr.getScaledWidth()/2 + xs.getValue());
        float y = (float) (sr.getScaledHeight()/2 + ys.getValue());

        int i = 0;

        long delta = System.currentTimeMillis() - lastTime;

        for(EntityLivingBase en : Elysium.getInstance().targets) {
            TargetInfo element = (TargetInfo) en.getElementByName(mode.getMode());
            if(follow.isEnabled() && en != mc.thePlayer) {
                float[] coords = enTo2D(en, (float) e.getPartialTicks());
                if(coords[0] != -69 && coords[1] != -69) {
                    float xv = sr.getScaledWidth() / 10;
                    float yv = sr.getScaledHeight() / 5;
                    coords[0] = (Math.round(coords[0] / xv) * xv * 0.8F) + sr.getScaledWidth() * 0.1F;
                    coords[1] = (Math.round(coords[1] / yv) * yv * 0.75F) + sr.getScaledHeight() * 0.1F;
                    int xmul = (coords[0] - element.followx) > 0 ? 1 : -1;
                    int ymul = (coords[1] - element.followy) > 0 ? 1 : -1;

                    int smoothness = 200;

                    if(!RenderUtils.isInViewFrustrum(en))
                        smoothness = 5000;

                    if(Math.abs(coords[0] - element.followx) > 4) element.followx += xmul * (delta*Math.abs(coords[0] - element.followx)/smoothness);
                    if(Math.abs(coords[1] - element.followy) > 4) element.followy += ymul * (delta*Math.abs(coords[0] - element.followx)/smoothness);
                    element.followx = Math.max(element.width / 2, element.followx); element.followx = Math.min(sr.getScaledWidth() - element.width / 2, element.followx);
                    element.followy = Math.max(element.height / 2, element.followy); element.followy = Math.min(sr.getScaledHeight() - element.height / 2, element.followy);
                }
                element.draw(Math.round((element.followx - element.width/2)*2)/2F,Math.round((element.followy - element.height/2)*2)/2F,en);
            } else
                element.draw(x,y + (element.height + 8)*i,en);
            i++;
        }
        lastTime = System.currentTimeMillis();
    }

    public float[] enTo2D(EntityLivingBase en, float partialTicks) {
        double posX = RenderUtils.interpolate(en.posX, en.lastTickPosX, partialTicks);
        double posY = RenderUtils.interpolate(en.posY, en.lastTickPosY, partialTicks) - 1;
        double posZ = RenderUtils.interpolate(en.posZ, en.lastTickPosZ, partialTicks);

        double width = en.width / 1.5;
        double height = en.getEyeHeight() * 1.3185;

        Vector4d pos = null;

        AxisAlignedBB aabb = new AxisAlignedBB(posX - width, posY, posZ - width, posX + width, posY + height, posZ + width);
        List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));

        mc.entityRenderer.setupCameraTransform((float) partialTicks, 0);

        for (Vector3d vector : vectors) {
            vector = RenderUtils.project(en.posX - mc.getRenderManager().viewerPosX, posY + height - mc.getRenderManager().viewerPosY, en.posZ - mc.getRenderManager().viewerPosZ);
            if (vector != null && vector.z >= 0.0 && vector.z < 1.0) {
                if (pos == null) {
                    pos = new Vector4d(vector.x, vector.y, vector.z, 0.0);
                }
                pos.x = Math.min(vector.x, pos.x);
                pos.y = Math.min(vector.y, pos.y);
                pos.z = Math.max(vector.x, pos.z);
                pos.w = Math.max(vector.y, pos.w);
            }
        }

        mc.entityRenderer.setupOverlayRendering();;

        if (pos != null) {
            final float x = (float) pos.x;
            final float x2 = (float) pos.z;
            final float y = (float) pos.y - 1;
            return new float[] {x,y};
        }
        return new float[] {-69,-69};
    }
}
