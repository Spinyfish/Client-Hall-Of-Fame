package me.kansio.client.gui.config;

import me.kansio.client.Client;
import me.kansio.client.config.Config;
import me.kansio.client.gui.notification.Notification;
import me.kansio.client.gui.notification.NotificationManager;
import me.kansio.client.utils.font.Fonts;
import me.kansio.client.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigurationGUI extends GuiScreen {

    private final ArrayList<Config> listedConfigs = new ArrayList<>();
    private Config selectedConfig = null;

    private String name = "";
    private boolean typing = false;

    private int page = 0;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = RenderUtils.getResolution();

        //draw background
        RenderUtils.drawRoundedRect(sr.getScaledWidth() / 2 - 150, sr.getScaledHeight() / 2 - 100, 300, 220, 3, new Color(33, 33, 33, 255).getRGB());
        RenderUtils.drawRoundedRect(sr.getScaledWidth() / 2 - 148, sr.getScaledHeight() / 2 - 98, 296, 216, 3, new Color(38, 38, 38, 255).getRGB());
        //end background

        //draw background for configs box
        RenderUtils.drawRoundedRect(sr.getScaledWidth() / 2 - 140, sr.getScaledHeight() / 2 - 90, 135, 200, 3, new Color(45, 45, 45, 255).getRGB());

        //add configs to the menu
        int y = 5;
        for (Config cfg : listedConfigs) {
            RenderUtils.drawRoundedRect(sr.getScaledWidth() / 2 - 140, sr.getScaledHeight() / 2 - 92 + y, 135, 25, 3, (selectedConfig != null && selectedConfig == cfg) ? new Color(198, 96, 234).getRGB() : new Color(57, 57, 57, 255).getRGB());

            mc.fontRendererObj.drawString(cfg.getName().replace("(Verified)", "§a§l✔ §f"), sr.getScaledWidth() / 2 - 138, sr.getScaledHeight() / 2 - 90 + y, -1);

            Fonts.UbuntuLight.drawString("Created by: " + cfg.getAuthor(), sr.getScaledWidth() / 2 - 138, sr.getScaledHeight() / 2 - 78 + y, -1);
            Fonts.UbuntuLight.drawString("Updated: " + cfg.getLastUpdated(), sr.getScaledWidth() / 2 - 138, sr.getScaledHeight() / 2 - 72 + y, -1);
            y += 28;
        }

        //end configs box


        //draw background for config creation box
        RenderUtils.drawRoundedRect(sr.getScaledWidth() / 2 + 5, sr.getScaledHeight() / 2 - 90, 135, 75, 3, new Color(45, 45, 45, 255).getRGB());

        //search box
        RenderUtils.drawRoundedRect(sr.getScaledWidth() / 2 + 10, sr.getScaledHeight() / 2 - 80, 125, 16, 3, typing ? new Color(66, 66, 66, 255).getRGB() : new Color(56, 56, 56, 255).getRGB());
        if (name.equalsIgnoreCase("") && !typing) {
            Fonts.Arial12.drawString("Name...", sr.getScaledWidth() / 2 + 13, sr.getScaledHeight() / 2 - 73, new Color(90, 90, 90).getRGB());
        } else {
            Fonts.Arial12.drawString(name, sr.getScaledWidth() / 2 + 13, sr.getScaledHeight() / 2 - 73, new Color(255, 255, 255).getRGB());
        }

        //"Create" Button
        RenderUtils.drawRoundedRect(sr.getScaledWidth() / 2 + 10, sr.getScaledHeight() / 2 - 38, 50, 13, 3, new Color(215, 103, 194, 255).getRGB());
        Fonts.Arial12.drawString("Create config", sr.getScaledWidth() / 2 + 14, sr.getScaledHeight() / 2 - 33, -1);

        //end config creation box

        //draw background for config loading box
        RenderUtils.drawRoundedRect(sr.getScaledWidth() / 2 + 5, sr.getScaledHeight() / 2 + 35, 135, 75, 3, new Color(45, 45, 45, 255).getRGB());

        //page <-
        RenderUtils.drawRoundedRect(sr.getScaledWidth() / 2 + 10, sr.getScaledHeight() / 2 + 38, 14, 13, 3, new Color(215, 103, 194, 255).getRGB());
        Fonts.Arial12.drawString("<-", sr.getScaledWidth() / 2 + 14, sr.getScaledHeight() / 2 + 44, -1);

        //page ->
        RenderUtils.drawRoundedRect(sr.getScaledWidth() / 2 + 30, sr.getScaledHeight() / 2 + 38, 14, 13, 3, new Color(215, 103, 194, 255).getRGB());
        Fonts.Arial12.drawString("->", sr.getScaledWidth() / 2 + 34, sr.getScaledHeight() / 2 + 44, -1);

        //Load
        RenderUtils.drawRoundedRect(sr.getScaledWidth() / 2 + 10, sr.getScaledHeight() / 2 + 88, 50, 13, 3, new Color(215, 103, 194, 255).getRGB());
        Fonts.Arial12.drawString("Load config", sr.getScaledWidth() / 2 + 14, sr.getScaledHeight() / 2 + 94, -1);

        //Delete button
        RenderUtils.drawRoundedRect(sr.getScaledWidth() / 2 + 80, sr.getScaledHeight() / 2 + 88, 50, 13, 3, new Color(215, 103, 194, 255).getRGB());
        Fonts.Arial12.drawString("Delete config", sr.getScaledWidth() / 2 + 84, sr.getScaledHeight() / 2 + 94, -1);

        //end config loading box

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        //enter

        if (keyCode != Keyboard.KEY_ESCAPE) {
            if (keyCode == 28) {
                typing = false;
                return;
            }

            if (typing) {
                //backspace
                if (keyCode == 14) {
                    if (name.toCharArray().length == 0) {
                        return;
                    }
                    name = removeLastChar(name);
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
                    if (typedChar == whitelistedChar) {
                        name = name + typedChar;
                        return;
                    }
                }

                if (!Character.isLetterOrDigit(typedChar)) {
                    return;
                }


                if (typing) {
                    name = name + typedChar;
                }
            }
        }
        super.keyTyped(typedChar, keyCode);
    }

    private String removeLastChar(String s) {
        return s.substring(0, s.length() - 1);
    }

    @Override
    public void initGui() {
        listedConfigs.clear();

        //grab the first 7 configs and add them to the arraylist
        int i = 0;
        for (Config config : Client.getInstance().getConfigManager().getConfigs()) {
            i++;

            //no going above 7 :D
            if (i > 7) continue;

            listedConfigs.add(config);
        }

        super.initGui();
    }

    public void loadConfigs(int page) {
        listedConfigs.clear();

        int i = 0;
        for (int y = 0; y < page; y++) {
            i += 7;
        }

        for (int s = i; s < i + 7; s++) {
            try {
                listedConfigs.add(Client.getInstance().getConfigManager().getConfigs().get(s));
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution sr = RenderUtils.getResolution();

        //check if they're clicking on the text box to name the config
        if (RenderUtils.hover(sr.getScaledWidth() / 2 + 10, sr.getScaledHeight() / 2 - 80, mouseX, mouseY, 125, 16)) {
            typing = !typing;
            return;
        }

        //<-
        if (RenderUtils.hover(sr.getScaledWidth() / 2 + 10, sr.getScaledHeight() / 2 + 38, mouseX, mouseY, 14, 13)) {
            if (page != 0) {
                page--;
            }
            loadConfigs(page);
            return;
        }

        //->
        if (RenderUtils.hover(sr.getScaledWidth() / 2 + 30, sr.getScaledHeight() / 2 + 38, mouseX, mouseY, 14, 13)) {
            page++;
            loadConfigs(page);
            return;
        }

        if (selectedConfig != null) {
            //Load the config
            if (RenderUtils.hover(sr.getScaledWidth() / 2 + 10, sr.getScaledHeight() / 2 + 88, mouseX, mouseY, 50, 13)) {
                Client.getInstance().getConfigManager().loadConfig(selectedConfig.getName(), false);
                NotificationManager.getNotificationManager().show(new Notification(Notification.NotificationType.INFO, "Success!", "Loaded the config", 5));
                loadConfigs(page);
                return;
            }

            //Delete the config
            if (RenderUtils.hover(sr.getScaledWidth() / 2 + 80, sr.getScaledHeight() / 2 + 88, mouseX, mouseY, 50, 13)) {
                Client.getInstance().getConfigManager().removeConfig(selectedConfig.getName());
                NotificationManager.getNotificationManager().show(new Notification(Notification.NotificationType.INFO, "Success!", "Removed the config", 5));
                loadConfigs(page);
                return;
            }
        }

        //create config
        if (RenderUtils.hover(sr.getScaledWidth() / 2 + 10, sr.getScaledHeight() / 2 - 38, mouseX, mouseY, 50, 13)) {
            if (name.equalsIgnoreCase("")) {
                NotificationManager.getNotificationManager().show(new Notification(Notification.NotificationType.ERROR, "Error!", "Name the config", 5));
                return;
            }

            if (name.startsWith(" ")) {
                NotificationManager.getNotificationManager().show(new Notification(Notification.NotificationType.ERROR, "Error!", "Invalid first char", 5));
                return;
            }

            typing = false;
            Client.getInstance().getConfigManager().saveConfig(name);
            NotificationManager.getNotificationManager().show(new Notification(Notification.NotificationType.INFO, "Success!", "Created the config", 5));
            name = "";
            return;
        }

        //check if they clicked one of the configs
        int y = 4;
        for (int i = 0; i < 7; i++) {
            if (RenderUtils.hover(sr.getScaledWidth() / 2 - 140, sr.getScaledHeight() / 2 - 92 + y, mouseX, mouseY, 135, 25)) {
                try {
                    selectedConfig = listedConfigs.get(i);
                } catch (Exception e) {

                }
                return;
            }
            y += 28;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }
}
