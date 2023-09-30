package igbt.astolfy.ui.inGame.GuiElement;

import net.minecraft.client.gui.Gui;

import java.util.concurrent.CopyOnWriteArrayList;

public class GuiElementManager {

    private static CopyOnWriteArrayList<GuiElement> guiElements = new CopyOnWriteArrayList<>();

    public static CopyOnWriteArrayList<GuiElement> getElements(){
        return guiElements;
    }

    public static void addElement(GuiElement guiElement) {
        guiElements.add(0,guiElement);
    }
}
