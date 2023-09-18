package v4n1ty.module.combat;

import de.Hero.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import v4n1ty.V4n1ty;
import v4n1ty.events.Event;
import v4n1ty.events.impl.EventMotion;
import v4n1ty.module.Category;
import v4n1ty.module.Module;
import v4n1ty.utils.misc.EntityHelper;
import v4n1ty.utils.misc.RayCast;
import v4n1ty.utils.misc.RotUtil;
import v4n1ty.utils.misc.TimerUtils;
import v4n1ty.utils.render.ColorUtils;
import v4n1ty.utils.render.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KillAura extends Module {

    public TimerUtils timer = new TimerUtils();
    public static EntityLivingBase target = null;

    public KillAura() {
        super("KillAura", Keyboard.KEY_K, Category.COMBAT);
    }

    @Override
    public void setup() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Full");
        options.add("Above");
        V4n1ty.settingsManager.rSetting(new Setting("Mark Mode", this, "Full", options));
        V4n1ty.settingsManager.rSetting(new Setting("Range", this, 4.3, 0, 6.0, false));
        V4n1ty.settingsManager.rSetting(new Setting("CPS", this, 15.4, 0, 25.0, false));
    }

    RenderManager renderManager = mc.getRenderManager();

    @Override
    public void onEnable() {
        long APS = (long) V4n1ty.settingsManager.getSettingByName("CPS").getValDouble();
    }

    @Override
    public void onDisable() {
        target = null;
    }

    public void FullBox() {
        EntityLivingBase player = (EntityLivingBase) target;


        if (mc.getRenderManager() == null || player == null) return;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glLineWidth(1.8F);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GlStateManager.depthMask(true);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        float partialTicks = mc.timer.renderPartialTicks;
        double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * partialTicks;
        double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * partialTicks;
        double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * partialTicks;

        float width = target.width;
        float height = target.height + (target.isSneaking() ? -0.2F : 0.1F);
        float fadeOffset = 0;
        RenderUtils.color(ColorUtils.withAlpha(V4n1ty.getHudColor(fadeOffset), 50));
        fadeOffset -= 3;
        RenderUtils.drawBoundingBox(new AxisAlignedBB(x - width + 0.25D, y + height + .1, z - width + 0.25D, x + width - 0.25D, y, z + width - 0.25D));
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
        RenderUtils.color(Color.WHITE);

    }

    public void AboveBox(){
        EntityLivingBase player = (EntityLivingBase) target;


        if (Minecraft.getMinecraft().getRenderManager() == null || player == null) return;

        float fadeOffset = 0;

        final Color color = player.hurtTime > 0 ? Color.red : V4n1ty.getHudColor(fadeOffset);
        fadeOffset -= 3;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glLineWidth(1.8F);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GlStateManager.depthMask(true);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        float partialTicks = Minecraft.getMinecraft().timer.renderPartialTicks;
        double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks + player.getEyeHeight() * 1.2;
        double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        float width = player.width;
        float height = player.height + (player.isSneaking() ? -0.2F : 0.1F);

        RenderUtils.color(ColorUtils.withAlpha(color, 40));
        RenderUtils.drawBoundingBox(new AxisAlignedBB(x - width / 1.75, y, z - width / 1.75,x + width / 1.75, y + .1, z + width / 1.75));
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
        RenderUtils.color(Color.WHITE);
    }

    private float[] getRotationsToEnt(Entity ent) {
        final double differenceX = ent.posX - mc.thePlayer.posX;
        final double differenceY = (ent.posY + ent.height) - (mc.thePlayer.posY + mc.thePlayer.height) - 0.5;
        final double differenceZ = ent.posZ - mc.thePlayer.posZ;
        final float rotationYaw = (float) (Math.atan2(differenceZ, differenceX) * 180.0D / Math.PI) - 90.0f;
        final float rotationPitch = (float) (Math.atan2(differenceY, mc.thePlayer.getDistanceToEntity(ent)) * 180.0D
                / Math.PI);
        final float finishedYaw = mc.thePlayer.rotationYaw
                + MathHelper.wrapAngleTo180_float(rotationYaw - mc.thePlayer.rotationYaw);
        final float finishedPitch = mc.thePlayer.rotationPitch
                + MathHelper.wrapAngleTo180_float(rotationPitch - mc.thePlayer.rotationPitch);
        return new float[]{finishedYaw, -MathHelper.clamp_float(finishedPitch, -90, 90)};
    }

    @Override
    public void onRender(){
        if(this.isToggled()) {
            if (target != null && V4n1ty.settingsManager.getSettingByName("Mark Mode").getValString().equalsIgnoreCase("Above")) {
                this.AboveBox();
            } else if (target != null && V4n1ty.settingsManager.getSettingByName("Mark Mode").getValString().equalsIgnoreCase("Full")){
                this.FullBox();
            }
        }
    }

    public void onEvent(Event e) {
        if(e instanceof EventMotion) {
            if(e.isPre()) {
                EventMotion event = (EventMotion)e;
                EntityLivingBase p = target = (EntityLivingBase) RayCast.raycast(mc, V4n1ty.settingsManager.getSettingByName("Range").getValDouble(), this.getTarget());
                if (p == null)
                    return;
                long APS = (long) V4n1ty.settingsManager.getSettingByName("CPS").getValDouble();
                float f[] = RotUtil.doBypassIRotations(target);
                event.setYaw(f[0]);
                event.setPitch(f[1]);
                if (this.timer.hasReached(1000L / APS)) {
                    mc.thePlayer.swingItem();
                    mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(p, C02PacketUseEntity.Action.ATTACK));
                    this.timer.reset();
                }
            }
        }
    }

    public EntityLivingBase getTarget() {
        for (EntityPlayer o : mc.theWorld.playerEntities) {
            if (o instanceof EntityPlayer && !o.isDead && o != mc.thePlayer && (mc.thePlayer.getDistanceToEntity(o) <= (mc.thePlayer.canEntityBeSeen(o) ? V4n1ty.settingsManager.getSettingByName("Range").getValDouble() : 3.1))) {
                return o;
            }
        }
        return null;
    }
}
