package net.optifine.gui;

import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.settings.GameSettings;

public class GuiOptionButtonOF extends GuiOptionButton implements IOptionControl {
    private GameSettings.Options option = null;

    public GuiOptionButtonOF(final int id, final int x, final int y, final GameSettings.Options option, final String text) {
        super(id, x, y, option, text);
        this.option = option;
    }

    public GameSettings.Options getOption() {
        return this.option;
    }
}
