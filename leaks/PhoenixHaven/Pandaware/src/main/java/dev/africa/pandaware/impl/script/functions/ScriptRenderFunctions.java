package dev.africa.pandaware.impl.script.functions;


import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.utils.render.RenderUtils;


public class ScriptRenderFunctions {
    public void drawRect(double x, double y,
                         double width, double height,
                         int color) {
        RenderUtils.drawRect(x, y, x + width, y + height, color);
    }

    public void drawString(String text, double x, double y, int color,
                           boolean customFont, boolean shadow) {
        if (customFont) {
            if (shadow) {
                Fonts.getInstance().getProductSansMedium().drawStringWithShadow(text, x, y, color);
            } else {
                Fonts.getInstance().getProductSansMedium().drawString(text, x, y, color);
            }
        } else {
            if (shadow) {
                MinecraftInstance.mc.fontRendererObj.drawStringWithShadow(text, x, y, color);
            } else {
                MinecraftInstance.mc.fontRendererObj.drawString(text, (int) x, (int) y, color);
            }
        }
    }

    public double getStringWidth(String text, boolean customFont) {
        if (customFont) {
            return Fonts.getInstance().getProductSansMedium().getStringWidth(text);
        } else {
            return MinecraftInstance.mc.fontRendererObj.getStringWidth(text);
        }
    }
}
