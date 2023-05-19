package club.marsh.bloom.impl.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import java.awt.Color;

public class AuthorizingUI extends GuiScreen {
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int j = height / 4 + 30;
        drawCenteredString(Minecraft.fontRendererObj, "AUTHORIZING", width / 2, j, Color.green.getRGB());
    }
}
