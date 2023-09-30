package net.optifine.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiQualitySettingsOF extends GuiScreen {
    private final GuiScreen prevScreen;
    protected String title;
    private final GameSettings settings;
    private static final GameSettings.Options[] enumOptions = new GameSettings.Options[]{GameSettings.Options.MIPMAP_LEVELS, GameSettings.Options.MIPMAP_TYPE, GameSettings.Options.AF_LEVEL, GameSettings.Options.AA_LEVEL, GameSettings.Options.CLEAR_WATER, GameSettings.Options.RANDOM_ENTITIES, GameSettings.Options.BETTER_GRASS, GameSettings.Options.BETTER_SNOW, GameSettings.Options.CUSTOM_FONTS, GameSettings.Options.CUSTOM_COLORS, GameSettings.Options.CONNECTED_TEXTURES, GameSettings.Options.NATURAL_TEXTURES, GameSettings.Options.CUSTOM_SKY, GameSettings.Options.CUSTOM_ITEMS, GameSettings.Options.CUSTOM_ENTITY_MODELS, GameSettings.Options.CUSTOM_GUIS, GameSettings.Options.EMISSIVE_TEXTURES};
    private final TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderOptions());

    public GuiQualitySettingsOF(final GuiScreen guiscreen, final GameSettings gamesettings) {
        this.prevScreen = guiscreen;
        this.settings = gamesettings;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        this.title = I18n.format("of.options.qualityTitle");
        this.buttonList.clear();

        for (int i = 0; i < enumOptions.length; ++i) {
            final GameSettings.Options gamesettings$options = enumOptions[i];
            final int j = this.width / 2 - 155 + i % 2 * 160;
            final int k = this.height / 6 + 21 * (i / 2) - 12;

            if (!gamesettings$options.getEnumFloat()) {
                this.buttonList.add(new GuiOptionButtonOF(gamesettings$options.returnEnumOrdinal(), j, k, gamesettings$options, this.settings.getKeyBinding(gamesettings$options)));
            } else {
                this.buttonList.add(new GuiOptionSliderOF(gamesettings$options.returnEnumOrdinal(), j, k, gamesettings$options));
            }
        }

        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168 + 11, I18n.format("gui.done")));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(final GuiButton guibutton) {
        if (guibutton.enabled) {
            if (guibutton.id < 200 && guibutton instanceof GuiOptionButton) {
                this.settings.setOptionValue(((GuiOptionButton) guibutton).returnEnumOptions(), 1);
                guibutton.displayString = this.settings.getKeyBinding(GameSettings.Options.getEnumOptions(guibutton.id));
            }

            if (guibutton.id == 200) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.prevScreen);
            }

            if (guibutton.id != GameSettings.Options.AA_LEVEL.ordinal()) {
                final ScaledResolution scaledresolution = new ScaledResolution(this.mc);
                this.setWorldAndResolution(this.mc, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
            }
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(final int x, final int y, final float f) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
        super.drawScreen(x, y, f);
        this.tooltipManager.drawTooltips(x, y, this.buttonList);
    }
}
