/**
 * @project hakarware
 * @author CodeMan
 * @at 24.07.23, 19:27
 */

package cc.swift.module.impl.render;

import cc.swift.Swift;
import cc.swift.events.BlurEvent;
import cc.swift.events.RenderOverlayEvent;
import cc.swift.module.Module;
import cc.swift.module.impl.render.hud.HudComponent;
import cc.swift.module.impl.render.hud.HudFont;
import cc.swift.module.impl.render.hud.impl.ArrayListComponent;
import cc.swift.module.impl.render.hud.impl.WatermarkComponent;
import cc.swift.module.impl.render.hud.impl.data.DataComponent;
import cc.swift.module.impl.render.hud.impl.data.DataComponentWrapper;
import cc.swift.util.shader.ShaderProgram;
import cc.swift.value.impl.BooleanValue;
import cc.swift.value.impl.DoubleValue;
import cc.swift.value.impl.ModeValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import java.util.ArrayList;

public final class HudModule extends Module {


    // I cba to deal with this
    // public final DoubleValue arrayListPadding = new DoubleValue("ArrayList Padding", 0d, -2, 10, 0.5);
    public final DoubleValue arrayListBGOpacity = new DoubleValue("Background Opacity", 0d, 0, 255, 1);
    public final ModeValue<WatermarkMode> watermarkMode = new ModeValue<>("Watermark mode", WatermarkMode.values());
    public final ModeValue<ArrayListMode> arrayListMode = new ModeValue<>("Arraylist mode", ArrayListMode.values());
    public final ModeValue<SideMode> sideMode = new ModeValue<>("Side", SideMode.values());
    public final DoubleValue padding = new DoubleValue("Padding", 20.0, 0.0, 45.0, 1.0);
    private final ArrayList<HudComponent> hudComponents = new ArrayList<>();
    public final ModeValue<HudFont> font = new ModeValue<>("Font", HudFont.values());
    public final BooleanValue blur = new BooleanValue("Blur", false);

    private final ArrayList<DataComponent> dataComponents = new ArrayList<>();

    public HudModule() {
        super("HUD", Category.RENDER);
        registerValues(watermarkMode, arrayListMode, sideMode, padding, font, arrayListBGOpacity, blur);
        hudComponents.add(new ArrayListComponent(-99, -99)); // fuck this bafoolery
        hudComponents.add(new WatermarkComponent(4, 4));
        hudComponents.add(new DataComponentWrapper(3, 12));
    }

    @Handler
    public final Listener<RenderOverlayEvent> renderOverlayEventListener = event -> {
        for (HudComponent hudComponent : hudComponents) {
            hudComponent.render(font.getValue());
        }

        Swift.INSTANCE.getNotificationManager().render(event.getScaledResolution());
    };

    @Handler
    public final Listener<BlurEvent> blurEvent = event -> {
        if (blur.getValue()) {
            hudComponents.get(0).render(font.getValue());
            hudComponents.get(1).render(font.getValue());
        }
    };
    @Handler
    public final Listener<BlurEvent> shadowEvent = event -> {
        if (blur.getValue()) {
            hudComponents.get(0).render(font.getValue());
            hudComponents.get(1).render(font.getValue());
        }
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }


    public enum WatermarkMode { // german codez made me do this the way i should have from the start
        SWIFT, PEAK, BIGGER_BASIC, BASIC
    }

    public enum ArrayListMode {
        BASIC, WRAPPED
    }
    public enum SideMode {
        LEFT, RIGHT
    }

}
