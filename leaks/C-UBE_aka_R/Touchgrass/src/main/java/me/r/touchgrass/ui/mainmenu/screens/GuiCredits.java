package me.r.touchgrass.ui.mainmenu.screens;

import me.r.touchgrass.font.FontUtil;
import me.r.touchgrass.utils.Utils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

/**
 * Created by r on 26/02/2021
 */
public class GuiCredits extends GuiScreen {

    public GuiCredits() {}

    public void initGui() {
        this.buttonList.add(new GuiButton(1, Utils.getScaledRes().getScaledWidth() / 3, Utils.getScaledRes().getScaledHeight() - 30, Utils.getScaledRes().getScaledWidth() / 3 - 1, 20, "Back"));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int left = Utils.getScaledRes().getScaledWidth() / 3 - 24;
        int right = Utils.getScaledRes().getScaledWidth() - Utils.getScaledRes().getScaledWidth() / 3 + 24;
        drawRect(left, 0, right, Utils.getScaledRes().getScaledHeight(), 0x55000000);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 1:
                mc.displayGuiScreen(new GuiMainMenu());
                break;
        }
    }

}

