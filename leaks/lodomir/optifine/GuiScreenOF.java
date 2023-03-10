/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;

public class GuiScreenOF
extends GuiScreen {
    protected void actionPerformedRightClick(GuiButton p_actionPerformedRightClick_1_) throws IOException {
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        GuiButton guibutton;
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 1 && (guibutton = GuiScreenOF.getSelectedButton(this.buttonList, mouseX, mouseY)) != null && guibutton.enabled) {
            guibutton.playPressSound(this.mc.getSoundHandler());
            this.actionPerformedRightClick(guibutton);
        }
    }

    public static GuiButton getSelectedButton(List<GuiButton> p_getSelectedButton_0_, int p_getSelectedButton_1_, int p_getSelectedButton_2_) {
        for (int i = 0; i < p_getSelectedButton_0_.size(); ++i) {
            GuiButton guibutton = p_getSelectedButton_0_.get(i);
            if (!guibutton.visible) continue;
            int j = GuiVideoSettings.getButtonWidth(guibutton);
            int k = GuiVideoSettings.getButtonHeight(guibutton);
            if (p_getSelectedButton_1_ < guibutton.xPosition || p_getSelectedButton_2_ < guibutton.yPosition || p_getSelectedButton_1_ >= guibutton.xPosition + j || p_getSelectedButton_2_ >= guibutton.yPosition + k) continue;
            return guibutton;
        }
        return null;
    }
}

