package cc.swift.notification;


import cc.swift.util.IMinecraft;
import cc.swift.util.render.RenderUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class Notification implements IMinecraft {
    private final Type type;
    private final String title, message;
    private final long start, end;

    @Getter @Setter
    private float width, height, x, y;

    public Notification(Type type, String title, String message, int end) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.width = mc.fontRendererObj.getStringWidth(message) + 70;
        this.height = 40;
        this.start = System.currentTimeMillis();
        this.end = end;
    }

    public boolean isShown() {
        return getTime() < this.end;
    }

    public long getTime() {
        return System.currentTimeMillis() - start;
    }

    public void render(float x, float y) {
        this.x = x;
        this.y = y;
        float animTime = 120f;
        long time = getTime();

        GlStateManager.pushMatrix();
        final float[] factor = {Math.min((float) time / (end / 5f), 1)};
        float barFactor = MathHelper.clamp_float((float) time / (float) (end), 0, 0.98f);

        if (time > end - animTime) {
            factor[0] = (time - (end - animTime)) / animTime;
            GlStateManager.translate(100 * factor[0], 0, 0);
        } else {
            GlStateManager.translate(100 - 100 * factor[0], 0, 0);
        }

        Gui.drawRect(x - width, y - height, x, y, new Color(0, 0, 0, 90).getRGB());
        mc.fontRendererObj.drawStringWithShadow(title, x - width + 40, y - height + 5, Color.white.getRGB());
        mc.fontRendererObj.drawStringWithShadow(message, x - width + 40, y - height + 15, Color.white.getRGB());
        // 😫😫😭😭😭
        RenderUtil.drawImg(new ResourceLocation("swift/notification/" + type.name().toUpperCase() + ".png"), x - width + 3.5f, y - height + 3.5f, 30, 30, Color.WHITE);

        Gui.drawRect(x - width, y - 3, x - width + (width * barFactor) + 2, y, type.color.getRGB());

        GlStateManager.popMatrix();
    }

    @AllArgsConstructor
    @Getter
    public enum Type {
        ENABLE(new Color(0, 255, 0)),
        DISABLE(new Color(255, 0, 0)),
        INFO(new Color(75, 125, 255)),
        WARNING(new Color(255, 255, 0));
        private final Color color;
    }

}
