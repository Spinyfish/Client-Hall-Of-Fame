package igbt.astolfy.module.visuals;

import igbt.astolfy.Astolfy;
import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventKey;
import igbt.astolfy.events.listeners.EventRender2D;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.ui.inGame.GuiElement.GuiElement;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

public class TabGUI extends ModuleBase {
    public TabGUI() {
        super("TabGUI", 0, Category.VISUALS);
    }
    public int index = 0;
    public int indexExp = 0;
    public GuiElement guiElement = new GuiElement("TabGUI", this, 5,15, 150,150);
    public Boolean expanded = false;

    public void onEvent(Event event ) {
        if(event instanceof EventRender2D) {
            Astolfy.moduleManager.sortModules();
            guiElement.renderStart();
            int width = 70;
            int cWidth = 70;
            int cHeight = 0;
            int mHeight = 0;
            int count = 0;
            for (Category c : Category.values()) {
                Gui.drawRect(0, (count * (mc.customFont.getHeight() + 5)), width, (count * (mc.customFont.getHeight() + 5) + 14), 0x80000000);
                if(index == count)
                    Gui.drawRect(0, (count * (mc.customFont.getHeight() + 5)), width, (count * (mc.customFont.getHeight() + 5) + 14), Hud.getColor(0));
                mc.customFont.drawString(c.name,(index == count) ? 3 : 2, (count * (mc.customFont.getHeight() + 5)) + (2), -1);
                cHeight += (mc.customFont.getHeight() + 5);
                count++;
            }
            if(expanded){
                width += width;
                width += 5;
                count = 0;
                for (ModuleBase c : Astolfy.moduleManager.getModulesByCategory(Category.values()[index])) {
                    Gui.drawRect(cWidth + 1, (count * (mc.customFont.getHeight() + 5)), width, (count * (mc.customFont.getHeight() + 5) + 14), 0x80000000);
                    if(indexExp == count)
                        Gui.drawRect(cWidth + 1, (count * (mc.customFont.getHeight() + 5)), width, (count * (mc.customFont.getHeight() + 5) + 14), Hud.getColor(0));
                    mc.customFont.drawString(c.getName(),((indexExp == count) ? 3 : 2) + cWidth + 1, (count * (mc.customFont.getHeight() + 5)) + (2), -1);
                    mHeight += (mc.customFont.getHeight() + 5);
                    count++;
                }
            }
            guiElement.renderEnd();
            guiElement.setWidth(width);
            guiElement.setHeight(Math.max(cHeight, mHeight));
        }
        if(event instanceof EventKey){
            Astolfy.moduleManager.sortModules();
            EventKey key = (EventKey)event;
            switch (key.code){
                case(Keyboard.KEY_RIGHT):
                    if(!expanded){
                        expanded = true;
                    }else {
                        Astolfy.moduleManager.getModulesByCategory(Category.values()[index]).get(indexExp).toggle();
                    }
                break;
                case(Keyboard.KEY_LEFT):
                    if(expanded){
                        expanded = false;
                    }
                    break;
                case(208):
                    if(!expanded) {
                        if (Category.values().length > index + 1)
                            index++;
                        else
                            index = 0;
                    }else{
                        if (Astolfy.moduleManager.getModulesByCategory(Category.values()[index]).size() > indexExp + 1)
                            indexExp++;
                        else
                            indexExp = 0;
                    }
                    break;
                case(200):
                    if(!expanded)
                        if(index > 0){
                            index--;
                        }else
                            index = Category.values().length - 1;
                    else
                        if(indexExp > 0){
                            indexExp--;
                        }else
                            indexExp = Astolfy.moduleManager.getModulesByCategory(Category.values()[indexExp]).size() - 1;
                        break;
            }
        }
    }
}
