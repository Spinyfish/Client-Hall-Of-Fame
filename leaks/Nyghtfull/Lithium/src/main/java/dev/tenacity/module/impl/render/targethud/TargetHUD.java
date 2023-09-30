package dev.tenacity.module.impl.render.targethud;

import dev.tenacity.utils.Utils;
import dev.tenacity.utils.objects.GradientColorWheel;
import dev.tenacity.utils.render.GLUtil;
import lombok.Getter;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;

import java.util.HashMap;

public abstract class TargetHUD implements Utils {

    protected GradientColorWheel colorWheel;
    private float width, height;
    @Getter
    private final String name;

    public TargetHUD(String name) {
        this.name = name;
    }

    public void setColorWheel(GradientColorWheel colorWheel) {
        this.colorWheel = colorWheel;
    }

    protected void renderPlayer2D(float x, float y, float width, float height, AbstractClientPlayer player) {
        GLUtil.startBlend();
        mc.getTextureManager().bindTexture(player.getLocationSkin());
        Gui.drawScaledCustomSizeModalRect(x, y, (float) 8.0, (float) 8.0, 8, 8, width, height, 64.0F, 64.0F);
        GLUtil.endBlend();
    }

    public abstract void render(float x, float y, float alpha, EntityLivingBase target);

    public abstract void renderEffects(float x, float y, float alpha, boolean glow);

    private static final HashMap<Class<? extends TargetHUD>, TargetHUD> targetHuds = new HashMap<>();

    public static TargetHUD get(String name) {
        return targetHuds.values().stream().filter(hud -> hud.getName().equals(name)).findFirst().orElse(null);
    }

    public static <T extends TargetHUD> T get(Class<T> clazz) {
        return (T) targetHuds.get(clazz);
    }

    public static void init() {
        targetHuds.put(TenacityTargetHUD.class, new TenacityTargetHUD());
        targetHuds.put(LithiumTargetHUD.class, new LithiumTargetHUD());
        targetHuds.put(JelloTargetHUD.class, new JelloTargetHUD());
        targetHuds.put(RiseTargetHUD.class, new RiseTargetHUD());
        targetHuds.put(ExhiTargetHUD.class, new ExhiTargetHUD());
        targetHuds.put(AutoDoxTargetHUD.class, new AutoDoxTargetHUD());
        targetHuds.put(AkrienTargetHUD.class, new AkrienTargetHUD());
        targetHuds.put(AstolfoTargetHUD.class, new AstolfoTargetHUD());
        targetHuds.put(NovolineTargetHUD.class, new NovolineTargetHUD());
        targetHuds.put(ZeroDayTargetHUD.class, new ZeroDayTargetHUD());
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
