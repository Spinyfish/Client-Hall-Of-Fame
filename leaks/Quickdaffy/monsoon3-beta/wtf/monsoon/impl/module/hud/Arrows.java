/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.hud;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.awt.Color;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.EntityUtil;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.api.util.render.RenderUtil;
import wtf.monsoon.impl.event.EventRender2D;

public class Arrows
extends Module {
    public static Setting<Double> radius = new Setting<Double>("Radius", 20.0).minimum(5.0).maximum(40.0).incrementation(1.0).describedBy("The arrows' distance from the center of the screen");
    public static Setting<Float> opacity = new Setting<Float>("Opacity", Float.valueOf(100.0f)).minimum(Float.valueOf(0.0f)).maximum(Float.valueOf(100.0f)).incrementation(Float.valueOf(1.0f)).describedBy("The alpha of the arrows");
    List<Entity> players;
    @EventLink
    Listener<EventRender2D> r2dListener = this::render2d;

    public Arrows() {
        super("Arrows", "Renders arrows towards players", Category.HUD);
    }

    void render2d(EventRender2D e) {
        this.players = this.mc.theWorld.loadedEntityList.stream().filter(ent -> ent instanceof EntityPlayer && ent != this.mc.thePlayer && !ent.isInvisible()).collect(Collectors.toList());
        float[] screenCenterCoord = new float[]{(float)e.getSr().getScaledWidth() / 2.0f, (float)e.getSr().getScaledHeight() / 2.0f};
        AtomicInteger i = new AtomicInteger();
        this.players.forEach(target -> {
            Vec3 vec = EntityUtil.getInterpolatedPosition(target);
            double yaw = StrictMath.atan2(vec.z - this.mc.thePlayer.posZ, vec.x - this.mc.thePlayer.posX) * 57.29577951308232 - 90.0;
            double finalAngle = 360.0 - (yaw - (double)this.mc.thePlayer.rotationYaw);
            Color accentClr = ColorUtil.getClientAccentTheme()[0];
            Color color = new Color((float)accentClr.getRed() / 255.0f, (float)accentClr.getGreen() / 255.0f, (float)accentClr.getBlue() / 255.0f, opacity.getValue().floatValue() / 100.0f);
            float[] scale = new float[]{2.5f, 2.5f};
            RenderUtil.scale(screenCenterCoord[0], screenCenterCoord[1], scale, () -> RenderUtil.rotate(screenCenterCoord[0], screenCenterCoord[1], finalAngle, () -> Wrapper.getFontUtil().entypo18.drawCenteredString("v", screenCenterCoord[0] - 0.5f, (float)((double)(screenCenterCoord[1] - 3.0f) - radius.getValue()), color, false)));
            i.getAndIncrement();
        });
    }
}

