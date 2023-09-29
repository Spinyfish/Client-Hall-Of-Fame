package alos.stella.ui.clickgui.astolfo.buttons;


import alos.stella.module.Module;
import alos.stella.module.modules.visual.ClickGUI;
import alos.stella.ui.client.font.Fonts;
import alos.stella.utils.render.DrawUtils;
import alos.stella.value.*;

import java.awt.*;
import java.util.ArrayList;

public class AstolfoModuleButton extends AstolfoButton {
    public Module module;
    public Color color;

    public boolean extended;

    public float finalHeight;

    public ArrayList<AstolfoButton> astolfoButtons = new ArrayList<>();

    public AstolfoModuleButton(float x, float y, float width, float height, Module mod, Color col) {
        super(x, y, width, height);

        module = mod;

        color = col;

        final float startY = y + height;

        int count = 0;

        for(Value set : module.getValues()) {
            if (!((boolean)set.getCanDisplay().invoke()))
                continue;
            if(set instanceof BoolValue) astolfoButtons.add(new AstolfoBooleanButton(x, startY + 18*count, width, 9, (BoolValue)set, color));
            if(set instanceof ListValue) astolfoButtons.add(new AstolfoModeButton(x, startY + 18 * count, width, 9, (ListValue) set, color) {

                @Override
                public void mouseAction(int mouseX, int mouseY, boolean click, int button) {

                }
            });
            if(set instanceof IntegerValue) astolfoButtons.add(new AstolfoNumberButton(x, startY + 18*count, width, 9, (IntegerValue)set, color));
            if(set instanceof FloatValue) astolfoButtons.add(new AstolfoFloatNumberButton(x, startY + 18*count, width, 9, (FloatValue) set, color));
            count++;
        }
    }

    @Override
    public void drawPanel(int mouseX, int mouseY) {
        DrawUtils.drawRect(x, y, x + width, y + height - 6, 0xff181A17);

        if(!extended) {
            DrawUtils.drawRect(x + 2, y, x + width - 2, y + height - 6, module.getState() ? color.getRGB() : 0xff232623);
        }else {
            DrawUtils.drawRect(x + 2 - 2, y, x + width - 2 + 2, y + finalHeight, 0xff181A17);
        }

        Fonts.fontSFUI35.drawStringWithShadow(module.getName(), (x + width) - Fonts.fontSFUI35.getStringWidth(module.getName()) - 3, y + height/2 - 6, extended ?  new Color(ClickGUI.colorRedValue.get(), ClickGUI.colorGreenValue.get(), ClickGUI.colorBlueValue.get()).getRGB() : 0xffffffff);

        int count = 0;

        float hehe = 0;

        if(extended) {
            final float startY = y + height;
            for(AstolfoButton pan : astolfoButtons) {
                pan.x = x;
                pan.y = startY + pan.height*count/2;
                pan.drawPanel(mouseX, mouseY);
                count += 2;

                hehe = pan.height - 4f;
            }
        }

        finalHeight = hehe * count + height - 6;
    }

    @Override
    public void mouseAction(int mouseX, int mouseY, boolean click, int button) {
        if(isHovered(x + 2, y, x + width - 2, y + height - 6,mouseX, mouseY) && click) {
            if(button == 0) {
                module.toggle();
            } else if(module.getValues().size() > 0) extended = !extended;
        }
    }
}
