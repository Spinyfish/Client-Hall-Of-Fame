/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.gui;

import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.settings.GameSettings;
import net.optifine.gui.IOptionControl;

public class GuiOptionButtonOF
extends GuiOptionButton
implements IOptionControl {
    private GameSettings.Options option = null;

    public GuiOptionButtonOF(int id, int x, int y, GameSettings.Options option, String text) {
        super(id, x, y, option, text);
        this.option = option;
    }

    @Override
    public GameSettings.Options getOption() {
        return this.option;
    }
}

