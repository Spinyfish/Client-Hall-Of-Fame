/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import lodomir.dev.event.impl.render.EventRender2D;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.render.Interface;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.utils.render.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MovementInput;

public class Crosshair
extends Module {
    public ModeSetting color = new ModeSetting("Color", "White", "White", "Sync");
    public BooleanSetting dynamic = new BooleanSetting("Dynamic", true);
    public NumberSetting gap = new NumberSetting("Gap", 0.25, 15.0, 0.25, 0.25);
    public NumberSetting width = new NumberSetting("Width", 0.25, 5.0, 0.25, 0.25);
    public NumberSetting height = new NumberSetting("Height", 0.25, 5.0, 0.25, 0.25);
    public NumberSetting size = new NumberSetting("Size", 0.25, 5.0, 0.25, 0.25);

    public Crosshair() {
        super("Crosshair", 0, Category.RENDER);
        this.addSetting(this.color);
        this.addSetting(this.dynamic);
        this.addSetting(this.gap);
        this.addSetting(this.width);
        this.addSetting(this.height);
        this.addSetting(this.size);
    }

    @Override
    @Subscribe
    public void on2D(EventRender2D event) {
        double gap = this.gap.getValueFloat();
        double width = this.width.getValueFloat();
        double height = this.height.getValueFloat();
        double size = this.size.getValueFloat();
        ScaledResolution scaledRes = new ScaledResolution(mc);
        RenderUtils.drawBorderedRect((double)(scaledRes.getScaledWidth() / 2) - width, (double)(scaledRes.getScaledHeight() / 2) - height - gap - size - (double)(this.isMoving() ? 2 : 0), (double)((float)(scaledRes.getScaledWidth() / 2) + 1.0f) + width, (double)(scaledRes.getScaledHeight() / 2) + height - gap - (double)(this.isMoving() ? 2 : 0), 0.5, this.color.isMode("White") ? new Color(255, 255, 255).getRGB() : new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()).getRGB(), new Color(0, 0, 0).getRGB());
        RenderUtils.drawBorderedRect((double)(scaledRes.getScaledWidth() / 2) - width, (double)(scaledRes.getScaledHeight() / 2) - height + gap + 1.0 + (double)(this.isMoving() ? 2 : 0) - 0.15, (double)((float)(scaledRes.getScaledWidth() / 2) + 1.0f) + width, (double)(scaledRes.getScaledHeight() / 2) + height + 1.0 + gap + size + (double)(this.isMoving() ? 2 : 0) - 0.15, 0.5, this.color.isMode("White") ? new Color(255, 255, 255).getRGB() : new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()).getRGB(), new Color(0, 0, 0).getRGB());
        RenderUtils.drawBorderedRect((double)(scaledRes.getScaledWidth() / 2) - height - gap - size - (double)(this.isMoving() ? 2 : 0) + 0.15, (double)(scaledRes.getScaledHeight() / 2) - width, (double)(scaledRes.getScaledWidth() / 2) + height - gap - (double)(this.isMoving() ? 2 : 0) + 0.15, (double)((float)(scaledRes.getScaledHeight() / 2) + 1.0f) + width, 0.5, this.color.isMode("White") ? new Color(255, 255, 255).getRGB() : new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()).getRGB(), new Color(0, 0, 0).getRGB());
        RenderUtils.drawBorderedRect((double)(scaledRes.getScaledWidth() / 2) - height + 1.0 + gap + (double)(this.isMoving() ? 2 : 0), (double)(scaledRes.getScaledHeight() / 2) - width, (double)(scaledRes.getScaledWidth() / 2) + height + size + gap + 1.0 + (double)(this.isMoving() ? 2 : 0), (double)((float)(scaledRes.getScaledHeight() / 2) + 1.0f) + width, 0.5, this.color.isMode("White") ? new Color(255, 255, 255).getRGB() : new Color(Interface.red.getValueInt(), Interface.green.getValueInt(), Interface.blue.getValueInt()).getRGB(), new Color(0, 0, 0).getRGB());
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean isMoving() {
        if (!this.dynamic.isEnabled()) return false;
        if (Crosshair.mc.thePlayer.isCollidedHorizontally) return false;
        if (Crosshair.mc.thePlayer.isSneaking()) return false;
        MovementInput cfr_ignored_0 = Crosshair.mc.thePlayer.movementInput;
        if (MovementInput.moveForward != 0.0f) return true;
        MovementInput cfr_ignored_1 = Crosshair.mc.thePlayer.movementInput;
        if (MovementInput.moveStrafe == 0.0f) return false;
        return true;
    }
}

