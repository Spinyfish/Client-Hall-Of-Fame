package dev.echo.module.impl.render;

import dev.echo.Echo;
import dev.echo.listener.event.impl.render.ShaderEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.ParentAttribute;
import dev.echo.module.settings.impl.BooleanSetting;
import dev.echo.module.settings.impl.MultipleBoolSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.ui.clickguis.modern.ModernClickGui;
import dev.echo.utils.render.RenderUtil;
import dev.echo.utils.render.blur.KawaseBloom;
import dev.echo.utils.render.blur.KawaseBlur;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;

import java.awt.*;

public class PostProcessing extends Module {

    public final BooleanSetting blur = new BooleanSetting("Blur", true);
    private final NumberSetting iterations = new NumberSetting("Blur Iterations", 2, 8, 1, 1);
    private final NumberSetting offset = new NumberSetting("Blur Offset", 3, 10, 1, 1);
    private final BooleanSetting bloom = new BooleanSetting("Bloom", true);
    public static MultipleBoolSetting glowOptions = new MultipleBoolSetting("Glow Options",
            new BooleanSetting("Arraylist", true),
            new BooleanSetting("ClickGui", false),
            new BooleanSetting("Watermark", true),
            new BooleanSetting("Statistics", true),
            new BooleanSetting("Radar", true),
            new BooleanSetting("TargetHud", true),
            new BooleanSetting("Spotify", true),
            new BooleanSetting("Notifications", false),
            new BooleanSetting("Keystrokes", false));
    private final NumberSetting shadowRadius = new NumberSetting("Bloom Iterations", 3, 8, 1, 1);
    private final NumberSetting shadowOffset = new NumberSetting("Bloom Offset", 1, 10, 1, 1);


    public PostProcessing() {
        super("PostProcessing", Category.RENDER, "blurs shit");
        shadowRadius.addParent(bloom, ParentAttribute.BOOLEAN_CONDITION);
        shadowOffset.addParent(bloom, ParentAttribute.BOOLEAN_CONDITION);
        glowOptions.addParent(bloom, ParentAttribute.BOOLEAN_CONDITION);
        addSettings(blur, iterations, offset, bloom, glowOptions, shadowRadius, shadowOffset);
    }

    private String currentMode;

    public void stuffToBlur(boolean bloom) {

        ScaledResolution sr = new ScaledResolution(mc);


        if (mc.currentScreen instanceof GuiChat) {
            Gui.drawRect2(2, sr.getScaledHeight() - (14 * GuiChat.openingAnimation.getOutput().floatValue()), sr.getScaledWidth() - 4, 12, Color.BLACK.getRGB());
        }

        if (mc.currentScreen == ClickGUIMod.dropdownClickGui) {
            ClickGUIMod.dropdownClickGui.renderEffects();
        }
        if (mc.currentScreen == ClickGUIMod.dropdownClickGui || mc.currentScreen == ClickGUIMod.modernClickGui || mc.currentScreen == ClickGUIMod.compactClickgui) {
            Echo.INSTANCE.getSideGui().drawForEffects(bloom);
            Echo.INSTANCE.getSearchBar().drawEffects();
        }


        RenderUtil.resetColor();
        mc.ingameGUI.getChatGUI().renderChatBox();
        RenderUtil.resetColor();
        mc.ingameGUI.renderScoreboardBlur(sr);
        RenderUtil.resetColor();
        NotificationsMod notificationsMod = Echo.INSTANCE.getModuleCollection().getModule(NotificationsMod.class);
        if (notificationsMod.isEnabled()) {
            notificationsMod.renderEffects(glowOptions.getSetting("Notifications").isEnabled());
        }

        if (bloom) {
            if (mc.currentScreen instanceof ModernClickGui) {
                ClickGUIMod.modernClickGui.drawBigRect();
            }
        }

    }

    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

    public void blurScreen() {
        if (!enabled) return;
        if (blur.isEnabled()) {
            stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer);

            stencilFramebuffer.framebufferClear();
            stencilFramebuffer.bindFramebuffer(false);
            Echo.INSTANCE.getEventProtocol().handleEvent(new ShaderEvent(false, glowOptions));
            stuffToBlur(false);
            stencilFramebuffer.unbindFramebuffer();


            KawaseBlur.renderBlur(stencilFramebuffer.framebufferTexture, iterations.getValue().intValue(), offset.getValue().intValue());

        }


        if (bloom.isEnabled()) {
            stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer);
            stencilFramebuffer.framebufferClear();
            stencilFramebuffer.bindFramebuffer(false);

            Echo.INSTANCE.getEventProtocol().handleEvent(new ShaderEvent(true, glowOptions));
            stuffToBlur(true);

            stencilFramebuffer.unbindFramebuffer();

            KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, shadowRadius.getValue().intValue(), shadowOffset.getValue().intValue());

        }
    }


}
