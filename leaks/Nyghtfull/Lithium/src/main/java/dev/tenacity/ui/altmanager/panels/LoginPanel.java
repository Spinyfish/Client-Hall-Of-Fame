package dev.tenacity.ui.altmanager.panels;

import dev.tenacity.Tenacity;
import dev.tenacity.ui.altmanager.Panel;
import dev.tenacity.ui.altmanager.helpers.Alt;
import dev.tenacity.ui.altmanager.helpers.AltManagerUtils;
import dev.tenacity.ui.sidegui.utils.ActionButton;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.animations.Direction;
import dev.tenacity.utils.animations.impl.DecelerateAnimation;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.objects.TextField;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RenderUtil;
import dev.tenacity.utils.render.RoundedUtil;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoginPanel extends Panel {

    private final List<ActionButton> actionButtons = new ArrayList<>();
    public final List<TextField> textFields = new ArrayList<>();

    public LoginPanel() {
        setHeight(200);
        actionButtons.add(new ActionButton("Login"));
        actionButtons.add(new ActionButton("Add"));
        actionButtons.add(new ActionButton("Gen Cracked"));
        textFields.add(new TextField(lithiumFont20));
        textFields.add(new TextField(lithiumFont20));
    }


    @Override
    public void initGui() {

    }

    public static boolean cracked = false;


    @Override
    public void keyTyped(char typedChar, int keyCode) {
        textFields.forEach(textField -> textField.keyTyped(typedChar, keyCode));
        if (keyCode == Keyboard.KEY_TAB) {
            TextField username = textFields.get(0);
            TextField pass = textFields.get(1);
            if (username.isFocused()) {
                username.setFocused(false);
                pass.setFocused(true);
                return;
            }
            if (pass.isFocused()) {
                pass.setFocused(false);
                username.setFocused(true);
            }
        }

    }

    private boolean hoveringMicrosoft = false;
    private final Animation hoverMicrosoftAnim = new DecelerateAnimation(250, 1);

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        super.drawScreen(mouseX, mouseY);
        setHeight(180);
        lithiumBoldFont32.drawCenteredString("Login", getX() + getWidth() / 2f, getY() + 3, ColorUtil.applyOpacity(-1, .75f));
        Color noColor = ColorUtil.applyOpacity(Color.WHITE, 0);

        int count = 0;
        int spacing = 8;
        float diff = 35;
        for (TextField textField : textFields) {
            textField.setXPosition(getX() + (diff / 2f));
            textField.setYPosition(getY() + 35 + count);
            textField.setWidth(getWidth() - diff);
            textField.setHeight(22);
            textField.setBackgroundText(count == 0 ? "Email or combo" : "Password");
            textField.setOutline(noColor);
            textField.setFill(ColorUtil.tripleColor(17));
            textField.setTextAlpha(.35f);
            textField.setMaxStringLength(60);
            textField.drawTextBox();

            count += textField.getHeight() + spacing;
        }

        float actionY = getY() + 98;
        float actionWidth = 90;
        float buttonSpacing = 10;
        float firstX = getX() + getWidth() / 2f - ((actionButtons.size() * actionWidth) + 20) / 2f;
        int seperation = 0;
        for (ActionButton actionButton : actionButtons) {
            actionButton.setBypass(true);
            actionButton.setColor(ColorUtil.tripleColor(55));
            actionButton.setAlpha(1);
            actionButton.setX(firstX + seperation);
            actionButton.setY(actionY);
            actionButton.setWidth(actionWidth);
            actionButton.setHeight(20);
            actionButton.setFont(lithiumBoldFont22);

            actionButton.setClickAction(() -> {
                switch (actionButton.getName()) {
                    case "Login":
                        Tenacity.INSTANCE.getAltManager().getUtils().login(textFields.get(0), textFields.get(1));
                        resetTextFields();
                        Tenacity.INSTANCE.getAltManager().getAltPanel().refreshAlts();
                        break;
                    case "Add":
                        TextField username = textFields.get(0);
                        String email = username.getText();
                        String password = textFields.get(1).getText();

                        if (email.contains(":")) {
                            String[] split = email.split(":");
                            if (split.length != 2) return;
                            email = split[0];
                            password = split[1];
                        }

                        Alt alt = new Alt(email, password);
                        resetTextFields();
                        AltManagerUtils.getAlts().add(alt);
                        Tenacity.INSTANCE.getAltManager().getAltPanel().refreshAlts();
                        break;
                    case "Gen Cracked":
                        Tenacity.INSTANCE.getAltManager().getAltPanel().refreshAlts();
                        break;
                }
            });


            actionButton.drawScreen(mouseX, mouseY);

            seperation += actionWidth + buttonSpacing;
        }


        float microsoftY = actionY + 35, microWidth = 240, microHeight = 35;
        float microX = getX() + getWidth() / 2f - microWidth / 2f;

        hoveringMicrosoft = HoveringUtil.isHovering(microX - 2, microsoftY - 2, microWidth + 4, microHeight + 4, mouseX, mouseY);
        hoverMicrosoftAnim.setDirection(hoveringMicrosoft ? Direction.FORWARDS : Direction.BACKWARDS);
        mc.getTextureManager().bindTexture(new ResourceLocation("Tenacity/mc.png"));
        RoundedUtil.drawRoundTextured(microX, microsoftY, microWidth, microHeight, 5, 1);

        RoundedUtil.drawRound(microX, microsoftY, microWidth, microHeight, 5,
                ColorUtil.applyOpacity(Color.BLACK, .2f + (.25f * hoverMicrosoftAnim.getOutput().floatValue())));


        lithiumBoldFont26.drawString("Microsoft Login", microX + 10, microsoftY + 4, -1);

        lithiumFont16.drawString("Login to your migrated account", microX + 10, microsoftY + 23, -1);

        float logoSize = 22;
        RenderUtil.drawMicrosoftLogo(microX + microWidth - (10 + logoSize), microsoftY + (microHeight / 2f) - (logoSize / 2f), logoSize, 1.5f);

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        textFields.forEach(textField -> textField.mouseClicked(mouseX, mouseY, button));
        actionButtons.forEach(actionButton -> actionButton.mouseClicked(mouseX, mouseY, button));

        if (hoveringMicrosoft && button == 0) {
            TextField username = textFields.get(0);
            String email = username.getText();
            String password = textFields.get(1).getText();
            if (email.contains(":")) {
                String[] split = email.split(":");
                if (split.length != 2) return;
                email = split[0];
                password = split[1];
            }

            Tenacity.INSTANCE.getAltManager().getUtils().microsoftLoginAsync(email, password);
            resetTextFields();
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    private void resetTextFields() {
        textFields.forEach(textField -> textField.setText(""));
    }
}
