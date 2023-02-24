package me.kansio.client.modules.impl.visuals;

import com.google.common.eventbus.Subscribe;
import me.kansio.client.event.impl.Render3DEvent;
import me.kansio.client.modules.api.ModuleCategory;
import me.kansio.client.modules.api.ModuleData;
import me.kansio.client.modules.impl.Module;
import me.kansio.client.value.value.ModeValue;
import me.kansio.client.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@ModuleData(
        name = "Image ESP",
        description = "Displays images on players",
        category = ModuleCategory.VISUALS
)
public class ImageESP extends Module {

    private ModeValue modeValue = new ModeValue("Mode", this, "Zuiy", "Edp", "Floyed");

    private static final ResourceLocation ZUIY = new ResourceLocation("sleek/image/imageesp/Zuiy.png");
    private static final ResourceLocation EdP = new ResourceLocation("sleek/image/imageesp/Edp.png");
    private static final ResourceLocation FLOYED = new ResourceLocation("sleek/image/imageesp/floyd.png");

    @Subscribe
    public void onEntityRender(Render3DEvent event) {

        for (final EntityPlayer p : mc.thePlayer.getEntityWorld().playerEntities) {
            if (RenderUtils.isInViewFrustrum(p) && !p.isInvisible() && p.isEntityAlive()) {
                if (p == mc.thePlayer) {
                    continue;
                }
            }

            final double x = RenderUtils.interp(p.posX, p.lastTickPosX) - Minecraft.getMinecraft().getRenderManager().renderPosX;
            final double y = RenderUtils.interp(p.posY, p.lastTickPosY) - Minecraft.getMinecraft().getRenderManager().renderPosY;
            final double z = RenderUtils.interp(p.posZ, p.lastTickPosZ) - Minecraft.getMinecraft().getRenderManager().renderPosZ;
            GlStateManager.pushMatrix();
            GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
            GL11.glDisable(2929);
            final float distance = MathHelper.clamp_float(mc.thePlayer.getDistanceToEntity(p), 20.0f, Float.MAX_VALUE);
            final double scale = 0.005 * distance;
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            GlStateManager.scale(-0.1, -0.1, 0.0);
            switch (modeValue.getValue()) {
                case "Zuiy":
                    Minecraft.getMinecraft().getTextureManager().bindTexture(ZUIY);
                    break;
                case "Edp":
                    Minecraft.getMinecraft().getTextureManager().bindTexture(EdP);
                    break;
                case "Floyed":
                    Minecraft.getMinecraft().getTextureManager().bindTexture(FLOYED);
                    break;
            }

            Gui.drawScaledCustomSizeModalRect(p.width / 2.0f - distance / 3.0f, -p.height - distance, 0.0f, 0.0f, 1, 1, (int) (252.0 * (scale / 2.0)), (int) (476.0 * (scale / 2.0)), 1.0f, 1.0f);
            GL11.glEnable(2929);
            GlStateManager.popMatrix();
        }
    }
}
