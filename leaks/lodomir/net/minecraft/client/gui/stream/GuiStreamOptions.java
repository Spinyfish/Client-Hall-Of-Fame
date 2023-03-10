/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui.stream;

import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.stream.GuiIngestServers;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;

public class GuiStreamOptions
extends GuiScreen {
    private static final GameSettings.Options[] field_152312_a = new GameSettings.Options[]{GameSettings.Options.STREAM_BYTES_PER_PIXEL, GameSettings.Options.STREAM_FPS, GameSettings.Options.STREAM_KBPS, GameSettings.Options.STREAM_SEND_METADATA, GameSettings.Options.STREAM_VOLUME_MIC, GameSettings.Options.STREAM_VOLUME_SYSTEM, GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR, GameSettings.Options.STREAM_COMPRESSION};
    private static final GameSettings.Options[] field_152316_f = new GameSettings.Options[]{GameSettings.Options.STREAM_CHAT_ENABLED, GameSettings.Options.STREAM_CHAT_USER_FILTER};
    private final GuiScreen parentScreen;
    private final GameSettings field_152318_h;
    private String field_152319_i;
    private String field_152313_r;
    private int field_152314_s;
    private boolean field_152315_t = false;

    public GuiStreamOptions(GuiScreen parentScreenIn, GameSettings p_i1073_2_) {
        this.parentScreen = parentScreenIn;
        this.field_152318_h = p_i1073_2_;
    }

    @Override
    public void initGui() {
        int i = 0;
        this.field_152319_i = I18n.format("options.stream.title", new Object[0]);
        this.field_152313_r = I18n.format("options.stream.chat.title", new Object[0]);
        for (GameSettings.Options gamesettings$options : field_152312_a) {
            if (gamesettings$options.getEnumFloat()) {
                this.buttonList.add(new GuiOptionSlider(gamesettings$options.returnEnumOrdinal(), width / 2 - 155 + i % 2 * 160, height / 6 + 24 * (i >> 1), gamesettings$options));
            } else {
                this.buttonList.add(new GuiOptionButton(gamesettings$options.returnEnumOrdinal(), width / 2 - 155 + i % 2 * 160, height / 6 + 24 * (i >> 1), gamesettings$options, this.field_152318_h.getKeyBinding(gamesettings$options)));
            }
            ++i;
        }
        if (i % 2 == 1) {
            ++i;
        }
        this.field_152314_s = height / 6 + 24 * (i >> 1) + 6;
        i += 2;
        for (GameSettings.Options gamesettings$options1 : field_152316_f) {
            if (gamesettings$options1.getEnumFloat()) {
                this.buttonList.add(new GuiOptionSlider(gamesettings$options1.returnEnumOrdinal(), width / 2 - 155 + i % 2 * 160, height / 6 + 24 * (i >> 1), gamesettings$options1));
            } else {
                this.buttonList.add(new GuiOptionButton(gamesettings$options1.returnEnumOrdinal(), width / 2 - 155 + i % 2 * 160, height / 6 + 24 * (i >> 1), gamesettings$options1, this.field_152318_h.getKeyBinding(gamesettings$options1)));
            }
            ++i;
        }
        this.buttonList.add(new GuiButton(200, width / 2 - 155, height / 6 + 168, 150, 20, I18n.format("gui.done", new Object[0])));
        GuiButton guibutton = new GuiButton(201, width / 2 + 5, height / 6 + 168, 150, 20, I18n.format("options.stream.ingestSelection", new Object[0]));
        guibutton.enabled = this.mc.getTwitchStream().isReadyToBroadcast() && this.mc.getTwitchStream().func_152925_v().length > 0 || this.mc.getTwitchStream().func_152908_z();
        this.buttonList.add(guibutton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id < 100 && button instanceof GuiOptionButton) {
                GameSettings.Options gamesettings$options = ((GuiOptionButton)button).returnEnumOptions();
                this.field_152318_h.setOptionValue(gamesettings$options, 1);
                button.displayString = this.field_152318_h.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));
                if (this.mc.getTwitchStream().isBroadcasting() && gamesettings$options != GameSettings.Options.STREAM_CHAT_ENABLED && gamesettings$options != GameSettings.Options.STREAM_CHAT_USER_FILTER) {
                    this.field_152315_t = true;
                }
            } else if (button instanceof GuiOptionSlider) {
                if (button.id == GameSettings.Options.STREAM_VOLUME_MIC.returnEnumOrdinal()) {
                    this.mc.getTwitchStream().updateStreamVolume();
                } else if (button.id == GameSettings.Options.STREAM_VOLUME_SYSTEM.returnEnumOrdinal()) {
                    this.mc.getTwitchStream().updateStreamVolume();
                } else if (this.mc.getTwitchStream().isBroadcasting()) {
                    this.field_152315_t = true;
                }
            }
            if (button.id == 200) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentScreen);
            } else if (button.id == 201) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiIngestServers(this));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiStreamOptions.drawCenteredString(this.fontRendererObj, this.field_152319_i, width / 2, 20, 0xFFFFFF);
        GuiStreamOptions.drawCenteredString(this.fontRendererObj, this.field_152313_r, width / 2, this.field_152314_s, 0xFFFFFF);
        if (this.field_152315_t) {
            GuiStreamOptions.drawCenteredString(this.fontRendererObj, (Object)((Object)EnumChatFormatting.RED) + I18n.format("options.stream.changes", new Object[0]), width / 2, 20 + this.fontRendererObj.FONT_HEIGHT, 0xFFFFFF);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

