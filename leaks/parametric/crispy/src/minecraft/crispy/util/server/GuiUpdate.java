package crispy.util.server;

import arithmo.gui.altmanager.Colors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class GuiUpdate extends GuiScreen {


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(Minecraft.fontRendererObj, "Please wait while we download the latest version!", ScaledResolution.scaledWidth / 2, ScaledResolution.scaledHeight / 2, Colors.getColor(255, 255, 255));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
