/**
 * @project Myth
 * @author CodeMan
 * @at 06.02.23, 18:58
 */
package dev.myth.features.visual;

import dev.codeman.eventbus.EventPriority;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.render.Rect;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.api.utils.render.StencilUtil;
import dev.myth.api.utils.render.shader.list.BlurShader;
import dev.myth.api.utils.render.shader.list.DropShadowUtil;
import dev.myth.api.utils.render.shader.list.KawaseBlurShader;
import dev.myth.events.Render2DEvent;
import dev.myth.settings.BooleanSetting;
import dev.myth.settings.EnumSetting;
import dev.myth.settings.NumberSetting;

import java.awt.*;
import java.util.ArrayList;

@Feature.Info(
        name = "Shader",
        description = "Shader",
        category = Feature.Category.VISUAL
)
public class ShaderFeature extends Feature {

    public final BooleanSetting blur = new BooleanSetting("Blur", true);
    public final EnumSetting<BlurType> blurType = new EnumSetting<>("Blur Type", BlurType.GAUSSIAN)
            .addDependency(blur::getValue);
    public final NumberSetting blurRadius = new NumberSetting("Blur Radius", 10, 1, 100, 1)
            .addDependency(() -> blur.getValue() && blurType.getValue() == BlurType.GAUSSIAN);
    public final NumberSetting kawasePasses = new NumberSetting("Kawase Passes", 10, 1, 100, 1)
            .addDependency(() -> blur.getValue() && blurType.getValue() == BlurType.KAWASE);
    public final NumberSetting kawaseOffset = new NumberSetting("Kawase Offset", 10, 1, 100, 1)
            .addDependency(() -> blur.getValue() && blurType.getValue() == BlurType.KAWASE);
    public final BooleanSetting glow = new BooleanSetting("Glow", true);
    public final NumberSetting glowRadius = new NumberSetting("Glow Radius", 10, 1, 100, 1)
            .addDependency(glow::getValue);

    private final ArrayList<Rect> blurAreas = new ArrayList<>();
    private final ArrayList<Rect> glowAreas = new ArrayList<>();

    public static ShaderFeature INSTANCE;

    public ShaderFeature() {
        INSTANCE = this;
    }

    @Handler(EventPriority.HIGHEST)
    public Listener<Render2DEvent> render2DEventListener = event -> {
        if (blur.getValue()) {
            switch (blurType.getValue()) {
                case GAUSSIAN: {

                    if (blurAreas.isEmpty()) break;

                    BlurShader.INSTANCE.startBlur();

                    for (Rect area : blurAreas) {
                        RenderUtil.drawRect(area.getX(), area.getY(), area.getWidth(), area.getHeight(), -1);
                    }

                    int radius = blurRadius.getValue().intValue();

                    BlurShader.INSTANCE.stopBlur(radius / 2, radius, 1);

                    blurAreas.clear();

                    break;
                }
                case KAWASE: {

                    if (blurAreas.isEmpty()) break;

                    StencilUtil.initStencilToWrite();

                    for (Rect area : blurAreas) {
                        RenderUtil.drawRect(area.getX(), area.getY(), area.getWidth(), area.getHeight(), -1);
                    }
                    StencilUtil.readStencilBuffer(1);

                    int passes = kawasePasses.getValue().intValue();
                    int offset = kawaseOffset.getValue().intValue();

                    KawaseBlurShader.INSTANCE.renderBlur(passes, offset);
                    StencilUtil.uninitStencilBuffer();

                    blurAreas.clear();

                    break;
                }
            }
        }
        if(glow.getValue()) {
            if(!glowAreas.isEmpty()) {
                DropShadowUtil.start();

                for (Rect area : glowAreas) {
                    RenderUtil.drawRect(area.getX(), area.getY(), area.getWidth(), area.getHeight(), -1);
                }

                DropShadowUtil.stop(glowRadius.getValue().intValue(), Color.BLACK);

                glowAreas.clear();
            }
        }
    };

    public void blurArea(Rect area) {
        blurAreas.add(area);
    }

    public void glowArea(Rect area) {
        glowAreas.add(area);
    }

    public enum BlurType {
        GAUSSIAN("Gaussian"),
        KAWASE("Kawase");

        final String name;

        BlurType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
