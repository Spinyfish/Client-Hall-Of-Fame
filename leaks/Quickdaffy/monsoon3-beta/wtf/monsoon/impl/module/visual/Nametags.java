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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.EntityUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RoundedUtils;
import wtf.monsoon.impl.event.EventRender3D;
import wtf.monsoon.impl.event.EventRenderVanillaNametag;

public class Nametags
extends Module {
    private final Setting<Boolean> distanceScale = new Setting<Boolean>("DistanceScale", true).describedBy("Scale the nametag based on the distance");
    private final Setting<Float> scaleSetting = new Setting<Float>("Scale", Float.valueOf(0.2f)).minimum(Float.valueOf(0.1f)).maximum(Float.valueOf(1.0f)).incrementation(Float.valueOf(0.01f)).describedBy("The scale of the nametag");
    private final Setting<Boolean> health = new Setting<Boolean>("Health", true).describedBy("Render the player's health");
    @EventLink
    private final Listener<EventRender3D> render3DListener = event -> this.mc.theWorld.playerEntities.forEach(player -> {
        if (player == this.mc.thePlayer && this.mc.gameSettings.thirdPersonView < 1 || player.isInvisible()) {
            return;
        }
        double[] renderValues = new double[]{this.mc.getRenderManager().renderPosX, this.mc.getRenderManager().renderPosY, this.mc.getRenderManager().renderPosZ};
        Vec3 vec = EntityUtil.getInterpolatedPosition(player);
        double distance = this.mc.thePlayer.getDistance(vec.x, vec.y, vec.z);
        float scale = this.scaleSetting.getValue().floatValue() * 5.0f / 50.0f;
        if (this.distanceScale.getValue().booleanValue()) {
            scale = (float)(Math.max((double)(this.scaleSetting.getValue().floatValue() * 5.0f), (double)this.scaleSetting.getValue().floatValue() * distance) / 50.0);
        }
        GL11.glPushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.disableLighting();
        GL11.glTranslated((double)(vec.x - renderValues[0]), (double)(vec.y + (double)player.height + 0.1 + (player.isSneaking() ? 0.05 : 0.08) - renderValues[1]), (double)(vec.z - renderValues[2]));
        GL11.glRotated((double)(-this.mc.getRenderManager().playerViewY), (double)0.0, (double)1.0, (double)0.0);
        GL11.glRotated((double)this.mc.getRenderManager().playerViewX, (double)(this.mc.gameSettings.thirdPersonView == 2 ? -1 : 1), (double)0.0, (double)0.0);
        GL11.glScaled((double)(-scale), (double)(-scale), (double)scale);
        GL11.glDisable((int)2929);
        StringBuilder builder = new StringBuilder();
        builder.append(player.getCommandSenderName());
        if (this.health.getValue().booleanValue()) {
            builder.append(" ").append((Object)EntityUtil.getTextColourFromEntityHealth(player)).append((float)Math.round(EntityUtil.getTotalHealth(player)) / 2.0f).append((Object)EnumChatFormatting.WHITE).append(" ");
        }
        float width = Wrapper.getFont().getStringWidth(builder.toString());
        GL11.glTranslated((double)(-width / 2.0f), (double)-20.0, (double)0.0);
        float size = 2.0f;
        RoundedUtils.round(-size, -size, (float)Wrapper.getFont().getStringWidth(builder.toString()) + size * 2.0f, (float)Wrapper.getFont().getHeight() + size * 2.0f, 3.0f, ColorUtil.interpolate(Wrapper.getPallet().getBackground(), ColorUtil.TRANSPARENT, 0.5));
        Wrapper.getFont().drawString(builder.toString(), 0.0f, 0.0f, Color.WHITE, false);
        GL11.glEnable((int)3008);
        GlStateManager.enableLighting();
        RenderHelper.disableStandardItemLighting();
        GL11.glEnable((int)2929);
        GL11.glDisable((int)3042);
        GL11.glPopMatrix();
    });
    @EventLink
    private final Listener<EventRenderVanillaNametag> eventRenderVanillaNametagListener = event -> {
        if (event.getEntity() instanceof EntityPlayer) {
            event.cancel();
        }
    };

    public Nametags() {
        super("Nametags", "Renders more info on a players nametag", Category.VISUAL);
    }
}

