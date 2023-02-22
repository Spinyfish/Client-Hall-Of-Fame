package net.optifine.shaders.gui;

import com.alan.clients.util.font.impl.minecraft.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.src.Config;
import net.minecraft.util.MathHelper;
import net.optifine.Lang;
import net.optifine.gui.GuiScreenOF;
import net.optifine.gui.TooltipManager;
import net.optifine.gui.TooltipProviderShaderOptions;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderOptionProfile;
import net.optifine.shaders.config.ShaderOptionScreen;

public class GuiShaderOptions extends GuiScreenOF {
    private final GuiScreen prevScreen;
    protected String title;
    private final GameSettings settings;
    private final TooltipManager tooltipManager;
    private String screenName;
    private String screenText;
    private boolean changed;
    public static final String OPTION_PROFILE = "<profile>";
    public static final String OPTION_EMPTY = "<empty>";
    public static final String OPTION_REST = "*";

    public GuiShaderOptions(final GuiScreen guiscreen, final GameSettings gamesettings) {
        this.tooltipManager = new TooltipManager(this, new TooltipProviderShaderOptions());
        this.screenName = null;
        this.screenText = null;
        this.changed = false;
        this.title = "Shader Options";
        this.prevScreen = guiscreen;
        this.settings = gamesettings;
    }

    public GuiShaderOptions(final GuiScreen guiscreen, final GameSettings gamesettings, final String screenName) {
        this(guiscreen, gamesettings);
        this.screenName = screenName;

        if (screenName != null) {
            this.screenText = Shaders.translate("screen." + screenName, screenName);
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        this.title = I18n.format("of.options.shaderOptionsTitle");
        final int i = 100;
        int j = 0;
        final int k = 30;
        final int l = 20;
        final int i1 = 120;
        final int j1 = 20;
        int k1 = Shaders.getShaderPackColumns(this.screenName, 2);
        final ShaderOption[] ashaderoption = Shaders.getShaderPackOptions(this.screenName);

        if (ashaderoption != null) {
            final int l1 = MathHelper.ceiling_double_int((double) ashaderoption.length / 9.0D);

            if (k1 < l1) {
                k1 = l1;
            }

            for (int i2 = 0; i2 < ashaderoption.length; ++i2) {
                final ShaderOption shaderoption = ashaderoption[i2];

                if (shaderoption != null && shaderoption.isVisible()) {
                    final int j2 = i2 % k1;
                    final int k2 = i2 / k1;
                    final int l2 = Math.min(this.width / k1, 200);
                    j = (this.width - l2 * k1) / 2;
                    final int i3 = j2 * l2 + 5 + j;
                    final int j3 = k + k2 * l;
                    final int k3 = l2 - 10;
                    final String s = getButtonText(shaderoption, k3);
                    final GuiButtonShaderOption guibuttonshaderoption;

                    if (Shaders.isShaderPackOptionSlider(shaderoption.getName())) {
                        guibuttonshaderoption = new GuiSliderShaderOption(i + i2, i3, j3, k3, j1, shaderoption, s);
                    } else {
                        guibuttonshaderoption = new GuiButtonShaderOption(i + i2, i3, j3, k3, j1, shaderoption, s);
                    }

                    guibuttonshaderoption.enabled = shaderoption.isEnabled();
                    this.buttonList.add(guibuttonshaderoption);
                }
            }
        }

        this.buttonList.add(new GuiButton(201, this.width / 2 - i1 - 20, this.height / 6 + 168 + 11, i1, j1, I18n.format("controls.reset")));
        this.buttonList.add(new GuiButton(200, this.width / 2 + 20, this.height / 6 + 168 + 11, i1, j1, I18n.format("gui.done")));
    }

    public static String getButtonText(final ShaderOption so, final int btnWidth) {
        String s = so.getNameText();

        if (so instanceof ShaderOptionScreen) {
            final ShaderOptionScreen shaderoptionscreen = (ShaderOptionScreen) so;
            return s + "...";
        } else {
            final FontRenderer fontrenderer = Config.getMinecraft().fontRendererObj;

            for (final int i = fontrenderer.width(": " + Lang.getOff()) + 5; fontrenderer.width(s) + i >= btnWidth && s.length() > 0; s = s.substring(0, s.length() - 1)) {
            }

            final String s1 = so.isChanged() ? so.getValueColor(so.getValue()) : "";
            final String s2 = so.getValueText(so.getValue());
            return s + ": " + s1 + s2;
        }
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(final GuiButton guibutton) {
        if (guibutton.enabled) {
            if (guibutton.id < 200 && guibutton instanceof GuiButtonShaderOption) {
                final GuiButtonShaderOption guibuttonshaderoption = (GuiButtonShaderOption) guibutton;
                final ShaderOption shaderoption = guibuttonshaderoption.getShaderOption();

                if (shaderoption instanceof ShaderOptionScreen) {
                    final String s = shaderoption.getName();
                    final GuiShaderOptions guishaderoptions = new GuiShaderOptions(this, this.settings, s);
                    this.mc.displayGuiScreen(guishaderoptions);
                    return;
                }

                if (isShiftKeyDown()) {
                    shaderoption.resetValue();
                } else if (guibuttonshaderoption.isSwitchable()) {
                    shaderoption.nextValue();
                }

                this.updateAllButtons();
                this.changed = true;
            }

            if (guibutton.id == 201) {
                final ShaderOption[] ashaderoption = Shaders.getChangedOptions(Shaders.getShaderPackOptions());

                for (int i = 0; i < ashaderoption.length; ++i) {
                    final ShaderOption shaderoption1 = ashaderoption[i];
                    shaderoption1.resetValue();
                    this.changed = true;
                }

                this.updateAllButtons();
            }

            if (guibutton.id == 200) {
                if (this.changed) {
                    Shaders.saveShaderPackOptions();
                    this.changed = false;
                    Shaders.uninit();
                }

                this.mc.displayGuiScreen(this.prevScreen);
            }
        }
    }

    protected void actionPerformedRightClick(final GuiButton btn) {
        if (btn instanceof GuiButtonShaderOption) {
            final GuiButtonShaderOption guibuttonshaderoption = (GuiButtonShaderOption) btn;
            final ShaderOption shaderoption = guibuttonshaderoption.getShaderOption();

            if (isShiftKeyDown()) {
                shaderoption.resetValue();
            } else if (guibuttonshaderoption.isSwitchable()) {
                shaderoption.prevValue();
            }

            this.updateAllButtons();
            this.changed = true;
        }
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed() {
        super.onGuiClosed();

        if (this.changed) {
            Shaders.saveShaderPackOptions();
            this.changed = false;
            Shaders.uninit();
        }
    }

    private void updateAllButtons() {
        for (final GuiButton guibutton : this.buttonList) {
            if (guibutton instanceof GuiButtonShaderOption) {
                final GuiButtonShaderOption guibuttonshaderoption = (GuiButtonShaderOption) guibutton;
                final ShaderOption shaderoption = guibuttonshaderoption.getShaderOption();

                if (shaderoption instanceof ShaderOptionProfile) {
                    final ShaderOptionProfile shaderoptionprofile = (ShaderOptionProfile) shaderoption;
                    shaderoptionprofile.updateProfile();
                }

                guibuttonshaderoption.displayString = getButtonText(shaderoption, guibuttonshaderoption.getButtonWidth());
                guibuttonshaderoption.valueChanged();
            }
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(final int x, final int y, final float f) {
        this.drawDefaultBackground();

        if (this.screenText != null) {
            this.drawCenteredString(this.fontRendererObj, this.screenText, this.width / 2, 15, 16777215);
        } else {
            this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
        }

        super.drawScreen(x, y, f);
        this.tooltipManager.drawTooltips(x, y, this.buttonList);
    }
}
