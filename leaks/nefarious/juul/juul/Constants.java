package juul;

import java.util.ArrayList;
import java.util.List;

import juul.Juul;
import juul.font.FontUtil;
import juul.inject.wrappers.EntityPlayerSPWrapper;
import juul.inject.wrappers.EntityPlayerWrapper;
import juul.inject.wrappers.MinecraftWrapper;
import juul.inject.wrappers.WorldWrapper;

public class Constants {
    private final MinecraftWrapper mc = Juul.INSTANCE.getWrapperManager().get(MinecraftWrapper.class);
    private Object timer;

    public Object getMinecraft() {
        return mc.getClassInstance();
    }

    public MinecraftWrapper getMC(){ return mc; }

    public Object getTimer() {
        if (timer == null)
            timer = mc.timer();

        return timer;
    }

    private final EntityPlayerWrapper playerWrapper = new EntityPlayerWrapper(mc.player());
    private final EntityPlayerSPWrapper playerSPWrapper = new EntityPlayerSPWrapper(mc.player());

    public EntityPlayerWrapper playerWrapper() {
        return playerWrapper;
    }

    public EntityPlayerSPWrapper playerSPWrapper() {
        return playerSPWrapper;
    }

    public Object world() {
        return mc.world();
    }

    public List<?> players() {
        if (mc.world() == null)
            return new ArrayList<>();

        return new WorldWrapper(mc.world()).playerEntities();
    }

    public void drawString(String text, double d, float y, int background, int color) {
        FontUtil.semibold_16.drawString(text, d + 0.5f, y + 0.5f, background);
        FontUtil.semibold_16.drawString(text, d, y, color);
    }

    public double getStringWidth(String text) {
        return FontUtil.semibold_16.getStringWidth(text);
    }

    public int getFontHeight(String text) {
        return FontUtil.semibold_16.getHeight();
    }

    public void drawString(String text, float x, float y, int color) {
        FontUtil.semibold_16.drawString(text, x, y, color);
    }

    public void drawStringWithShadow(String text, int x, int y, int color) {
        FontUtil.semibold_16.drawStringWithShadow(text, x, y, color);
    }

}
