package dev.echo.ui.sidegui.forms.impl;

import dev.echo.Echo;
import dev.echo.ui.sidegui.forms.Form;
import dev.echo.ui.sidegui.utils.ActionButton;
import dev.echo.utils.misc.HoveringUtil;
import dev.echo.utils.objects.TextField;
import dev.echo.utils.render.ColorUtil;
import dev.echo.utils.render.RoundedUtil;

import java.awt.*;

public class UploadForm extends Form {

    private final ActionButton uploadButton = new ActionButton("Upload");

    private final String type;

    private final TextField nameField = new TextField(echoFont18);
    private final TextField descriptionField = new TextField(echoFont18);
    private final TextField serverField = new TextField(echoFont18);

    public UploadForm(String type) {
        super("Upload " + type);
        setWidth(375);
        setHeight(175);
        this.type = type;
    }


    @Override
    public void initGui() {

    }

    private boolean error = false;

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        nameField.keyTyped(typedChar, keyCode);
        descriptionField.keyTyped(typedChar, keyCode);
        if (type.equals("Config")) {
            serverField.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);

        float infoX = getX() + echoBoldFont40.getStringWidth(getTitle()) + 20;
        float infoY = getY() + 7.5f;

        echoFont16.drawString("To upload your " + type.toLowerCase() + " you must provide some information",
                infoX, infoY, ColorUtil.applyOpacity(getTextColor(), .5f));

        infoY += echoFont16.getHeight() + 3;

        echoFont16.drawString("All fields are §lrequired", infoX, infoY, ColorUtil.applyOpacity(getTextColor(), .5f));

        float insideWidth = getWidth() - (getSpacing() * 2);
        RoundedUtil.drawRound(getX() + getSpacing(), getY() + 40, getWidth() - (getSpacing() * 2),
                getHeight() - (40 + getSpacing()), 5, ColorUtil.tripleColor(29, getAlpha()));

        float insideX = getX() + getSpacing();
        float insideY = getY() + 40;


        Color noColor = ColorUtil.applyOpacity(Color.WHITE, 0);
        Color darkColor = ColorUtil.tripleColor(17, getAlpha());

        nameField.setBackgroundText("Type here...");
        nameField.setXPosition(insideX + getSpacing());
        nameField.setYPosition(insideY + 25);
        nameField.setWidth(150);
        nameField.setHeight(20);
        nameField.setOutline(noColor);
        nameField.setFill(darkColor);
        nameField.setTextAlpha(getAlpha());

        int maxStringLength = echoBoldFont26.getStringWidth(nameField.getText()) >= 143 ? nameField.getText().length() : 30;
        nameField.setMaxStringLength(maxStringLength);
        nameField.drawTextBox();

        echoFont24.drawString(type + " name", nameField.getXPosition(),
                nameField.getYPosition() - (echoFont24.getHeight() + 5), getTextColor());


        if (type.equals("Config")) {

            serverField.setBackgroundText("Type here...");
            serverField.setYPosition(nameField.getYPosition());
            serverField.setWidth(150);
            serverField.setXPosition(insideX + insideWidth - (serverField.getWidth() + getSpacing() * 2));
            serverField.setHeight(20);
            serverField.setOutline(noColor);
            serverField.setFill(darkColor);
            serverField.setTextAlpha(getAlpha());
            serverField.drawTextBox();

            echoFont24.drawString("Server IP", serverField.getXPosition(),
                    serverField.getYPosition() - (echoFont24.getHeight() + 5), getTextColor());

            echoFont14.drawCenteredString("Input the IP of the server the config was made for",
                    serverField.getXPosition() + serverField.getWidth() / 2f + 2,
                    serverField.getYPosition() + serverField.getHeight() + 5, getTextColor());

        }


        descriptionField.setBackgroundText("Type here...");
        descriptionField.setXPosition(insideX + getSpacing());
        descriptionField.setYPosition((nameField.getYPosition() + nameField.getHeight()) + 25);
        descriptionField.setWidth(insideWidth - (getSpacing() * 2));
        descriptionField.setHeight(20);
        descriptionField.setOutline(noColor);
        descriptionField.setFill(darkColor);
        descriptionField.setTextAlpha(getAlpha());
        descriptionField.setMaxStringLength(210);
        descriptionField.drawTextBox();

        echoFont24.drawString("Description", descriptionField.getXPosition(),
                descriptionField.getYPosition() - (echoFont24.getHeight() + 5), getTextColor());


        uploadButton.setWidth(70);
        uploadButton.setHeight(15);
        uploadButton.setX(getX() + getWidth() / 2f - uploadButton.getWidth() / 2f);
        uploadButton.setY(getY() + getHeight() - (uploadButton.getHeight() + (getSpacing() * 2)));
        uploadButton.setAlpha(getAlpha());
        uploadButton.setBypass(true);
        uploadButton.setBold(true);
        uploadButton.setClickAction(() -> {
            if (type.equals("Config")) {
                getTriUploadAction().accept(nameField.getText(), descriptionField.getText(), serverField.getText());
            } else {
                getUploadAction().accept(nameField.getText(), descriptionField.getText());
            }
            Echo.INSTANCE.getSideGui().displayForm(null);
        });
        uploadButton.drawScreen(mouseX, mouseY);


        if (error) {
            echoFont16.drawCenteredStringWithShadow("Error please fill out the required fields",
                    uploadButton.getX() + uploadButton.getWidth() / 2f, uploadButton.getY() - (echoFont16.getHeight() + 5),
                    Echo.INSTANCE.getSideGui().getRedBadColor().getRGB());
        }

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        nameField.mouseClicked(mouseX, mouseY, button);
        descriptionField.mouseClicked(mouseX, mouseY, button);
        if (type.equals("Config")) {
            serverField.mouseClicked(mouseX, mouseY, button);
        }

        if (HoveringUtil.isHovering(uploadButton.getX(), uploadButton.getY(), uploadButton.getWidth(), uploadButton.getHeight(), mouseX, mouseY)) {
            String descriptionText = descriptionField.getText();
            String[] descArray = descriptionText.split(" ");
            boolean descriptionFilter = descriptionText.length() > 35 && !(descArray.length > 1);
            if ((type.equals("Config") && serverField.getText().isEmpty()) || nameField.getText().isEmpty() || descriptionField.getText().isEmpty() || descriptionFilter) {
                error = true;
            } else {
                error = false;
                uploadButton.mouseClicked(mouseX, mouseY, button);
            }
        }

    }

    @Override
    public void clear() {
        error = false;
        nameField.setText("");
        descriptionField.setText("");
        serverField.setText("");
    }


    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

}
