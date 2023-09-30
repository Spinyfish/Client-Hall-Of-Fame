package de.fanta.clickgui.defaultgui.components;

import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.module.Module;
import de.fanta.utils.Colors;
import de.fanta.utils.RenderUtil;

public class ModuleButton {
    private float x;
    private float y;
    public boolean isOpened;
    public Module module;
    private CategoryPanel panel;
    private int ani = 0;

    public ModuleButton(Module module, CategoryPanel panel, float x, float y) {
        this.module = module;
        this.x = x;
        this.y = y;
        this.panel = panel;
    }

    public void drawButton(float mouseX, float mouseY) {
        float xOff = panel.cateButton.panel.dragX;
        float yOff = panel.cateButton.panel.dragY;

        float moduleNameLength = Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(module.name);

        boolean hovering = mouseX >= x + xOff + panel.lengthModule + 5 && mouseY >= y + yOff && mouseX <= x + xOff + panel.lengthModule + 15 && mouseY <= y + yOff + 10;
        boolean hoveringText = mouseX >= x + xOff && mouseY >= y + yOff && mouseX <= x + xOff + moduleNameLength && mouseY <= y + yOff + 10;

        int color = hovering ? Colors.getColor(88, 88, 88, 255) : Colors.getColor(66, 66, 66, 255);

        GL11.glPushMatrix();
        if(module.state) {
            ani += 5;
            color = Colors.getColor(200, 50, 0, 255 + ani);
        }else{
            color = Colors.getColor(41, 128, 185, 5 + ani);
            ani -= 5;
        }

        if (ani >= 255) {
            ani = 255;
        }

        if (ani < 0) {
            ani = 0;
             color = hovering ? Colors.getColor(88, 88, 88, 255) : Colors.getColor(66, 66, 66, 255);
        }

        GL11.glColor4f(0.0F,0.0F,0.0F,100.0F);
        GL11.glPopMatrix();

        RenderUtil.rectangle(x + xOff + panel.lengthModule + 5, y + yOff, x + xOff + panel.lengthModule + 15, y + yOff + 10, color);

        int colorText = hoveringText ? Colors.getColor(189, 195, 199, 255) : Colors.getColor(127, 140, 141, 255);
        if(isOpened) colorText = Colors.getColor(255, 255, 255, 255);
        Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(module.name, (int) (x + xOff), (int) (y + yOff) - 2, colorText);
    }

    public void buttonClicked(float mouseX, float mouseY, int mouseButton) {
        float xOff = panel.cateButton.panel.dragX;
        float yOff = panel.cateButton.panel.dragY;

        float moduleNameLength =   Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(module.name);

        boolean hovering = mouseX >= x + xOff + panel.lengthModule + 5 && mouseY >= y + yOff && mouseX <= x + xOff + panel.lengthModule + 15 && mouseY <= y + yOff + 10;
        boolean hoveringText = mouseX >= x + xOff && mouseY >= y + yOff && mouseX <= x + xOff + moduleNameLength && mouseY <= y + yOff + 10;

        if(hovering) module.setState();

        if(hoveringText && !module.settings.isEmpty()) {
            isOpened = true;

            panel.moduleButtons.forEach(moduleButton -> {
                if(moduleButton != this && moduleButton.isOpened)
                    moduleButton.isOpened = !isOpened;
            });
        }
    }
}
