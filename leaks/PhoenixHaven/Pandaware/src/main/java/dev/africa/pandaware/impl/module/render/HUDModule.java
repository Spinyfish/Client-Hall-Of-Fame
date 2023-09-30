package dev.africa.pandaware.impl.module.render;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.game.TickEvent;
import dev.africa.pandaware.impl.event.player.PacketEvent;
import dev.africa.pandaware.impl.event.render.RenderEvent;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.impl.packet.PacketBalance;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.ColorSetting;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.impl.ui.UISettings;
import dev.africa.pandaware.utils.client.HWIDUtils;
import dev.africa.pandaware.utils.client.Printer;
import dev.africa.pandaware.utils.math.MathUtils;
import dev.africa.pandaware.utils.math.TimeHelper;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import dev.africa.pandaware.utils.player.MovementUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;
import dev.africa.pandaware.utils.render.ColorUtils;
import dev.africa.pandaware.utils.render.RenderUtils;
import dev.africa.pandaware.utils.render.animator.Easing;
import javazoom.jl.player.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Getter
@ModuleInfo(name = "HUD", category = Category.VISUAL)
public class HUDModule extends Module {
    private final EnumSetting<ColorMode> colorMode = new EnumSetting<>("Color mode", ColorMode.PANDAWARE);
    private final EnumSetting<HUDMode> hudMode = new EnumSetting<>("HUD Style", HUDMode.ROUNDED);

    private final BooleanSetting arraylist = new BooleanSetting("Arraylist", true);
    private final BooleanSetting watermark = new BooleanSetting("Watermark", true);
    public final BooleanSetting transparentChat = new BooleanSetting("Transparent Chat", false);
    private final BooleanSetting label = new BooleanSetting("Show Labels", true, this.arraylist::getValue);
    private final BooleanSetting informations = new BooleanSetting("Information", true);
    private final BooleanSetting customFont = new BooleanSetting("Custom font", true);
    private final BooleanSetting irc = new BooleanSetting("IRC", true);
    private final BooleanSetting arraylistLine = new BooleanSetting("Arraylist line", true,
            this.arraylist::getValue);
    private final BooleanSetting arraylistHideVisual = new BooleanSetting("Arraylist Hide Visuals", true,
            this.arraylist::getValue);
    private final BooleanSetting borderlessFullscreen = new BooleanSetting("Borderless Fulscreen", true);
    private final BooleanSetting showCape = new BooleanSetting("Show Cape", true);
    private final BooleanSetting toggleSound = new BooleanSetting("Toggle sound", false);
    private final BooleanSetting toggleNotifications = new BooleanSetting("Toggle notifications", false);
    private final BooleanSetting notificationsCenter = new BooleanSetting("Notifications in Center", false);
    private final BooleanSetting scoreboardDynamic = new BooleanSetting("Dynamic Scoreboard", false);
    private final BooleanSetting hidePotionParticles = new BooleanSetting("Hide potion particles", false);
    private final BooleanSetting showBalance = new BooleanSetting("Show Balance", false);
    private final NumberSetting soundVolume = new NumberSetting("Toggle Sound volume", 100, 1, 20, 1,
            this.toggleSound::getValue);
    private final EnumSetting<CapeMode> capeMode = new EnumSetting<>("Cape mode", CapeMode.CAR, this.showCape::getValue);

    private final NumberSetting colorTime = new NumberSetting("Color time",
            10000, 100, 3000, 1, () -> this.colorMode.getValue() == ColorMode.PANDAWARE
            || this.colorMode.getValue() == ColorMode.SHADE);
    private final NumberSetting colorIndexTime = new NumberSetting("Color index time",
            300, 50, 55, 1, () -> this.colorMode.getValue() == ColorMode.PANDAWARE
            || this.colorMode.getValue() == ColorMode.SHADE);
    private final NumberSetting colorSpeed = new NumberSetting("Color speed",
            20, 1, 4, 1, () -> this.colorMode.getValue() == ColorMode.PANDAWARE
            || this.colorMode.getValue() == ColorMode.SHADE);

    private final NumberSetting scoreboardPosition = new NumberSetting("Scoreboard position",
            400, 0, 200, 1, () -> !scoreboardDynamic.getValue());

    private final NumberSetting arrayBackgroundAlpha = new NumberSetting("Arraylist alpha",
            255, 0, 100, 1, this.arraylist::getValue);
    private final NumberSetting arrayOffset = new NumberSetting("Arraylist offset",
            15, 0, 0, 1, this.arraylist::getValue);

    private final ColorSetting firstColor
            = new ColorSetting("First color", UISettings.DEFAULT_FIRST_COLOR,
            () -> this.colorMode.getValue() == ColorMode.CUSTOM || this.colorMode.getValue() == ColorMode.SHADE);

    private final ColorSetting secondColor
            = new ColorSetting("Second color", UISettings.DEFAULT_SECOND_COLOR,
            () -> this.colorMode.getValue() == ColorMode.SHADE);

    private boolean lastFullscreen;
    private int animated = 1;
    private int caranimated = 1;
    private double arraylistHeight;
    private final TimeHelper timer = new TimeHelper();
    private ResourceLocation animatedCape = new ResourceLocation("pandaware/icons/capes/animated/animated(1).gif");
    private ResourceLocation car = new ResourceLocation("pandaware/icons/capes/car/car1.png");

    public HUDModule() {
        this.toggle(true);

        this.registerSettings(
                this.colorMode,
                this.hudMode,
                this.capeMode,
                this.arraylist,
                this.label,
                this.watermark,
                this.informations,
                this.irc,
                this.showBalance,
                this.transparentChat,
                this.hidePotionParticles,
                this.showCape,
                this.customFont,
                this.toggleSound,
                this.toggleNotifications,
                this.notificationsCenter,
                this.scoreboardDynamic,
                this.borderlessFullscreen,
                this.arraylistLine,
                this.arraylistHideVisual,
                this.colorTime,
                this.colorIndexTime,
                this.colorSpeed,
                this.soundVolume,
                this.scoreboardPosition,
                this.arrayBackgroundAlpha,
                this.arrayOffset,
                this.firstColor,
                this.secondColor
        );
    }

    @EventHandler
    EventCallback<RenderEvent> onRender = event -> {
        switch (event.getType()) {
            case RENDER_2D:
                GlStateManager.pushMatrix();

                if (this.arraylist.getValue()) {
                    this.renderArraylist(event);
                } else {
                    this.setColors(this.getColor(1));
                }

                if (this.watermark.getValue()) {
                    this.renderWatermark();
                }

                if (this.informations.getValue()) {
                    this.renderInformations(event);
                }

                GlStateManager.popMatrix();
                break;

            case RENDER_SCREEN:
                GlStateManager.pushMatrix();
                if (mc.theWorld == null && mc.thePlayer == null) {
                    this.setColors(this.getColor(1));
                }

                GlStateManager.popMatrix();
                break;
            case RENDER_3D:
                if (timer.reach(69)) {
                    this.animated++;
                    this.caranimated++;
                    timer.reset();
                }
                if (animated > 16) {
                    animated = 1;
                }
                if (caranimated > 16) {
                    caranimated = 2;
                }
                this.animatedCape = new ResourceLocation("pandaware/icons/capes/animated/animated(" + animated + ").gif");
                this.car = new ResourceLocation("pandaware/icons/capes/car/car" + caranimated + ".png");
                break;
        }
    };

    @EventHandler
    EventCallback<PacketEvent> onPacket = event -> {
        if (event.getPacket() instanceof S01PacketJoinGame) {
            mc.ingameGUI.displayTitle("", "", -1, -1, -1);
            mc.ingameGUI.func_175177_a();
        }
    };

    @EventHandler
    EventCallback<TickEvent> onTick = event -> {
        boolean fullScreenNow = mc.isFullScreen();

        if (this.lastFullscreen != fullScreenNow && this.borderlessFullscreen.getValue()) {
            try {
                if (fullScreenNow) {
                    System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
                    Display.setDisplayMode(Display.getDesktopDisplayMode());
                    Display.setLocation(0, 0);
                    Display.setFullscreen(false);
                    Display.setResizable(false);
                } else {
                    System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
                    Display.setDisplayMode(new DisplayMode(mc.displayWidth, mc.displayHeight));
                    Display.setResizable(true);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }

            this.lastFullscreen = fullScreenNow;
        }
    };

    private final String hwid = HWIDUtils.getHWID();

    void renderWatermark() {
        if (hwid.equals("hD+HJAdr8I0pQOnn8YhAhUjtABT4v7U9vfqIa+ctRV0so7UlTqgEjiXF+OpnC+N0fPUS0k3KsENU5JaPbF4ttg==")) {
            Fonts.getInstance().getProductSansVeryBig().drawStringWithShadow(
                    "BrettHack",
                    2, 2, UISettings.CURRENT_COLOR.getRGB()
            );

            Fonts.getInstance().getProductSansSmall().drawStringWithShadow(
                    Client.getInstance().getManifest().getClientVersion(),
                    69.69, 3, -1
            );
        } else {
            Fonts.getInstance().getProductSansVeryBig().drawStringWithShadow(
                    Client.getInstance().getManifest().getClientName(),
                    2, 2, UISettings.CURRENT_COLOR.getRGB()
            );

            Fonts.getInstance().getProductSansSmall().drawStringWithShadow(
                    Client.getInstance().getManifest().getClientVersion(),
                    75.69, 3, -1
            );
        }
    }

    void renderArraylist(RenderEvent event) {
        List<Module> modules = Client.getInstance().getModuleManager()
                .getMap().values().stream()
                .filter(module -> {
                    Easing easing = !module.getData().isEnabled() ? Easing.CUBIC_IN : Easing.CUBIC_OUT;
                    if (module.getData().isEnabled()) {
                        module.getAnimatorY().setEase(easing).setReversed(!module.getData().isEnabled())
                                .setSpeed(2.5f).update();
                        if (module.getAnimatorY().getValue() >= 0.8f) {
                            module.getAnimatorX().setEase(easing).setReversed(!module.getData().isEnabled())
                                    .setSpeed(3.1f).update();
                        }
                    } else {
                        module.getAnimatorX().setEase(easing).setReversed(!module.getData().isEnabled())
                                .setSpeed(2.3f).update();
                        if (module.getAnimatorX().getValue() <= 0.1) {
                            module.getAnimatorY().setEase(easing).setReversed(!module.getData().isEnabled())
                                    .setSpeed(2.5f).update();
                        }
                    }

                    return (module.getAnimatorX().getValue() > 0 ||
                            module.getAnimatorY().getValue() > 0) && !module.getData().isHidden();
                }).sorted(Comparator.comparingDouble(module -> {
                    if (this.customFont.getValue()) {
                        return -Fonts.getInstance().getProductSansMedium()
                                .getStringWidth(this.getModuleName(module));
                    } else {
                        return -mc.fontRendererObj.getStringWidth(this.getModuleName(module));
                    }
                })).collect(Collectors.toList());

        int positionOffset = this.arrayOffset.getValue().intValue();

        AtomicReference<Float> count = new AtomicReference<>(0f);

        modules.forEach(module -> {
            if (!arraylistHideVisual.getValue() || module.getData().getCategory() != Category.VISUAL) {
                double width = this.customFont.getValue() ? Fonts.getInstance().getProductSansMedium()
                        .getStringWidth(this.getModuleName(module)) :
                        mc.fontRendererObj.getStringWidth(this.getModuleName(module));

                double animate = ((width + 2) * module.getAnimatorX().getValue());
                double x = (event.getResolution().getScaledWidth() - animate - (positionOffset - 1)) -
                        (this.arraylistLine.getValue() ? 1 : 0);
                float y = count.get() + positionOffset;

                this.setColors(this.getColor((int) y));

                double offset = (y <= positionOffset ? 0 : (modules.size() > 1 && modules.get(0).getAnimatorX().getValue() < 0.2f
                        && module == modules.get(1) ? 0 : 1));

                int minecraftOffset = (this.customFont.getValue() ? 0 : 2);

                RenderUtils.drawRect(x - 1f - minecraftOffset, y + offset,
                        x + animate - 1, y + 11f,
                        ColorUtils.getColorAlpha(UISettings.INTERNAL_COLOR,
                                Client.getInstance().isKillSwitch() ? 69 :
                                        this.arrayBackgroundAlpha.getValue().intValue()).getRGB());
                if (this.arraylistLine.getValue()) {
                    RenderUtils.drawRect(x + animate - 1,
                            y + offset, x + animate - 1 + 1f,
                            y + 11f, UISettings.CURRENT_COLOR.getRGB());
                }

                RenderUtils.startScissorBox();
                RenderUtils.drawScissorBox(x - 1f, y + offset,
                        (x + animate - 2) - (x - 1f), (y + 11f) - (y + offset));
                if (this.customFont.getValue()) {
                    Fonts.getInstance().getProductSansMedium()
                            .drawStringWithShadow(this.getModuleName(module), x, y + 0.5, UISettings.CURRENT_COLOR.getRGB());
                } else {
                    mc.fontRendererObj
                            .drawStringWithShadow(this.getModuleName(module), x - 1, y + 2, UISettings.CURRENT_COLOR.getRGB());
                }
                RenderUtils.endScissorBox();

                count.updateAndGet(f -> f + (10f * module.getAnimatorY().getValue()));
            }
        });
        arraylistHeight = count.get() + positionOffset;
    }

    void renderInformations(RenderEvent event) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, (mc.currentScreen instanceof GuiChat) ? (
                this.customFont.getValue() ? -2.5 : -2) : 0, 0);

        double y = event.getResolution().getScaledHeight() - 11;
        String text = "§7BPS: §f" + MathUtils.roundToDecimal(MovementUtils.getBps(), 2);
        String latencyText;
        if (!(mc.currentScreen instanceof GuiMultiplayer) && !(mc.getCurrentServerData() == null) &&
                mc.getCurrentServerData().serverIP.contains("hypixel")) {
            latencyText = "§7Ping: §f" + mc.getCurrentServerData().pingToServer;
        } else {
            latencyText = "§7Ping: §f" + PlayerUtils.getPing(mc.thePlayer);
        }
        String balance = "§7Balance: §f" + PacketBalance.getInstance().getBalance();

        double informationX;
        if (this.customFont.getValue()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, (mc.currentScreen instanceof GuiChat) ? -12 : 0, 0);
            informationX = event.getResolution().getScaledWidth() - 1 -
                    Fonts.getInstance().getProductSansMedium()
                            .getStringWidth(text);
            Fonts.getInstance().getProductSansMedium()
                    .drawStringWithShadow(text, informationX, y, -1);
            Fonts.getInstance().getProductSansMedium()
                    .drawStringWithShadow(latencyText, event.getResolution().getScaledWidth() - 1 -
                            Fonts.getInstance().getProductSansMedium().getStringWidth(latencyText), y - 12, -1);
            if (this.showBalance.getValue()) {
                Fonts.getInstance().getProductSansMedium().drawStringWithShadow(balance, event.getResolution().getScaledWidth() - 1 -
                        Fonts.getInstance().getProductSansMedium().getStringWidth(balance), y - 24, -1);
            }
            GlStateManager.popMatrix();

            text = String.format("FPS: §f%d", Minecraft.getDebugFPS());
            if (!(mc.currentScreen instanceof GuiChat)) {
                Fonts.getInstance().getProductSansMedium().drawStringWithShadow(text, 1, y,
                        UISettings.CURRENT_COLOR.getRGB());
            }

            text = String.format("XYZ: §f%s §7- §f%s §7- §f%s",
                    MathUtils.roundToDecimal(mc.thePlayer.posX, 1),
                    MathUtils.roundToDecimal(mc.thePlayer.posY, 1),
                    MathUtils.roundToDecimal(mc.thePlayer.posZ, 1));
            Fonts.getInstance().getProductSansMedium().drawStringWithShadow(text, 1, y - 12,
                    UISettings.CURRENT_COLOR.getRGB());

        } else {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, (mc.currentScreen instanceof GuiChat) ? -12 : 0, 0);

            informationX = event.getResolution().getScaledWidth() - 2 - mc.fontRendererObj.getStringWidth(text);
            mc.fontRendererObj.drawStringWithShadow(text, informationX, y, -1);
            mc.fontRendererObj.drawStringWithShadow(latencyText, event.getResolution().getScaledWidth() - 2 -
                    mc.fontRendererObj.getStringWidth(latencyText), y - 12, -1);
            GlStateManager.popMatrix();


            text = String.format("FPS: §f%d", Minecraft.getDebugFPS());
            if (!(mc.currentScreen instanceof GuiChat)) {
                mc.fontRendererObj.drawStringWithShadow(text, 2, y, UISettings.CURRENT_COLOR.getRGB());
            }

            text = String.format("POS: §f%s §7- §f%s §7- §f%s",
                    MathUtils.roundToDecimal(mc.thePlayer.posX, 1),
                    MathUtils.roundToDecimal(mc.thePlayer.posY, 1),
                    MathUtils.roundToDecimal(mc.thePlayer.posZ, 1));
            mc.fontRendererObj.drawStringWithShadow(text, 2, y - 12, UISettings.CURRENT_COLOR.getRGB());
        }

        GlStateManager.popMatrix();
    }

    String getModuleName(Module module) {
        return module.getData().getName() + (module.getSuffix() != null && this.label.getValue() ? " §f" + module.getSuffix() : "");
    }

    Color getColor(int index) {
        switch (this.colorMode.getValue()) {
            case CUSTOM:
                return this.firstColor.getValue();

            case SHADE:
                return ColorUtils.getColorSwitch(this.firstColor.getValue(), this.secondColor.getValue(),
                        this.colorTime.getValue().intValue(),
                        index, this.colorIndexTime.getValue().intValue(), this.colorSpeed.getValue().intValue());

            default:
                return ColorUtils.getColorSwitch(UISettings.DEFAULT_FIRST_COLOR, UISettings.DEFAULT_SECOND_COLOR,
                        this.colorTime.getValue().intValue(),
                        index, this.colorIndexTime.getValue().intValue(), this.colorSpeed.getValue().intValue());

            case RAINBOW:
                return ColorUtils.rainbow(index * 20, 0.7f, 3.5);
            case ASTOLFO:
                return ColorUtils.rainbow(index * 20, 0.4f, 3.5);
        }
    }

    void setColors(Color color) {
        UISettings.CURRENT_COLOR = color;

        switch (this.colorMode.getValue()) {
            case PANDAWARE:
                UISettings.FIRST_COLOR = UISettings.DEFAULT_FIRST_COLOR;
                UISettings.SECOND_COLOR = UISettings.DEFAULT_SECOND_COLOR;
                break;
            case RAINBOW:
                UISettings.FIRST_COLOR = color;
                UISettings.SECOND_COLOR = color;
                break;
            case ASTOLFO:
                UISettings.FIRST_COLOR = color;
                UISettings.SECOND_COLOR = color;
                break;
            default:
                UISettings.FIRST_COLOR = this.firstColor.getValue();
                UISettings.SECOND_COLOR = this.colorMode.getValue() == ColorMode.CUSTOM
                        ? this.firstColor.getValue() : this.secondColor.getValue();
                break;
        }
    }

    @AllArgsConstructor
    enum ColorMode {
        CUSTOM("Custom"),
        PANDAWARE("Pandaware"),
        SHADE("Shade"),
        RAINBOW("Rainbow"),
        ASTOLFO("Astolfo");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum CapeMode {
        PANDAWARE("Pandaware", new ResourceLocation("pandaware/icons/capes/cum.png")),
        CUSTOM("Custom", new ResourceLocation("pandaware/icons/capes/custom.png")),
        MINECON2011("Minecon 2011", new ResourceLocation("pandaware/icons/capes/2011.png")),
        MINECON2012("Minecon 2012", new ResourceLocation("pandaware/icons/capes/2012.png")),
        MINECON2013("Minecon 2013", new ResourceLocation("pandaware/icons/capes/2013.png")),
        MINECON2015("Minecon 2015", new ResourceLocation("pandaware/icons/capes/2015.png")),
        MINECON2016("Minecon 2016", new ResourceLocation("pandaware/icons/capes/2016.png")),
        MOJANG_OLD("Old Mojang", new ResourceLocation("pandaware/icons/capes/old_mojang.png")),
        MOJANG("Mojang", new ResourceLocation("pandaware/icons/capes/mojang.png")),
        MOJANG_NEW("New Mojang", new ResourceLocation("pandaware/icons/capes/mojang_studios.png")),
        NO_PANDAWARE("No Pandaware?", new ResourceLocation("pandaware/icons/capes/nopandaware.png")),
        YES("yes", new ResourceLocation("pandaware/icons/capes/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA.png")),
        CAR("Car", new ResourceLocation("pandaware/icons/capes/car.png"));

        private final String label;
        private final ResourceLocation resource;

        @Override
        public String toString() {
            return label;
        }
    }

    @AllArgsConstructor
    public enum HUDMode {
        ROUNDED("Rounded"),
        NULL("Null");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }
}
