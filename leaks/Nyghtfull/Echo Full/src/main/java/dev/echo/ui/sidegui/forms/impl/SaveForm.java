package dev.echo.ui.sidegui.forms.impl;

import dev.echo.Echo;
import dev.echo.ui.sidegui.forms.Form;
import dev.echo.ui.sidegui.utils.ActionButton;
import dev.echo.utils.objects.TextField;
import dev.echo.utils.render.ColorUtil;
import dev.echo.utils.render.RoundedUtil;

import java.awt.*;

public class SaveForm extends Form {

    private final ActionButton save = new ActionButton("Save");

    private final TextField nameField = new TextField(echoFont20);

    public SaveForm() {
        super("Save Config");
        setWidth(300);
        setHeight(120);
    }


    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        nameField.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);

        float infoX = getX() + echoBoldFont40.getStringWidth(getTitle()) + 20;
        float infoY = getY() + 3 + echoFont16.getMiddleOfBox(echoBoldFont40.getHeight()) + 2;

        echoFont16.drawString("Enter a name for the local config",
                infoX, infoY, ColorUtil.applyOpacity(getTextColor(), .5f));

        float insideWidth = getWidth() - (getSpacing() * 2);
        float insideX = getX() + getSpacing();
        float insideY = getY() + 30;

        RoundedUtil.drawRound(insideX, insideY, getWidth() - (getSpacing() * 2),
                getHeight() - (30 + getSpacing()), 5, ColorUtil.tripleColor(29, getAlpha()));



        Color noColor = ColorUtil.applyOpacity(Color.WHITE, 0);
        Color darkColor = ColorUtil.tripleColor(17, getAlpha());

        nameField.setBackgroundText("Type here...");
        nameField.setXPosition(insideX + getSpacing());
        nameField.setYPosition(insideY + 25);
        nameField.setWidth(insideWidth - (getSpacing() * 2));
        nameField.setHeight(20);
        nameField.setFont(echoFont18);
        nameField.setOutline(noColor);
        nameField.setFill(darkColor);
        nameField.setTextAlpha(getAlpha());

        int maxStringLength = echoBoldFont26.getStringWidth(nameField.getText()) >= 143 ? nameField.getText().length() : 30;
        nameField.setMaxStringLength(maxStringLength);
        nameField.drawTextBox();

        echoFont24.drawString("Config name", nameField.getXPosition(),
                nameField.getYPosition() - (echoFont24.getHeight() + 5), getTextColor());


        save.setWidth(70);
        save.setHeight(15);
        save.setX(getX() + getWidth() / 2f - save.getWidth() / 2f);
        save.setY(getY() + getHeight() - (save.getHeight() + (getSpacing() * 2)));
        save.setAlpha(getAlpha());
        save.setBypass(true);
        save.setBold(true);
        save.setClickAction(() -> {
            getUploadAction().accept(nameField.getText(), null);
            Echo.INSTANCE.getSideGui().displayForm(null);
        });
        save.drawScreen(mouseX, mouseY);

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        nameField.mouseClicked(mouseX, mouseY, button);
        save.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void clear() {
        nameField.setText("");
    }


    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

}
