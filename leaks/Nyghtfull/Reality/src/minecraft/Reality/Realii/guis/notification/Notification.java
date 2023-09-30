package Reality.Realii.guis.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import Reality.Realii.guis.font.FontLoaders;
import Reality.Realii.mods.modules.ClientSettings;
import Reality.Realii.utils.cheats.world.TimerUtil;
import Reality.Realii.utils.math.AnimationUtils;
import Reality.Realii.utils.render.RenderUtil;

import java.awt.*;
import java.util.Locale;

public class Notification {
    public float x;
    public float width, height;
    public String name;
    public float lastTime;
    public TimerUtil timer;
    public Type type;
    public boolean setBack;
    private float fy, cy = 0;
    private TimerUtil anitimer = new TimerUtil();
    private AnimationUtils animationUtils = new AnimationUtils();
    private AnimationUtils animationUtils2 = new AnimationUtils();


    public Notification(String name, Type type) {
        this.name = name;
        this.type = type;
        this.lastTime = 1.5f;
        this.width = FontLoaders.arial16.getStringWidth(name) + 25;
        this.height = 20;
    }

    public Notification(String name, Type type, float lastTime) {
        this.name = name;
        this.type = type;
        this.lastTime = lastTime;
        this.width = FontLoaders.arial16.getStringWidth(name) + 45;
        this.height = 24;

    }

    public void render(float y) {
        fy = y;
        if (cy == 0) {
            cy = fy + 25;
        }
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        RenderUtil.drawRect((float) (sr.getScaledWidth_double() - x), cy, (float) ((sr.getScaledWidth_double() - x) + width), cy + height, new Color(10, 10, 10, 70));
        RenderUtil.drawRect((float) (sr.getScaledWidth_double() - x), cy + height - 1, (float) (sr.getScaledWidth_double() - x) + width, cy + height, new Color(50, 50, 60, 70));
        if (timer != null) {
        	Color c = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue());
            RenderUtil.drawRect((float) (sr.getScaledWidth_double() - x), cy + height - 1, (float) ((sr.getScaledWidth_double() - x) + (this.timer.getTime() - timer.lastMS) / (lastTime * 800) * width), cy + height, c.getRGB());
        }
        RenderUtil.drawCustomImageAlpha(sr.getScaledWidth() - x + -2, cy + -1, 29, 29, new ResourceLocation("client/Icon12.png"), -1, 255);

        FontLoaders.arial16.drawString(name, (sr.getScaledWidth() - x) + 30, cy + 7, -1);
      
    }

    public void update() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        if (timer == null && Math.abs(x - width) < 0.1f) {
            timer = new TimerUtil();
            timer.reset();
        }
        if (anitimer.delay(10)) {
            cy = animationUtils.animate(fy, cy, 0.1f);

            if (!setBack) {
                x = animationUtils2.animate(width, x, 0.1f);
            } else {
                x = animationUtils2.animate(0, x, 0.1f);
            }
            anitimer.reset();
        }
    }

    public enum Type {
        Info,
        Alert,
        Error
    }

}
