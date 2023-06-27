package rip.athena.client.gui.clickgui.pages;

import net.minecraft.client.*;
import rip.athena.client.gui.clickgui.*;
import rip.athena.client.*;
import java.net.*;
import java.awt.*;
import org.json.*;
import java.io.*;
import rip.athena.client.requests.*;
import rip.athena.client.config.save.*;
import rip.athena.client.gui.clickgui.components.profiles.*;
import rip.athena.client.gui.framework.*;
import rip.athena.client.gui.clickgui.components.macros.*;
import org.apache.commons.lang3.*;
import net.minecraft.client.gui.*;
import java.util.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.gui.framework.draw.*;
import rip.athena.client.gui.clickgui.components.mods.*;

public class ProfilesPage extends Page
{
    private MacroTextfield nameNew;
    private MacroTextfield nameAdd;
    private MacroButton add;
    private ProfilesBlueButton download;
    private MacroButton delete;
    private ModScrollPane scrollPane;
    private String PROFILES_URL;
    
    public ProfilesPage(final Minecraft mc, final Menu menu, final IngameMenu parent) {
        super(mc, menu, parent);
        this.PROFILES_URL = "http://download.athena.rip:3000/api/v1/profiles/upload";
    }
    
    @Override
    public void onInit() {
        final int width = 300;
        final int x = this.menu.getWidth() - width + 20;
        final int y = 59;
        final int compWidth = width - 6 - 40;
        this.nameNew = new MacroTextfield(TextPattern.TEXT_AND_NUMBERS, x, y + 85, compWidth, 22, "...");
        this.nameAdd = new MacroTextfield(TextPattern.TEXT_AND_NUMBERS, x, y + 255, compWidth, 22, "...");
        final int acceptWidth = compWidth - 40;
        this.add = new MacroButton("ADD", x - 21 + width / 2 - acceptWidth / 2, y + 125, acceptWidth, 22, true) {
            @Override
            public void onAction() {
                this.setActive(false);
                if (ProfilesPage.this.nameNew.getText().isEmpty()) {
                    return;
                }
                Athena.INSTANCE.getConfigManager().getConfig(ProfilesPage.this.nameNew.getText()).save();
                ProfilesPage.this.nameNew.setText("");
                ProfilesPage.this.populateScrollPane();
            }
        };
        this.download = new ProfilesBlueButton("DOWNLOAD", x - 21 + width / 2 - acceptWidth / 2, y + 295, acceptWidth, 22) {
            @Override
            public void onAction() {
                this.setActive(false);
                if (ProfilesPage.this.nameAdd.getText().isEmpty()) {
                    return;
                }
                try {
                    final WebRequest request = new WebRequest("http://download.athena.rip:3000/api/v1/profiles/" + URLEncoder.encode(ProfilesPage.this.nameAdd.getText(), "UTF-8") + "/config.json", "GET", ContentType.FORM, false);
                    final WebRequestResult result = request.connect();
                    if (result.getResponse() == 200) {
                        Athena.INSTANCE.getConfigManager().getConfig(ProfilesPage.this.nameAdd.getText());
                        Athena.INSTANCE.getNotificationManager().showNotification("Successfully downloaded config", Color.GREEN);
                    }
                    else {
                        Athena.INSTANCE.getNotificationManager().showNotification("Config failed to download, make sure the profile name is accurate.", Color.RED);
                    }
                }
                catch (JSONException | NoSuchElementException | IOException ex2) {
                    final Exception ex;
                    final Exception e = ex;
                    Athena.INSTANCE.getLog().error("Failed to download config." + e);
                }
                ProfilesPage.this.nameAdd.setText("");
                ProfilesPage.this.populateScrollPane();
            }
        };
        this.delete = new MacroButton("CLEAR ALL PROFILES", x - 21 + width / 2 - compWidth / 2, this.menu.getHeight() - 22 - 20, compWidth, 22, false) {
            @Override
            public void onAction() {
                this.setActive(false);
                Athena.INSTANCE.getConfigManager().deleteAllConfigs();
                ProfilesPage.this.populateScrollPane();
            }
        };
        this.scrollPane = new ModScrollPane(260, 140, this.menu.getWidth() - width - 240, this.menu.getHeight() - 141, false);
        this.populateScrollPane();
    }
    
    private void populateScrollPane() {
        this.scrollPane.getComponents().clear();
        final int spacing = 15;
        final int height = 110;
        final int defaultX = spacing;
        int y = spacing;
        int x = spacing;
        final int width = 190;
        final int maxWidth = this.scrollPane.getWidth() - spacing * 2;
        final int innerSpacing = 5;
        final int innerWidth = width - innerSpacing * 2;
        final int buttonHeight = 20;
        final int exitButtonSize = 18;
        for (final Config config : Athena.INSTANCE.getConfigManager().getConfigs()) {
            this.scrollPane.addComponent(new ProfilesBase(config.getName(), x, y, width, height));
            this.scrollPane.addComponent(new SimpleTextButton("X", x + innerWidth - exitButtonSize + innerSpacing, y + innerSpacing, exitButtonSize, exitButtonSize, false) {
                @Override
                public void onAction() {
                    this.setActive(false);
                    config.delete();
                    ProfilesPage.this.populateScrollPane();
                }
            });
            this.scrollPane.addComponent(new ProfilesBlueButton("UPLOAD PROFILE", x + innerSpacing, y + height - buttonHeight - innerSpacing * 3 - spacing, innerWidth, buttonHeight) {
                @Override
                public void onAction() {
                    this.setActive(false);
                    try {
                        final String code = URLEncoder.encode(RandomStringUtils.randomAlphabetic(12).toLowerCase(), "UTF-8");
                        final WebRequest request = new WebRequest(ProfilesPage.this.PROFILES_URL, "POST", ContentType.MULTIPART_FORM, false);
                        final String boundary = "WebKitFormBoundaryYDPG5KWy5y4yolEf";
                        request.setBoundary(boundary);
                        request.setHeader("Content-Type", "multipart/form-data; boundary=----" + boundary);
                        request.setHeader("Connection", "keep-alive");
                        request.setRawData("------" + boundary + "\r\nContent-Disposition: form-data; name=\"id\"\r\n\r\n" + code + "\r\n------" + boundary + "\r\nContent-Disposition: form-data; name=\"fileToUpload\"; filename=\"config.json\"\r\nContent-Type: application/json\r\n\r\n" + config.toString() + "\r\n------" + boundary + "--\r\n");
                        final WebRequestResult result = request.connect();
                        if (result.getResponse() == 200) {
                            Athena.INSTANCE.getNotificationManager().showNotification("Uploaded | Code: [" + code + "], copied to clipboard.", Color.GREEN);
                        }
                        else {
                            Athena.INSTANCE.getNotificationManager().showNotification("Config failed to upload.", Color.RED);
                        }
                        GuiScreen.setClipboardString(code);
                    }
                    catch (JSONException | NoSuchElementException | IOException ex2) {
                        final Exception ex;
                        final Exception e = ex;
                        Athena.INSTANCE.getNotificationManager().showNotification("Config failed to upload.", Color.RED);
                    }
                }
            });
            this.scrollPane.addComponent(new MacroButton(config.isEnabled() ? "ENABLED" : "DISABLED", x + innerSpacing, y + height - innerSpacing * 2 - spacing, innerWidth, buttonHeight, config.isEnabled()) {
                @Override
                public void onAction() {
                    this.setActive(false);
                    final Config cfg = Athena.INSTANCE.getConfigManager().getLoadedConfig();
                    if (cfg != null) {
                        cfg.save();
                    }
                    config.load();
                    ProfilesPage.this.populateScrollPane();
                }
            });
            x += spacing + width;
            if (x + spacing + width > maxWidth) {
                x = defaultX;
                y += height + spacing;
            }
        }
    }
    
    @Override
    public void onRender() {
        final int width = 300;
        final int x = this.menu.getX() + this.menu.getWidth() - width + 20;
        int y = this.menu.getY() + 59;
        final int height = 32;
        this.drawVerticalLine(this.menu.getX() + 215, y + height - 30, height + 432, 3, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
        if (Settings.customGuiFont) {
            FontManager.getNunitoBold(30).drawString("PROFILES", this.menu.getX() + 235, this.menu.getY() + 80, IngameMenu.MENU_HEADER_TEXT_COLOR);
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString("PROFILES", this.menu.getX() + 235, this.menu.getY() + 80, IngameMenu.MENU_HEADER_TEXT_COLOR);
        }
        DrawImpl.drawRect(this.menu.getX() + this.menu.getWidth() - width, this.menu.getY() + 58, width, this.menu.getHeight() - 58, MacrosPage.MENU_SIDE_BG_COLOR);
        DrawImpl.drawRect(this.menu.getX() + this.menu.getWidth() - width, this.menu.getY() + 58, width, height + 1, ModCategoryButton.MAIN_COLOR);
        this.drawShadowDown(this.menu.getX() + this.menu.getWidth() - width, y + height, width);
        if (Settings.customGuiFont) {
            FontManager.getProductSansRegular(30).drawString("CREATE NEW PROFILE", this.menu.getX() + this.menu.getWidth() - width / 2.0f - FontManager.getProductSansRegular(30).width("CREATE NEW PROFILE") / 2, y + height / 2.0f - rip.athena.client.font.FontManager.baloo17.getHeight("CREATE NEW PROFILE") / 2.0f, IngameMenu.MENU_HEADER_TEXT_COLOR);
        }
        else {
            this.mc.fontRendererObj.drawString("CREATE NEW PROFILE", (int)(this.menu.getX() + this.menu.getWidth() - width / 2.0f - this.mc.fontRendererObj.getStringWidth("CREATE NEW PROFILE") / 2), (int)(y + height / 2.0f - this.mc.fontRendererObj.FONT_HEIGHT / 2), IngameMenu.MENU_HEADER_TEXT_COLOR);
        }
        this.drawShadowDown(this.menu.getX() + this.menu.getWidth() - width, y - 1, width);
        y += 60;
        if (Settings.customGuiFont) {
            FontManager.getProductSansRegular(30).drawString("ENTER NAME", x, y, IngameMenu.MENU_HEADER_TEXT_COLOR);
        }
        else {
            this.mc.fontRendererObj.drawString("ENTER NAME", x, y, IngameMenu.MENU_HEADER_TEXT_COLOR);
        }
        y += 120;
        DrawImpl.drawRect(this.menu.getX() + this.menu.getWidth() - width, y, width, height + 1, ModCategoryButton.MAIN_COLOR);
        this.drawShadowDown(this.menu.getX() + this.menu.getWidth() - width, y + height, width);
        this.drawShadowUp(this.menu.getX() + this.menu.getWidth() - width, y, width);
        if (Settings.customGuiFont) {
            FontManager.getProductSansRegular(30).drawString("DOWNLOAD PROFILE", this.menu.getX() + this.menu.getWidth() - width / 2.0f - FontManager.getProductSansRegular(30).width("DOWNLOAD PROFILE") / 2, y + height / 2.0f - rip.athena.client.font.FontManager.baloo17.getHeight("DOWNLOAD PROFILE") / 2.0f, IngameMenu.MENU_HEADER_TEXT_COLOR);
        }
        else {
            this.mc.fontRendererObj.drawString("DOWNLOAD PROFILE", (int)(this.menu.getX() + this.menu.getWidth() - width / 2.0f - this.mc.fontRendererObj.getStringWidth("DOWNLOAD PROFILE") / 2), (int)(y + height / 2.0f - this.mc.fontRendererObj.FONT_HEIGHT / 2), IngameMenu.MENU_HEADER_TEXT_COLOR);
        }
        y += 50;
        if (Settings.customGuiFont) {
            FontManager.getProductSansRegular(30).drawString("PROFILE CODE", x, y, IngameMenu.MENU_HEADER_TEXT_COLOR);
        }
        else {
            this.mc.fontRendererObj.drawString("PROFILE CODE", x, y, IngameMenu.MENU_HEADER_TEXT_COLOR);
        }
    }
    
    @Override
    public void onLoad() {
        this.menu.addComponent(this.nameNew);
        this.menu.addComponent(this.nameAdd);
        this.menu.addComponent(this.add);
        this.menu.addComponent(this.download);
        this.menu.addComponent(this.delete);
        this.menu.addComponent(this.scrollPane);
    }
    
    @Override
    public void onUnload() {
    }
    
    @Override
    public void onOpen() {
    }
    
    @Override
    public void onClose() {
    }
}
