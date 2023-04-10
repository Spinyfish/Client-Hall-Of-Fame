package crispy.features.hacks.impl.render;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.util.fbi.target.TargetManager;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.render.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;
import net.superblaubeere27.valuesystem.ModeValue;
import net.superblaubeere27.valuesystem.NumberValue;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@HackInfo(name = "Nametags", category = Category.RENDER)
public class Nametag extends Hack {
    ModeValue colorMode = new ModeValue("Color Option", "Custom", "Custom", "Health");

    NumberValue<Float> size = new NumberValue<Float>("Scale", 16f, 16f, 50f,() -> colorMode.getMode().equalsIgnoreCase("Custom"));
    NumberValue<Integer> Red = new NumberValue<Integer>("Red", 0, 0, 255, () -> colorMode.getMode().equalsIgnoreCase("Custom"));
    NumberValue<Integer> Green = new NumberValue<Integer>("Green", 0, 0, 255, () -> colorMode.getMode().equalsIgnoreCase("Custom"));
    NumberValue<Integer> Blue = new NumberValue<Integer>("Blue", 0, 0, 255, () -> colorMode.getMode().equalsIgnoreCase("Custom"));



    @Override
    public void onEvent(Event e) {

    }

    public void renderLivingLabel(Entity entityIn, String str, double x, double y, double z, int maxDistance) {
        try {
            if (entityIn instanceof EntityPlayer) {
                EntityPlayer p = (EntityPlayer) entityIn;
                double distance = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entityIn);
                FontRenderer var12 = Minecraft.fontRendererObj;
                float maxScale = -(size.getObject() / 1000);
                double factor = 4.0f + distance;
                float scale = factor > 4.0f ? (float) ((maxScale * factor) / 6.0f) : -0.03f;

                GlStateManager.pushMatrix();
                GlStateManager.translate((float) x + 0.0F, (float) y + entityIn.height + 0.5F, (float) z);
                GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(-RenderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(RenderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                GlStateManager.scale(scale, scale, scale);
                GlStateManager.disableLighting();
                GlStateManager.depthMask(false);
                GlStateManager.disableDepth();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                Tessellator var15 = Tessellator.getInstance();
                WorldRenderer var16 = var15.getWorldRenderer();
                byte var17 = 0;

                if (str.equals("deadmau5")) {
                    var17 = -10;
                }

                GlStateManager.disableTexture2D();
                var16.startDrawingQuads();
                int var18 = var12.getStringWidth(str) / 2;
                var16.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
                var16.addVertex(-var18 - 1, -1 + var17, 0.0D);
                var16.addVertex(-var18 - 1, 8 + var17, 0.0D);
                var16.addVertex(var18 + 1, 8 + var17, 0.0D);
                var16.addVertex(var18 + 1, -1 + var17, 0.0D);
                var15.draw();
                GlStateManager.enableTexture2D();

                Color color = new Color(Red.getObject(), Green.getObject(), Blue.getObject());
                if (colorMode.getMode().equalsIgnoreCase("Health")) {
                    color = ColorUtils.getHealthColor(p.getHealth(), p.getMaxHealth());
                    if (color == null) {
                        color = new Color(Red.getObject(), Green.getObject(), Blue.getObject());
                    }
                }

                String stripped = StringUtils.stripControlCodes(str);
                if(TargetManager.INSTANCE.getTargets().contains(entityIn)) {
                    stripped += " \2474(HACKER)";
                }
                var12.drawString(stripped, -var12.getStringWidth(str) / 2, var17, color.getRGB());
                GlStateManager.enableDepth();
                GlStateManager.depthMask(true);
                var12.drawString(stripped, -var12.getStringWidth(str) / 2, var17, color.getRGB());
                GlStateManager.enableLighting();
                GlStateManager.disableBlend();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.popMatrix();
            }
        } catch (Exception e) {

        }
    }
}
