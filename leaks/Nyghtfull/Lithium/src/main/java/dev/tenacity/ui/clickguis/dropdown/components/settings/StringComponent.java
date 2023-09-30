package dev.tenacity.ui.clickguis.dropdown.components.settings;

import dev.tenacity.module.settings.impl.StringSetting;
import dev.tenacity.ui.clickguis.dropdown.components.SettingComponent;
import dev.tenacity.utils.objects.TextField;

public class StringComponent extends SettingComponent<StringSetting> {

    private final TextField textField = new TextField(lithiumFont16);

    public StringComponent(StringSetting setting) {
        super(setting);
    }


    boolean setDefaultText = false;
    @Override
    public void initGui() {
        setDefaultText = false;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        textField.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        float boxX = x + 6;
        float boxY = y + 12;
        float boxWidth = width - 12;
        float boxHeight = height - 16;

        if(!setDefaultText){
            textField.setText(getSetting().getString());
            textField.setCursorPositionZero();
            setDefaultText = true;
        }



        getSetting().setString(textField.getText());


        textField.setBackgroundText("Type here...");

        lithiumFont14.drawString(getSetting().name, boxX, y + 3, textColor);

        textField.setXPosition(boxX);
        textField.setYPosition(boxY);
        textField.setWidth(boxWidth);
        textField.setHeight(boxHeight);
        textField.setOutline(settingRectColor.brighter().brighter().brighter());
        textField.setFill(settingRectColor.brighter());

        textField.drawTextBox();


        if(!typing) {
            typing = textField.isFocused();
        }


        countSize = 2f;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        textField.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
