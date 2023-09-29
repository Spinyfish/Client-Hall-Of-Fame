package juul.ui;

import juul.util.Utils;
import juul.util.render.RenderUtil;
import juul.util.render.RoundedUtil;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;

public class NewClickGui extends GuiScreen implements Utils {

    @Override
    public void initGui() {

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == 1) {
            Utils.mc.displayGuiScreen(null);
        }
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float rectWidth = 400;
        float rectHeight = 300;
        RoundedUtil.drawRound(40, 40, rectWidth, rectHeight, 10, new Color(54,57,63));


    }



}
