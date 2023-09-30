package dev.tenacity.ui.sidegui.panels.infopanel;

import dev.tenacity.ui.sidegui.panels.Panel;
import dev.tenacity.utils.render.ColorUtil;

import java.util.Arrays;

public class InfoPanel extends Panel {

    private final InfoRect configInfo, scriptInfo;

    public InfoPanel() {
        configInfo = new InfoRect();
        configInfo.faqButtons.addAll(Arrays.asList(
                new InfoButton("How do I upload a config to the cloud?",
                        "You can upload your config to the cloud by going to the Config panel and then clicking the \"Upload config\" button. " +
                                "You will then be prompted with a form of information to fill out before you can upload your config."),

                new InfoButton("How do I load an online config?", "To load an online config, click the download icon in the bottom right corner of the config."),

                new InfoButton("How do I download an online config?", "To download an online config, " +
                        "click the save icon in the bottom right corner of the config next to the load button."),

                new InfoButton("How do I delete an online config?", "To delete an online config you must first own the config you are trying to delete. " +
                        "There should be an edit icon in the bottom right corner of your config next to the download icon. " +
                        "Click the edit icon and you will be prompted with avalible editing options for the config such as deletion"),

                new InfoButton("How do I update an online config?", "To update an online config you must first own the config you are trying to update. " +
                        "There should be an edit icon in the bottom right corner of your config next to the download icon. " +
                        "Click the edit icon and you will be prompted with avalible editing options for the config such as updating"),

                new InfoButton("How do I get my online config pinned?",
                        "To get your config pinned you must make a good enough config for a Developer to notice it and pin it."),

                new InfoButton("How do I save my config locally?", "To save your config locally, click the \"Save config\" button. " +
                        "You will then be prompted with a text field to name your local config."),

                new InfoButton("How does relevance ranking in cloud configs work?", "For configs, the relevance ranking is a weighted score system. " +
                        "We take into account the current server you are on, the ratio of upvotes to total votes, if the config was made on the current version, " +
                        "and how recently the config was updated."),
                new InfoButton("How can I copy my config's share code?", "When hovering over the \"Hover for more information\" text, click while " +
                        "holding the SHIFT key and the share code will be copied to your clipboard."))

        );

        scriptInfo = new InfoRect();
        scriptInfo.faqButtons.addAll(Arrays.asList(
                new InfoButton("How do I upload a script to the cloud?",
                        "You can upload your script to the cloud by going to the Script panel and switching to the \"Local\" tab. " +
                                "Your local scripts will have an icon that looks like a cloud uploading. Click this icon to upload that specific script."),

                new InfoButton("How do I download an online script?",
                        "To download an online script, click the file icon in the bottom right corner of the script."),

                new InfoButton("How do I delete an online script?", "To delete an online script you must first own the script you are trying to delete. " +
                        "There should be an edit icon in the bottom right corner of your script next to the download/save icon. " +
                        "Click the edit icon and you will be prompted with avalible editing options for the config such as deletion."),

                new InfoButton("How do I update an online script?", "To delete an online script you must first own the script you are trying to update. " +
                        "There should be an edit icon in the bottom right corner of your script next to the download/save icon. " +
                        "Click the edit icon and you will be prompted with avalible editing options for the config such as updating"),


                new InfoButton("How do I code a script?",
                        "Scripts are coded in JavaScript using the cedoscript engine. " +
                                "You can find more information about all the scripting functionalities by clicking the \"Open documentation\" button."),

                new InfoButton("What folder should my local scripts be in?",
                        "If you click the \"Open folder\" button the correct file location will open up in your file explorer."),

                new InfoButton("How do I refresh my local scripts?", "To refresh all script and config data, click the refresh icon in the top hotbar."),

                new InfoButton("How does relevance ranking in cloud scripts work?", "For scripts, the relevance ranking is still a weighted score system. " +
                        "We take into account the ratio of upvotes to total votes and how recently the script was updated."),

                new InfoButton("How can I copy my scripts's share code?", "When hovering over the \"Hover for more information\" text, click while " +
                        "holding the SHIFT key and the share code will be copied to your clipboard.")
        ));


    }


    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        lithiumBoldFont40.drawString("Info", getX() + 8, getY() + 8, getTextColor());
        lithiumFont18.drawString("Developed by Liticane, Nyghtfull", getX() + 8, getY() + 30, ColorUtil.applyOpacity(getTextColor(), .3f));

        float spacing = 8;
        float infoWidth = (getWidth() - (spacing * 3)) / 2f;
        configInfo.x = getX() + spacing;
        configInfo.y = getY() + 40 + spacing;
        configInfo.width = infoWidth;
        configInfo.height = getHeight() - (40 + spacing * 2);
        configInfo.alpha = getAlpha();
        configInfo.drawScreen(mouseX, mouseY);

        scriptInfo.x = configInfo.x + configInfo.width + spacing;
        scriptInfo.y = getY() + 40 + spacing;
        scriptInfo.width = infoWidth;
        scriptInfo.height = getHeight() - (40 + spacing * 2);
        scriptInfo.alpha = getAlpha();
        scriptInfo.drawScreen(mouseX, mouseY);

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        configInfo.mouseClicked(mouseX, mouseY, button);
        scriptInfo.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
