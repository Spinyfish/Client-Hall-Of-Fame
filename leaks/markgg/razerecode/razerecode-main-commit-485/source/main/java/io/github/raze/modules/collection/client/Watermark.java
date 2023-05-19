package io.github.raze.modules.collection.client;

import io.github.raze.Raze;
import io.github.raze.modules.system.information.ModuleCategory;
import io.github.raze.modules.system.ingame.ModuleIngame;
import io.github.raze.settings.collection.ArraySetting;
import io.github.raze.settings.collection.BooleanSetting;
import io.github.raze.themes.system.BaseTheme;
import io.github.raze.utilities.collection.fonts.CFontUtil;
import io.github.raze.utilities.collection.math.MathUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class Watermark extends ModuleIngame {

    public ArraySetting mode;
    public BooleanSetting bps;

    public Watermark() {
        super("Watermark", "Displays a watermark.", ModuleCategory.CLIENT);

        this.x = 5;
        this.y = 5;

        Raze.INSTANCE.MANAGER_REGISTRY.SETTING_REGISTRY.register(

                mode = new ArraySetting(this, "Font", "Jello", "Jello", "Minecraft"),
                bps = new BooleanSetting(this,"Blocks per second", true)
        );

        setEnabled(true);
    }

    @Override
    public void renderIngame() {

        BaseTheme theme = Raze.INSTANCE.MANAGER_REGISTRY.THEME_REGISTRY.getCurrentTheme();

        ScaledResolution sr = new ScaledResolution(mc);

        float squareMotion = (float)(MathUtil.square(mc.thePlayer.motionX) + MathUtil.square(mc.thePlayer.motionZ));
        float bpsValue = (float)MathUtil.round(Math.sqrt(squareMotion) * 20.0D * mc.timer.timerSpeed, (int) 2.0D);

        switch(mode.get().toString()) {
            case "Jello":
                CFontUtil.Jello_Regular_40.getRenderer().drawString(
                        "Raze",
                        getX(),
                        getY(),
                        new Color(255, 255, 255, 185)
                );

                CFontUtil.Jello_Medium_24.getRenderer().drawString(
                        theme.getName(),
                        getX(),
                        getY() + CFontUtil.Jello_Light_40.getRenderer().getHeight() - 2,
                        new Color(255, 255, 255, 185)
                );

                if (bps.get()) {
                    CFontUtil.Jello_Light_20.getRenderer().drawString (
                            "BPS: " + bpsValue,
                            getX(),
                            sr.getScaledHeight() - (CFontUtil.Jello_Light_20.getRenderer().getHeight() + 7) - 5,
                            new Color(255, 255, 255, 185)
                    );
                }
                break;
            case "Minecraft":
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.4, 2.4, 2.4);
                mc.fontRenderer.drawString("Raze", 3, 3, -1);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(1.4, 1.4, 1.4);
                mc.fontRenderer.drawString(theme.getName(), 3, 19, -1);
                GlStateManager.popMatrix();

                GlStateManager.pushMatrix();
                GlStateManager.scale(1, 1, 1);
                if (bps.get())
                    mc.fontRenderer.drawString("BPS: " + bpsValue, getX(), sr.getScaledHeight() - (mc.fontRenderer.FONT_HEIGHT + 7) - 5, -1);
                GlStateManager.popMatrix();
                break;
        }

    }

    @Override
    public void renderDummy() {
        renderIngame();
    }
}
