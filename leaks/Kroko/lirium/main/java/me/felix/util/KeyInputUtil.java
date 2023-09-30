package me.felix.util;


import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

public class KeyInputUtil {

    public String typedString = "";
    public boolean typing, allSelected;

    public String getInput() {
        return typedString;
    }

    public void registerInput(char typedChar, int keyCode) {
        if (typing) {
            if (GuiScreen.isKeyComboCtrlA(keyCode) && !typedString.isEmpty()) {
                this.allSelected = true;
            }
            if (allSelected) {
                if ((keyCode == Keyboard.KEY_DELETE || keyCode == 14) && !this.typedString.isEmpty()) {
                    typedString = "";
                    allSelected = false;
                }
            } else if ((keyCode == Keyboard.KEY_DELETE || keyCode == 14) && !this.typedString.isEmpty()) {
                typedString = this.typedString.substring(0, this.typedString.length() - 1);
            } else if (keyCode == Keyboard.KEY_RETURN) {
                typing = false;

            } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar) && (typedString.length() < 20)) {
                typedString = this.typedString + typedChar;
            } else if (GuiScreen.isKeyComboCtrlV(keyCode)) {
                this.typedString = this.typedString + GuiScreen.getClipboardString();
            }

        }
    }
}
