package net.optifine.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;

import java.io.IOException;
import java.util.List;

public class GuiScreenOF extends GuiScreen {
    protected void actionPerformedRightClick(final GuiButton button) throws IOException {
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 1) {
            final GuiButton guibutton = getSelectedButton(mouseX, mouseY, this.buttonList);

            if (guibutton != null && guibutton.enabled) {
                guibutton.playPressSound(this.mc.getSoundHandler());
                this.actionPerformedRightClick(guibutton);
            }
        }
    }

    public static GuiButton getSelectedButton(final int x, final int y, final List<GuiButton> listButtons) {
        for (final GuiButton guibutton : listButtons) {
            if (guibutton.visible) {
                final int j = GuiVideoSettings.getButtonWidth(guibutton);
                final int k = GuiVideoSettings.getButtonHeight(guibutton);

                if (x >= guibutton.xPosition && y >= guibutton.yPosition && x < guibutton.xPosition + j && y < guibutton.yPosition + k) {
                    return guibutton;
                }
            }
        }

        return null;
    }
}
