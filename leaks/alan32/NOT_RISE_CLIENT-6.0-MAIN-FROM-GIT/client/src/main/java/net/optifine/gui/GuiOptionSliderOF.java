package net.optifine.gui;

import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.settings.GameSettings;

public class GuiOptionSliderOF extends GuiOptionSlider implements IOptionControl {
    private GameSettings.Options option = null;

    public GuiOptionSliderOF(final int id, final int x, final int y, final GameSettings.Options option) {
        super(id, x, y, option);
        this.option = option;
    }

    public GameSettings.Options getOption() {
        return this.option;
    }
}
