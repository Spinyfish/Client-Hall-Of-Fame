package dev.echo.module.impl.render;

import com.sun.jna.platform.unix.X11;
import dev.echo.Echo;
import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.utils.font.FontUtil;
import dev.echo.utils.tuples.Pair;
import dev.echo.listener.event.impl.render.Render2DEvent;
import dev.echo.listener.event.impl.render.ShaderEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.impl.combat.KillAura;
import dev.echo.module.impl.player.ChestStealer;
import dev.echo.module.impl.player.InvManager;
import dev.echo.module.settings.impl.*;
import dev.echo.utils.animations.Animation;
import dev.echo.utils.animations.Direction;
import dev.echo.utils.animations.impl.DecelerateAnimation;
import dev.echo.utils.font.AbstractFontRenderer;
import dev.echo.utils.font.CustomFont;
import dev.echo.utils.misc.RomanNumeralUtils;
import dev.echo.utils.player.MovementUtils;
import dev.echo.utils.render.*;
import dev.echo.utils.server.PingerUtils;
import javafx.scene.text.Font;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class HUDMod extends Module {

    private final StringSetting clientName = new StringSetting("Client Name");
    private final ModeSetting watermarkMode = new ModeSetting("Watermark Mode", "Echo", "Echo", "Jello", "Echobition");

    public static final ColorSetting color1 = new ColorSetting("Color 1", new Color(0xffa028d4));
    public static final ColorSetting color2 = new ColorSetting("Color 2", new Color(0xff0008ff));

    public static final ModeSetting theme = Theme.getModeSetting("Theme Selection", "Echo");
    public static final BooleanSetting customFont = new BooleanSetting("Custom Font", true);

    private static final MultipleBoolSetting infoCustomization = new MultipleBoolSetting("Info Options",
            new BooleanSetting("Show Ping", false),
            new BooleanSetting("Semi-Bold Info", true),
            new BooleanSetting("White Info", false),
            new BooleanSetting("Info Shadow", true));

    public static final MultipleBoolSetting hudCustomization = new MultipleBoolSetting("HUD Options",
            new BooleanSetting("Radial Gradients", true),
            new BooleanSetting("Potion HUD", true),
            new BooleanSetting("Armor HUD", true),
            new BooleanSetting("Render Cape", true),
            new BooleanSetting("Lowercase", false));

    private static final MultipleBoolSetting disableButtons = new MultipleBoolSetting("Disable Buttons",
            new BooleanSetting("Disable KillAura", true),
            new BooleanSetting("Disable InvManager", true),
            new BooleanSetting("Disable ChestStealer", true));

    public HUDMod() {
        super("HUD", Category.RENDER, "customizes the client's appearance");
        color1.addParent(theme, modeSetting -> modeSetting.is("Custom Theme"));
        color2.addParent(theme, modeSetting -> modeSetting.is("Custom Theme") && !color1.isRainbow());
        this.addSettings(clientName, watermarkMode, theme, color1, color2, customFont, infoCustomization, hudCustomization, disableButtons);
        if (!enabled) this.toggleSilent();
    }

    public static int offsetValue = 0;
    private final Animation fadeInText = new DecelerateAnimation(500, 1);
    private int ticks = 0;

    private boolean version = true;

    public static float xOffset = 0;


    @Link
    public Listener<ShaderEvent> onShaderEvent = event -> {
        String name = Echo.NAME;

        switch (watermarkMode.getMode()) {
            case "Echo": {
                float echoX = 15.0F;
                float echoY = 15.0F;

                float echoWidth = 45.0F;
                float echoHeight = 45.0F;

                if (event.isBloom()) {
                    RoundedUtil.drawRound(
                            echoX,
                            echoY,
                            echoWidth,
                            echoHeight,
                            7.5F,
                            new Color(255, 255, 255, 150)
                    );

                    iconFont40.drawString(
                            FontUtil.SAVE,
                            echoX + echoWidth / 2.0F - iconFont40.getStringWidth(FontUtil.SAVE) / 2.0F,
                            echoY + echoHeight / 2.0F - iconFont40.getHeight() / 2.25F,
                            Color.BLACK
                    );
                }

                if (!event.isBloom())
                    RoundedUtil.drawRound(
                            echoX,
                            echoY,
                            echoWidth,
                            echoHeight,
                            7.5F,
                            Color.BLACK
                    );

                break;
            }
            case "Jello": {
                float jelloX = 15.0F;
                float jelloY = 15.0F;

                if (!event.isBloom())
                    echoBoldFont80.drawString(
                            name,
                            jelloX,
                            jelloY,
                            Color.BLACK
                    );

                if (event.isBloom())
                    echoBoldFont80.drawString(
                            name,
                            jelloX,
                            jelloY,
                            new Color(25, 25, 25, 225)
                    );

                break;
            }
        }
    };

    @Link
    public Listener<Render2DEvent> onRender2D = e -> {
        String name = Echo.NAME;

        switch (watermarkMode.getMode()) {
            case "Echo": {
                float echoX = 15.0F;
                float echoY = 15.0F;

                float echoWidth = 45.0F;
                float echoHeight = 45.0F;

                RoundedUtil.drawRound(
                        echoX,
                        echoY,
                        echoWidth,
                        echoHeight,
                        7.5F,
                        new Color(255, 255, 255, 50)
                );

                iconFont40.drawString(
                        FontUtil.SAVE,
                        echoX + echoWidth / 2.0F - iconFont40.getStringWidth(FontUtil.SAVE) / 2.0F,
                        echoY + echoHeight / 2.0F - iconFont40.getHeight() / 2.25F,
                        Color.WHITE
                );

                break;
            }
            case "Jello": {
                float jelloX = 15.0F;
                float jelloY = 15.0F;

                echoBoldFont80.drawString(
                        name,
                        jelloX,
                        jelloY,
                        new Color(255, 255, 255, 50)
                );

                break;
            }
            case "Echobition": {
                StringBuilder stringBuilder = new StringBuilder(name.replace("echo", "Echobition")).insert(1, "§7");
                stringBuilder.append(" [§fFPS: ").append(Minecraft.getDebugFPS()).append("§7]");

                RenderUtil.resetColor();
                mc.fontRendererObj.drawStringWithShadow(
                        stringBuilder.toString(),
                        4, 4,
                        getClientColors().getFirst().getRGB()
                );
                break;
            }
        }

    };

    public static Pair<Color, Color> getClientColors() {
        return Theme.getThemeColors(theme.getMode());
    }

    public static String get(String text) {
        return hudCustomization.getSetting("Lowercase").isEnabled() ? text.toLowerCase() : text;
    }

    public static boolean isRainbowTheme() {
        return theme.is("Custom Theme") && color1.isRainbow();
    }

    public static boolean drawRadialGradients() {
        return hudCustomization.getSetting("Radial Gradients").isEnabled();
    }

    public static void addButtons(List<GuiButton> buttonList) {
        for (ModuleButton mb : ModuleButton.values()) {
            if (mb.getSetting().isEnabled()) {
                buttonList.add(mb.getButton());
            }
        }
    }

    public static void updateButtonStatus() {
        for (ModuleButton mb : ModuleButton.values()) {
            mb.getButton().enabled = Echo.INSTANCE.getModuleCollection().getModule(mb.getModule()).isEnabled();
        }
    }

    public static void handleActionPerformed(GuiButton button) {
        for (ModuleButton mb : ModuleButton.values()) {
            if (mb.getButton() == button) {
                Module m = Echo.INSTANCE.getModuleCollection().getModule(mb.getModule());
                if (m.isEnabled()) {
                    m.toggle();
                }
                break;
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public enum ModuleButton {
        AURA(KillAura.class, disableButtons.getSetting("Disable KillAura"), new GuiButton(2461, 3, 4, 120, 20, "Disable KillAura")),
        INVMANAGER(InvManager.class, disableButtons.getSetting("Disable InvManager"), new GuiButton(2462, 3, 26, 120, 20, "Disable InvManager")),
        CHESTSTEALER(ChestStealer.class, disableButtons.getSetting("Disable ChestStealer"), new GuiButton(2463, 3, 48, 120, 20, "Disable ChestStealer"));

        private final Class<? extends Module> module;
        private final BooleanSetting setting;
        private final GuiButton button;
    }

}

