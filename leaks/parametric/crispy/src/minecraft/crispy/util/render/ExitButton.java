package crispy.util.render;

import arithmo.gui.altmanager.Colors;
import crispy.fonts.decentfont.FontUtil;
import crispy.util.render.gui.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class ExitButton extends GuiButton {
    public ExitButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {

    }
}
