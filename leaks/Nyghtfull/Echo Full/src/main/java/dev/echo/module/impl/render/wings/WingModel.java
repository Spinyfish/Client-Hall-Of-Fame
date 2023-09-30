package dev.echo.module.impl.render.wings;

import dev.echo.module.impl.render.CustomModel;
import dev.echo.utils.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Ported from canelex's Wings Mod (<a href="https://youtu.be/5_iZ4sxymu0">...</a>)
 *
 * @author senoe
 * @since 6/2/2022
 */
public class WingModel extends ModelBase {

    private final ResourceLocation location = new ResourceLocation("Echo/Models/wings.png");
    private final ModelRenderer wingTip, wing;

    public WingModel() {
        setTextureOffset("wing.bone", 0, 0);
        setTextureOffset("wing.skin", -10, 8);
        setTextureOffset("wingtip.bone", 0, 5);
        setTextureOffset("wingtip.skin", -10, 18);
        (wing = new ModelRenderer(this, "wing")).setTextureSize(30, 30);
        wing.setRotationPoint(-2, 0, 0);
        wing.addBox("bone", -10, -1, -1, 10, 2, 2);
        wing.addBox("skin", -10, 0, 0.5F, 10, 0, 10);
        (wingTip = new ModelRenderer(this, "wingtip")).setTextureSize(30, 30);
        wingTip.setRotationPoint(-10, 0, 0);
        wingTip.addBox("bone", -10, -0.5F, -0.5F, 10, 1, 1);
        wingTip.addBox("skin", -10, 0, 0.5F, 10, 0, 10);
        wing.addChild(wingTip);
    }

    public void renderWings(EntityPlayer player, float partialTicks, double scale, Color color) {
        double angle = interpolate(MathHelper.wrapAngleTo180_float(player.prevRenderYawOffset),
                MathHelper.wrapAngleTo180_float(player.renderYawOffset), partialTicks);

        glPushMatrix();
        glScaled(-scale, -scale, scale);
        glRotated(angle + 180, 0, 1, 0);
        glTranslated(0, CustomModel.enabled ? -.65 : -1.25 / scale, 0);
        glTranslated(0, 0, 0.2 / scale);
        if (player.isSneaking()) {
            glTranslated(0, 0.125 / scale, 0);
        }

        RenderUtil.color(color.getRGB());
        Minecraft.getMinecraft().getTextureManager().bindTexture(location);

        for (int i = 0; i < 2; i++) {
            glEnable(GL_CULL_FACE);
            float f11 = (System.currentTimeMillis() % 1500L) / 1500.0F * MathHelper.PI * 2.0F;
            wing.rotateAngleX = -1.4F - (float) Math.cos(f11) * 0.2F;
            wing.rotateAngleY = 0.35F + (float) Math.sin(f11) * 0.4F;
            wing.rotateAngleZ = 0.35F;
            wingTip.rotateAngleZ = -(float) (Math.sin(f11 + 2.0F) + 0.5) * 0.75F;
            wing.render(0.0625F);
            glScalef(-1, 1, 1);
            if (i == 0) {
                glCullFace(GL_FRONT);
            }
        }

        glCullFace(GL_BACK);
        glDisable(GL_CULL_FACE);
        glColor3f(1, 1, 1);
        glPopMatrix();
    }

    private float interpolate(float current, float target, float percent) {
        float f = (current + (target - current) * percent) % 360;
        return f < 0 ? f + 360 : f;
    }

}
