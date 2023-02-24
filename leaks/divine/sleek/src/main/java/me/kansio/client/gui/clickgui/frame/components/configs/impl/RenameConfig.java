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
import org.apache.commons.lang3.RandomStringUtils;
import org.lwjgl.input.Keyboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

public class RenameConfig extends ConfigComponent implements Values {

    private FrameConfig frame;
    private String tempName;

    public RenameConfig(int x, int y, FrameConfig owner) {
        super(x, y, owner);

        this.frame = owner;
        tempName = frame.config.getName();
    }

    private boolean typing;

    @Override
    public void initGui() {
        typing = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

        fontRenderer.drawString("Change name: " + tempName, x + 5, y + (getOffset() / 2F - (fontRenderer.FONT_HEIGHT / 2F)), stringColor, true);

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

            //create a temporary config to store current config
            String currentTempConfig = RandomStringUtils.randomAlphanumeric(12);
            Client.getInstance().getConfigManager().saveConfig(currentTempConfig);

            //load the config they're trying to rename
            Client.getInstance().getConfigManager().loadConfig(frame.config.getName(), false);

            //create a new config with the new name
            Client.getInstance().getConfigManager().saveConfig(tempName);

            //Load the previous config
            Client.getInstance().getConfigManager().loadConfig(currentTempConfig, false);

            //Delete the temporary config and old config
            Client.getInstance().getConfigManager().removeConfig(frame.config.getName());
            Client.getInstance().getConfigManager().removeConfig(currentTempConfig);

            //Reload the configs
            Client.getInstance().getConfigManager().loadConfigs();

            NotificationManager.getNotificationManager().show(new Notification(Notification.NotificationType.INFO, "Configs", "Successfully renamed config", 1));

            //close the click gui
            Minecraft.getMinecraft().thePlayer.closeScreen();

            //retoggle it
            ClickGUI cgui = (ClickGUI) Client.getInstance().getModuleManager().getModuleByName("Click GUI");
            cgui.toggle();
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
