package alos.stella.ui.clickgui.astolfo.buttons;

import alos.stella.Stella;
import alos.stella.module.Module;
import alos.stella.module.ModuleCategory;
import alos.stella.ui.client.font.Fonts;
import alos.stella.utils.render.DrawUtils;

import java.awt.*;
import java.util.ArrayList;

public class AstolfoCategoryPanel extends AstolfoButton {

    public ModuleCategory category;
    public Color color;

    public boolean dragged, open;
    public int mouseX2, mouseY2;

    public float count;

    public ArrayList<AstolfoModuleButton> moduleButtons = new ArrayList<>();

    public AstolfoCategoryPanel(float x, float y, float width, float height, ModuleCategory cat, Color color) {
        super(x, y, width, height);
        category = cat;
        this.color = color;

        int count = 0;

        final float startY = y + height;

        for(Module mod : Stella.moduleManager.getModules()) {
            if(mod.getCategory() == category) {
                moduleButtons.add(new AstolfoModuleButton(x, startY + height*count, width, height, mod, color));
                count++;
            }
        }
    }

    @Override
    public void drawPanel(int mouseX, int mouseY) {
        if(dragged) {
            x = mouseX2 + mouseX;
            y = mouseY2 + mouseY;
        }


        DrawUtils.drawRect(x, y + 6, x + width, y + height, 0xff181A17);
        Fonts.fontSFUI35.drawStringWithShadow(String.valueOf(category).toLowerCase(), x + 4, y + height/2, 0xffffffff);

        count = 0;

        if(open) {

            final float startY = y + height;

            for (AstolfoModuleButton modulePanel : moduleButtons) {
                modulePanel.x = x;
                modulePanel.y = startY + count;
                modulePanel.drawPanel(mouseX, mouseY);
                count += modulePanel.finalHeight;
            }
        }

        DrawUtils.drawRect(x, (y + count) + height, x + width, (y + count) + height + 2, 0xff181A17);

        DrawUtils.drawBorder(x, y + 6, x + width, (y + count) + height + 2, 2, color.getRGB());
    }

    @Override
    public void mouseAction(int mouseX, int mouseY, boolean click, int button) {
        if(isHovered(x, y + 6, x + width, y + height,mouseX, mouseY)) {
            if(click) {
                if(button == 0) {
                    dragged = true;
                    mouseX2 = (int) (x - mouseX);
                    mouseY2 = (int) (y - mouseY);
                } else {
                    open = !open;
                }
            }
        }

        if(!click) dragged = false;
    }
}
