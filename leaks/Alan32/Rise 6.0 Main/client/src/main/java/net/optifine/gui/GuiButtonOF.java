package net.optifine.gui;

import net.minecraft.client.gui.GuiButton;

public class GuiButtonOF extends GuiButton {
    public GuiButtonOF(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    public GuiButtonOF(final int buttonId, final int x, final int y, final String buttonText) {
        super(buttonId, x, y, buttonText);
    }
}
