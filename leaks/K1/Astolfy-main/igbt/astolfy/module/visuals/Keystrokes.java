package igbt.astolfy.module.visuals;

import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventRender2D;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.ui.inGame.GuiElement.GuiElement;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

public class Keystrokes extends ModuleBase {

    public GuiElement guiElement = new GuiElement("Keystrokes",this,5,60, 100,65);

    public Keystrokes() {
        super("Keystrokes", 0, Category.VISUALS);
    }

    public void onEvent(Event e){
        if(e instanceof EventRender2D){
            guiElement.renderStart();
            Gui.drawRect(35, 0, 30 + 5 + 30, 30, mc.gameSettings.keyBindForward.pressed ? 0x80FFFFFF : 0x80000000);
            drawCenteredText(Keyboard.getKeyName(mc.gameSettings.keyBindForward.getKeyCode()), 35,0, Hud.getColor(0));
            Gui.drawRect(0, 35, 30, 35 + 30, mc.gameSettings.keyBindLeft.pressed ? 0x80FFFFFF : 0x80000000);
            drawCenteredText(Keyboard.getKeyName(mc.gameSettings.keyBindLeft.getKeyCode()), 0,35, Hud.getColor(35));
            Gui.drawRect(35, 35, 35 + 30, 35 + 30, mc.gameSettings.keyBindBack.pressed ? 0x80FFFFFF : 0x80000000);
            drawCenteredText(Keyboard.getKeyName(mc.gameSettings.keyBindBack.getKeyCode()), 35,35, Hud.getColor(35));
            Gui.drawRect(35 + 35, 35, 35 + 35 + 30, 35 + 30, mc.gameSettings.keyBindRight.pressed ? 0x80FFFFFF : 0x80000000);
            drawCenteredText(Keyboard.getKeyName(mc.gameSettings.keyBindRight.getKeyCode()), 35 + 35,35, Hud.getColor(35));
            guiElement.renderEnd();
        }
    }

    public void drawCenteredText(String txt, int x, int y, int col){
        mc.customFont.drawString(txt, x + (30 / 2) - (mc.customFont.getStringWidth(txt) / 2), y + (30 / 2) - (mc.customFont.getHeight() / 2),col);
    }

}
