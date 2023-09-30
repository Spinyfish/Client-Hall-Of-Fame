package v4n1ty.module.render;

import de.Hero.settings.Setting;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.renderer.GlStateManager;
import v4n1ty.V4n1ty;
import v4n1ty.module.Category;
import v4n1ty.module.Module;
import v4n1ty.utils.render.ColorUtils;
import v4n1ty.utils.render.RenderUtils;

import java.util.ArrayList;

public class ChinaHat extends Module {

    public ChinaHat() {
        super("China Hat", Keyboard.KEY_V, Category.RENDER);
    }

    @Override
    public void setup() {
        V4n1ty.settingsManager.rSetting(new Setting("Radius", this, 0.75, 0, 10, false));
    }

    public void ChinaHat() {
        float radius = (float) V4n1ty.settingsManager.getSettingByName("Radius").getValDouble();
        float x = (float) mc.thePlayer.posX, z = (float) mc.thePlayer.posZ, y = (float) mc.thePlayer.posY;
        for (int i = 0; i < 360; i++) {
            float fadeOffset = 0;
            float dX = radius * (float) Math.cos(Math.toRadians(i));
            float dZ = radius * (float) Math.sin(Math.toRadians(i));
            GlStateManager.pushMatrix();
            RenderUtils.draw3dLine(0, 2.25, 0, dX, 1.9, dZ, ColorUtils.getColorAlpha(V4n1ty.getHudColor(fadeOffset).getRGB(), 255));
            fadeOffset -= 3;
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void onRender() {
        if(this.isToggled()) {
            ChinaHat();
        }
    }
}