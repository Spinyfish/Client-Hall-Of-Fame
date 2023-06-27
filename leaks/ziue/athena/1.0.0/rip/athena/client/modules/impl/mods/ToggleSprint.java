package rip.athena.client.modules.impl.mods;

import rip.athena.client.config.*;
import java.awt.*;
import rip.athena.client.gui.hud.*;
import rip.athena.client.modules.*;
import net.minecraft.client.*;
import net.minecraft.util.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.settings.*;
import java.text.*;
import rip.athena.client.utils.font.*;
import org.lwjgl.opengl.*;
import rip.athena.client.*;
import rip.athena.client.utils.render.*;

public class ToggleSprint extends Module
{
    @ConfigValue.List(name = "Display Mode", values = { "Circle", "Modern", "Fade", "Old" }, description = "Chose display of background")
    private String backgroundMode;
    @ConfigValue.Boolean(name = "Background")
    private boolean backGround;
    @ConfigValue.Color(name = "Background Color")
    private Color background;
    @ConfigValue.Boolean(name = "Toggle Sneak")
    private static boolean optionToggleSneak;
    @ConfigValue.Boolean(name = "Toggle Sprint")
    public static boolean optionToggleSprint;
    @ConfigValue.Boolean(name = "Double Tap Sprint")
    public static boolean optionDoubleTap;
    @ConfigValue.Boolean(name = "Fly Boost")
    public static boolean optionEnableFlyBoost;
    @ConfigValue.Double(name = "Fly Boost Speed Vertical", min = 0.1, max = 20.0)
    public static double flyboostspeedVertical;
    @ConfigValue.Double(name = "Fly Boost Speed Horizontal", min = 0.1, max = 20.0)
    public static double flyboostspeedHorizontal;
    @ConfigValue.Boolean(name = "Custom Font")
    private static boolean customFont;
    @ConfigValue.Color(name = "Color")
    private Color color;
    public static boolean isDisabled;
    public static boolean canDoubleTap;
    public static boolean sprint;
    public static boolean sprintHeldAndReleased;
    public static boolean sprintDoubleTapped;
    public static long lastPressed;
    public static long lastSprintPressed;
    public static boolean handledSneakPress;
    public static boolean handledSprintPress;
    public static boolean wasRiding;
    public static boolean wasSprintDisabled;
    public static String textForHUD;
    private HUDElement hud;
    private int width;
    private int height;
    
    public ToggleSprint() {
        super("ToggleSprint", Category.MODS, "Athena/gui/mods/entityhud.png");
        this.backgroundMode = "Circle";
        this.backGround = false;
        this.background = new Color(0, 0, 0, 150);
        this.color = Color.WHITE;
        this.width = 20;
        this.height = 10;
        this.addHUD(this.hud = new HUDElement("togglesprint", this.width, this.height) {
            @Override
            public void onRender() {
                ToggleSprint.this.render();
            }
        });
    }
    
    public static void update(final Minecraft mc, final MovementInputFromOptions options, final EntityPlayerSP thisPlayer) {
        options.moveStrafe = 0.0f;
        options.moveForward = 0.0f;
        final GameSettings settings = mc.gameSettings;
        if (settings.keyBindForward.isKeyDown()) {
            ++options.moveForward;
        }
        if (settings.keyBindBack.isKeyDown()) {
            --options.moveForward;
        }
        if (settings.keyBindLeft.isKeyDown()) {
            ++options.moveStrafe;
        }
        if (settings.keyBindRight.isKeyDown()) {
            --options.moveStrafe;
        }
        options.jump = settings.keyBindJump.isKeyDown();
        if (ToggleSprint.optionToggleSneak) {
            if (settings.keyBindSneak.isKeyDown() && !ToggleSprint.handledSneakPress) {
                if (thisPlayer.isRiding() || thisPlayer.capabilities.isFlying) {
                    options.sneak = true;
                    ToggleSprint.wasRiding = thisPlayer.isRiding();
                }
                else {
                    options.sneak = !options.sneak;
                }
                ToggleSprint.lastPressed = System.currentTimeMillis();
                ToggleSprint.handledSneakPress = true;
            }
            if (!settings.keyBindSneak.isKeyDown() && ToggleSprint.handledSneakPress) {
                if (thisPlayer.capabilities.isFlying || ToggleSprint.wasRiding) {
                    options.sneak = false;
                    ToggleSprint.wasRiding = false;
                }
                else if (System.currentTimeMillis() - ToggleSprint.lastPressed > 300L) {
                    options.sneak = false;
                }
                ToggleSprint.handledSneakPress = false;
            }
        }
        else {
            options.sneak = settings.keyBindSneak.isKeyDown();
        }
        if (options.sneak) {
            options.moveStrafe *= (float)0.3;
            options.moveForward *= (float)0.3;
        }
        final boolean enoughHunger = thisPlayer.getFoodStats().getFoodLevel() > 6.0f;
        final boolean canSprint = !options.sneak && enoughHunger;
        ToggleSprint.isDisabled = !ToggleSprint.optionToggleSprint;
        ToggleSprint.canDoubleTap = ToggleSprint.optionDoubleTap;
        if ((canSprint || ToggleSprint.isDisabled) && settings.keyBindSprint.isKeyDown() && !ToggleSprint.handledSprintPress && !ToggleSprint.isDisabled) {
            ToggleSprint.sprint = !ToggleSprint.sprint;
            ToggleSprint.lastSprintPressed = System.currentTimeMillis();
            ToggleSprint.handledSprintPress = true;
            ToggleSprint.sprintHeldAndReleased = false;
        }
        if ((canSprint || ToggleSprint.isDisabled) && !settings.keyBindSprint.isKeyDown() && ToggleSprint.handledSprintPress) {
            if (System.currentTimeMillis() - ToggleSprint.lastSprintPressed > 300L) {
                ToggleSprint.sprintHeldAndReleased = true;
            }
            ToggleSprint.handledSprintPress = false;
        }
        UpdateStatus(options, thisPlayer, settings);
    }
    
    public static void UpdateSprint(final boolean newValue, final boolean doubleTapped) {
        ToggleSprint.sprint = newValue;
        ToggleSprint.sprintDoubleTapped = doubleTapped;
    }
    
    public static void UpdateStatus(final MovementInputFromOptions options, final EntityPlayerSP thisPlayer, final GameSettings settings) {
        String output = "";
        final boolean isFlying = thisPlayer.capabilities.isFlying;
        final boolean isRiding = thisPlayer.isRiding();
        final boolean isHoldingSneak = settings.keyBindSneak.isKeyDown();
        final boolean isHoldingSprint = settings.keyBindSprint.isKeyDown();
        if (isFlying) {
            final DecimalFormat numFormat = new DecimalFormat("#.00");
            final String speedVert = numFormat.format(ToggleSprint.flyboostspeedVertical);
            final String speedHori = numFormat.format(ToggleSprint.flyboostspeedHorizontal);
            String label = speedVert + "x, " + speedHori + "x";
            if (speedVert.equalsIgnoreCase(speedHori)) {
                label = speedVert + "x";
            }
            if (ToggleSprint.optionEnableFlyBoost && ToggleSprint.sprint) {
                output = output + "[Flying (" + label + " boost)]  ";
            }
            else {
                output += "[Flying]  ";
            }
        }
        if (isRiding) {
            output += "[Riding]  ";
        }
        if (options.sneak) {
            if (isFlying) {
                output += "[Descending]  ";
            }
            else if (isRiding) {
                output += "[Dismounting]  ";
            }
            else if (isHoldingSneak) {
                output += "[Sneaking (Key Held)]  ";
            }
            else {
                output += "[Sneaking (Toggled)]  ";
            }
        }
        else if (ToggleSprint.sprint) {
            if (!isFlying && !isRiding) {
                final boolean isVanilla = ToggleSprint.sprintHeldAndReleased || ToggleSprint.isDisabled || ToggleSprint.sprintDoubleTapped;
                if (isHoldingSprint) {
                    output += "[Sprinting (Key Held)]";
                }
                else if (isVanilla) {
                    output += "[Sprinting (Vanilla)]";
                }
                else {
                    output += "[Sprinting (Toggled)]";
                }
            }
        }
        else {
            output += "[Inactive] ";
        }
        ToggleSprint.textForHUD = output;
    }
    
    public void render() {
        if (!ToggleSprint.customFont) {
            this.hud.setWidth(ToggleSprint.mc.fontRendererObj.getStringWidth(ToggleSprint.textForHUD) + 1);
            this.hud.setHeight(ToggleSprint.mc.fontRendererObj.FONT_HEIGHT + 1);
        }
        else {
            this.hud.setWidth(FontManager.getProductSansRegular(25).width(ToggleSprint.textForHUD));
            this.hud.setHeight((int)(FontManager.getProductSansRegular(25).height() + 3.0f));
        }
        if (ToggleSprint.mc.gameSettings.showDebugInfo) {
            return;
        }
        GL11.glPushMatrix();
        if (this.backGround) {
            if (this.backgroundMode.equalsIgnoreCase("Modern")) {
                if (Athena.INSTANCE.getThemeManager().getTheme().isTriColor()) {
                    RoundedUtils.drawGradientRound((float)this.hud.getX(), (float)this.hud.getY(), (float)this.hud.getWidth(), (float)this.hud.getHeight(), 6.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getTheme().getThirdColor(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor());
                }
                else {
                    RoundedUtils.drawGradientRound((float)this.hud.getX(), (float)this.hud.getY(), (float)this.hud.getWidth(), (float)this.hud.getHeight(), 6.0f, Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getFirstColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor(), Athena.INSTANCE.getThemeManager().getTheme().getSecondColor());
                }
            }
            else if (this.backgroundMode.equalsIgnoreCase("Circle")) {
                RoundedUtils.drawGradientRound((float)this.hud.getX(), (float)this.hud.getY(), (float)this.hud.getWidth(), (float)this.hud.getHeight(), 6.0f, ColorUtil.getClientColor(0, 255), ColorUtil.getClientColor(90, 255), ColorUtil.getClientColor(180, 255), ColorUtil.getClientColor(270, 255));
            }
            else if (this.backgroundMode.equalsIgnoreCase("Fade")) {
                RoundedUtils.drawRoundedRect((float)this.hud.getX(), (float)this.hud.getY(), (float)(this.hud.getX() + this.width), (float)(this.hud.getY() + this.height), 8.0f, Athena.INSTANCE.getThemeManager().getTheme().getAccentColor().getRGB());
            }
            else {
                RoundedUtils.drawRoundedRect((float)this.hud.getX(), (float)this.hud.getY(), (float)(this.hud.getX() + this.width), (float)(this.hud.getY() + this.height), 12.0f, this.background.getRGB());
            }
        }
        if (!ToggleSprint.customFont) {
            if (this.color.getBlue() == 5 && this.color.getRed() == 5 && this.color.getGreen() == 5) {
                DrawUtils.drawChromaString(ToggleSprint.textForHUD, this.hud.getX() + 1, this.hud.getY() + 1, true, true);
            }
            else if (this.color.getBlue() == 6 && this.color.getRed() == 6 && this.color.getGreen() == 6) {
                DrawUtils.drawChromaString(ToggleSprint.textForHUD, this.hud.getX() + 1, this.hud.getY() + 1, false, true);
            }
            else {
                ToggleSprint.mc.fontRendererObj.drawStringWithShadow(ToggleSprint.textForHUD, this.hud.getX() + 1.0f, this.hud.getY() + 1.0f, this.color.getRGB());
            }
        }
        else if (this.color.getBlue() == 5 && this.color.getRed() == 5 && this.color.getGreen() == 5) {
            DrawUtils.drawCustomFontChromaString(FontManager.getProductSansRegular(25), ToggleSprint.textForHUD, this.hud.getX() + 1, this.hud.getY() + 3, true, true);
        }
        else if (this.color.getBlue() == 6 && this.color.getRed() == 6 && this.color.getGreen() == 6) {
            DrawUtils.drawCustomFontChromaString(FontManager.getProductSansRegular(25), ToggleSprint.textForHUD, this.hud.getX() + 1, this.hud.getY() + 3, false, true);
        }
        else {
            FontManager.getProductSansRegular(25).drawString(ToggleSprint.textForHUD, this.hud.getX() + 1, this.hud.getY() + 3, this.color.getRGB());
        }
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }
    
    static {
        ToggleSprint.optionToggleSneak = true;
        ToggleSprint.optionToggleSprint = true;
        ToggleSprint.optionDoubleTap = false;
        ToggleSprint.optionEnableFlyBoost = true;
        ToggleSprint.flyboostspeedVertical = 2.0;
        ToggleSprint.flyboostspeedHorizontal = 2.0;
        ToggleSprint.customFont = false;
        ToggleSprint.sprint = false;
        ToggleSprint.sprintHeldAndReleased = false;
        ToggleSprint.sprintDoubleTapped = false;
        ToggleSprint.wasSprintDisabled = false;
        ToggleSprint.textForHUD = "";
    }
}
