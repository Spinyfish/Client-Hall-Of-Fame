/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.surge.animation.Animation
 *  me.surge.animation.Easing
 */
package wtf.monsoon.impl.module.hud;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.awt.Color;
import me.surge.animation.Animation;
import me.surge.animation.Easing;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.render.ColorUtil;
import wtf.monsoon.impl.event.EventRender2D;

public class CrosshairCustomizer
extends Module {
    public final Setting<Float> sizeSetting = new Setting<Float>("Size", Float.valueOf(2.5f)).minimum(Float.valueOf(0.25f)).maximum(Float.valueOf(15.0f)).incrementation(Float.valueOf(0.25f)).describedBy("Size");
    public final Setting<Float> gapSetting = new Setting<Float>("Gap", Float.valueOf(2.5f)).minimum(Float.valueOf(0.25f)).maximum(Float.valueOf(15.0f)).incrementation(Float.valueOf(0.25f)).describedBy("Gap");
    public final Setting<Float> widthSetting = new Setting<Float>("Width", Float.valueOf(1.0f)).minimum(Float.valueOf(0.25f)).maximum(Float.valueOf(10.0f)).incrementation(Float.valueOf(0.25f)).describedBy("Width");
    public Setting<Boolean> outline = new Setting<Boolean>("Outline", true).describedBy("Outline");
    public Setting<Boolean> dynamic = new Setting<Boolean>("Dynamic", false).describedBy("dynamic");
    public Setting<Float> dynamicGap = new Setting<Float>("Gap", Float.valueOf(2.5f)).minimum(Float.valueOf(0.0f)).maximum(Float.valueOf(15.0f)).incrementation(Float.valueOf(0.25f)).describedBy("dynamic gap").childOf(this.dynamic);
    public Setting<String> elements = new Setting<String>("Elements", "Elements").describedBy("Elements to render.");
    public Setting<Boolean> renderTop = new Setting<Boolean>("Top", true).describedBy("Render the top crosshair element.").childOf(this.elements);
    public Setting<Boolean> renderBottom = new Setting<Boolean>("Bottom", true).describedBy("Render the bottom crosshair element.").childOf(this.elements);
    public Setting<Boolean> renderLeft = new Setting<Boolean>("Left", true).describedBy("Render the left crosshair element.").childOf(this.elements);
    public Setting<Boolean> renderRight = new Setting<Boolean>("Right", true).describedBy("Render the right crosshair element.").childOf(this.elements);
    public Setting<Color> color = new Setting<Color>("Color", new Color(-1)).describedBy("Color");
    public Setting<Boolean> pointed = new Setting<Boolean>("Pointed", true).describedBy("Change the colour when pointed at an entity");
    public Setting<Float> pointedGap = new Setting<Float>("Gap", Float.valueOf(2.5f)).minimum(Float.valueOf(0.0f)).maximum(Float.valueOf(15.0f)).incrementation(Float.valueOf(0.25f)).describedBy("How much to expand the gap by when pointed at an entity").childOf(this.pointed);
    public Setting<Color> pointedColour = new Setting<Color>("Color", new Color(-1)).describedBy("The colour of the crosshair when pointed at an entity").childOf(this.pointed);
    private final Animation dynamicAnimation = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.LINEAR);
    private final Animation pointAnimation = new Animation(() -> Float.valueOf(200.0f), false, () -> Easing.CUBIC_IN_OUT);
    @EventLink
    private final Listener<EventRender2D> eventRender2DListener = e -> {
        ScaledResolution sr = e.getSr();
        this.renderVerticalRects(sr);
        this.renderHorizontalRects(sr);
    };

    public CrosshairCustomizer() {
        super("Crosshair", "Customize the crosshair", Category.HUD);
    }

    private void renderHorizontalRects(ScaledResolution sr) {
        float height = this.widthSetting.getValue().floatValue() / 2.0f;
        float gap = this.gapSetting.getValue().floatValue();
        float outlineSize = 0.5f;
        Color color1 = this.color.getValue();
        if (this.dynamic.getValue().booleanValue()) {
            this.dynamicAnimation.setState(this.mc.thePlayer.isSprinting());
            gap = (float)((double)gap + (double)this.dynamicGap.getValue().floatValue() * this.dynamicAnimation.getAnimationFactor());
        }
        if (this.pointed.getValue().booleanValue()) {
            this.pointAnimation.setState(this.mc.pointedEntity != null && !this.mc.pointedEntity.isDead);
            gap = (float)((double)gap + (double)this.pointedGap.getValue().floatValue() * this.pointAnimation.getAnimationFactor());
            color1 = new Color(ColorUtil.fadeBetween(color1.getRGB(), this.pointedColour.getValue().getRGB(), (float)this.pointAnimation.getAnimationFactor()));
        }
        if (this.outline.getValue().booleanValue()) {
            if (this.renderLeft.getValue().booleanValue()) {
                Gui.drawRect((float)(sr.getScaledWidth() / 2) - gap - this.sizeSetting.getValue().floatValue() - outlineSize, (float)(sr.getScaledHeight() / 2) - height - outlineSize, (float)(sr.getScaledWidth() / 2) - gap + outlineSize, (float)(sr.getScaledHeight() / 2) + height + outlineSize, Color.BLACK.getRGB());
            }
            if (this.renderRight.getValue().booleanValue()) {
                Gui.drawRect((float)(sr.getScaledWidth() / 2) + gap - outlineSize, (float)(sr.getScaledHeight() / 2) - height - outlineSize, (float)(sr.getScaledWidth() / 2) + gap + this.sizeSetting.getValue().floatValue() + outlineSize, (float)(sr.getScaledHeight() / 2) + height + outlineSize, Color.BLACK.getRGB());
            }
        }
        if (this.renderLeft.getValue().booleanValue()) {
            Gui.drawRect((float)(sr.getScaledWidth() / 2) - gap - this.sizeSetting.getValue().floatValue(), (float)(sr.getScaledHeight() / 2) - height, (float)(sr.getScaledWidth() / 2) - gap, (float)(sr.getScaledHeight() / 2) + height, color1.getRGB());
        }
        if (this.renderRight.getValue().booleanValue()) {
            Gui.drawRect((float)(sr.getScaledWidth() / 2) + gap, (float)(sr.getScaledHeight() / 2) - height, (float)(sr.getScaledWidth() / 2) + gap + this.sizeSetting.getValue().floatValue(), (float)(sr.getScaledHeight() / 2) + height, color1.getRGB());
        }
    }

    private void renderVerticalRects(ScaledResolution sr) {
        float width = this.widthSetting.getValue().floatValue() / 2.0f;
        float gap = this.gapSetting.getValue().floatValue();
        float outlineSize = 0.5f;
        Color color1 = this.color.getValue();
        if (this.dynamic.getValue().booleanValue()) {
            this.dynamicAnimation.setState(this.mc.thePlayer.isSprinting());
            gap = (float)((double)gap + (double)this.dynamicGap.getValue().floatValue() * this.dynamicAnimation.getAnimationFactor());
        }
        if (this.pointed.getValue().booleanValue()) {
            this.pointAnimation.setState(this.mc.pointedEntity != null && !this.mc.pointedEntity.isDead);
            gap = (float)((double)gap + (double)this.pointedGap.getValue().floatValue() * this.pointAnimation.getAnimationFactor());
            color1 = new Color(ColorUtil.fadeBetween(color1.getRGB(), this.pointedColour.getValue().getRGB(), (float)this.pointAnimation.getAnimationFactor()));
        }
        if (this.outline.getValue().booleanValue()) {
            if (this.renderTop.getValue().booleanValue()) {
                Gui.drawRect((float)(sr.getScaledWidth() / 2) - width - outlineSize, (float)(sr.getScaledHeight() / 2) - gap - this.sizeSetting.getValue().floatValue() - outlineSize, (float)(sr.getScaledWidth() / 2) + width + outlineSize, (float)(sr.getScaledHeight() / 2) - gap + outlineSize, Color.BLACK.getRGB());
            }
            if (this.renderBottom.getValue().booleanValue()) {
                Gui.drawRect((float)(sr.getScaledWidth() / 2) - width - outlineSize, (float)(sr.getScaledHeight() / 2) + gap + this.sizeSetting.getValue().floatValue() + outlineSize, (float)(sr.getScaledWidth() / 2) + width + outlineSize, (float)(sr.getScaledHeight() / 2) + gap - outlineSize, Color.BLACK.getRGB());
            }
        }
        if (this.renderTop.getValue().booleanValue()) {
            Gui.drawRect((float)(sr.getScaledWidth() / 2) - width, (float)(sr.getScaledHeight() / 2) - gap - this.sizeSetting.getValue().floatValue(), (float)(sr.getScaledWidth() / 2) + width, (float)(sr.getScaledHeight() / 2) - gap, color1.getRGB());
        }
        if (this.renderBottom.getValue().booleanValue()) {
            Gui.drawRect((float)(sr.getScaledWidth() / 2) - width, (float)(sr.getScaledHeight() / 2) + gap + this.sizeSetting.getValue().floatValue(), (float)(sr.getScaledWidth() / 2) + width, (float)(sr.getScaledHeight() / 2) + gap, color1.getRGB());
        }
    }
}

