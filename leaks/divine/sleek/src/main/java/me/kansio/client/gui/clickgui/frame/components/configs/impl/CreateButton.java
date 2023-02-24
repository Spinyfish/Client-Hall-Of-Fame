package me.kansio.client.gui.clickgui.frame.components.configs.impl;

import me.kansio.client.Client;
import me.kansio.client.gui.clickgui.frame.Values;
import me.kansio.client.gui.clickgui.frame.components.configs.ConfigComponent;
import me.kansio.client.gui.clickgui.frame.components.configs.FrameConfig;
import me.kansio.client.modules.impl.visuals.ClickGUI;
import me.kansio.client.gui.notification.Notification;
import me.kansio.client.gui.notification.NotificationManager;
import me.kansio.client.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

public class CreateButton extends ConfigComponent implements Values {

    private FrameConfig frame;
    private String tempName;

    public CreateButton(int x, int y, FrameConfig owner) {
        super(x, y, owner);

        this.frame = owner;
        tempName = "";
    }

    private boolean typing;

    @Override
    public void initGui() {
        typing = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

        fontRenderer.drawString("» Create: " + tempName, x + 5, y + (getOffset() / 2F - (fontRenderer.FONT_HEIGHT / 2F)), stringColor, true);

        if (typing) {
            Gui.drawRect(x + 125, y + (getOffset() / 2F - (fontRenderer.FONT_HEIGHT / 2F)) + 9, defaultWidth - 8, y + (getOffset() / 2F - (fontRenderer.FONT_HEIGHT / 2F)) + 7 + 4, -1);
        }
    }


    private double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtils.hover(x, y, mouseX, mouseY, defaultWidth, getOffset()) && mouseButton == 0) {
            typing = !typing;
        }
        return RenderUtils.hover(x, y, mouseX, mouseY, defaultWidth, getOffset()) && mouseButton == 0;
    }

    @Override
    public void onGuiClosed(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_RSHIFT
                || keyCode == Keyboard.KEY_LSHIFT
                || keyCode == Keyboard.KEY_CAPITAL)
            return;


        //enter
        if (keyCode == 28) {
            typing = false;

            //Don't allow empty config names...
            if (tempName.equalsIgnoreCase("")) return;

            //create config
            Client.getInstance().getConfigManager().saveConfig(tempName);
            NotificationManager.getNotificationManager().show(new Notification(Notification.NotificationType.INFO, "Configs", "Successfully created config", 1));

            ClickGUI clickGUI = (ClickGUI) Client.getInstance().getModuleManager().getModuleByName("Click GUI");

            if (Minecraft.getMinecraft().currentScreen != null) {
                Minecraft.getMinecraft().thePlayer.closeScreen();

                clickGUI.toggle();
            }
            return;
        }


        //backspace
        if (keyCode == 14 && typing) {
            if (tempName.toCharArray().length == 0) {
                return;
            }
            tempName = removeLastChar(tempName);
            return;
        }

        List<Character> whitelistedChars = Arrays.asList(
                '&', ' ', '[', ']', '(', ')',
                '.', ',', '<', '>', '-', '$',
                '!', '"', '\'', '\\', '/', '=',
                '+', ',', '|', '^', '?', '`', ';', ':',
                '@', '£', '%', '{', '}', '_', '*', '»'
        );

        for (char whitelistedChar : whitelistedChars) {
            if (typedChar == whitelistedChar && typing) {
                tempName = tempName + typedChar;
                return;
            }
        }

        if (!Character.isLetterOrDigit(typedChar)) {
            return;
        }


        if (typing) {
            tempName = tempName + typedChar;
        }
    }

    private String removeLastChar(String s) {
        return s.substring(0, s.length() - 1);
    }

    @Override
    public int getOffset() {
        return 15;
    }
}
