/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.lwjgl.input.Keyboard
 */
package net.minecraft.client.gui;

import java.io.IOException;
import java.util.Random;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateFlatWorld;
import net.minecraft.client.gui.GuiCustomizeWorldScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

public class GuiCreateWorld
extends GuiScreen {
    private GuiScreen parentScreen;
    private GuiTextField field_146333_g;
    private GuiTextField field_146335_h;
    private String field_146336_i;
    private String gameMode = "survival";
    private String field_175300_s;
    private boolean field_146341_s = true;
    private boolean allowCheats;
    private boolean field_146339_u;
    private boolean field_146338_v;
    private boolean field_146337_w;
    private boolean field_146345_x;
    private boolean field_146344_y;
    private GuiButton btnGameMode;
    private GuiButton btnMoreOptions;
    private GuiButton btnMapFeatures;
    private GuiButton btnBonusItems;
    private GuiButton btnMapType;
    private GuiButton btnAllowCommands;
    private GuiButton btnCustomizeType;
    private String field_146323_G;
    private String field_146328_H;
    private String field_146329_I;
    private String field_146330_J;
    private int selectedIndex;
    public String chunkProviderSettingsJson = "";
    private static final String[] disallowedFilenames = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

    public GuiCreateWorld(GuiScreen p_i46320_1_) {
        this.parentScreen = p_i46320_1_;
        this.field_146329_I = "";
        this.field_146330_J = I18n.format("selectWorld.newWorld", new Object[0]);
    }

    @Override
    public void updateScreen() {
        this.field_146333_g.updateCursorCounter();
        this.field_146335_h.updateCursorCounter();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents((boolean)true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width / 2 - 155, height - 28, 150, 20, I18n.format("selectWorld.create", new Object[0])));
        this.buttonList.add(new GuiButton(1, width / 2 + 5, height - 28, 150, 20, I18n.format("gui.cancel", new Object[0])));
        this.btnGameMode = new GuiButton(2, width / 2 - 75, 115, 150, 20, I18n.format("selectWorld.gameMode", new Object[0]));
        this.buttonList.add(this.btnGameMode);
        this.btnMoreOptions = new GuiButton(3, width / 2 - 75, 187, 150, 20, I18n.format("selectWorld.moreWorldOptions", new Object[0]));
        this.buttonList.add(this.btnMoreOptions);
        this.btnMapFeatures = new GuiButton(4, width / 2 - 155, 100, 150, 20, I18n.format("selectWorld.mapFeatures", new Object[0]));
        this.buttonList.add(this.btnMapFeatures);
        this.btnMapFeatures.visible = false;
        this.btnBonusItems = new GuiButton(7, width / 2 + 5, 151, 150, 20, I18n.format("selectWorld.bonusItems", new Object[0]));
        this.buttonList.add(this.btnBonusItems);
        this.btnBonusItems.visible = false;
        this.btnMapType = new GuiButton(5, width / 2 + 5, 100, 150, 20, I18n.format("selectWorld.mapType", new Object[0]));
        this.buttonList.add(this.btnMapType);
        this.btnMapType.visible = false;
        this.btnAllowCommands = new GuiButton(6, width / 2 - 155, 151, 150, 20, I18n.format("selectWorld.allowCommands", new Object[0]));
        this.buttonList.add(this.btnAllowCommands);
        this.btnAllowCommands.visible = false;
        this.btnCustomizeType = new GuiButton(8, width / 2 + 5, 120, 150, 20, I18n.format("selectWorld.customizeType", new Object[0]));
        this.buttonList.add(this.btnCustomizeType);
        this.btnCustomizeType.visible = false;
        this.field_146333_g = new GuiTextField(9, this.fontRendererObj, width / 2 - 100, 60, 200, 20);
        this.field_146333_g.setFocused(true);
        this.field_146333_g.setText(this.field_146330_J);
        this.field_146335_h = new GuiTextField(10, this.fontRendererObj, width / 2 - 100, 60, 200, 20);
        this.field_146335_h.setText(this.field_146329_I);
        this.func_146316_a(this.field_146344_y);
        this.func_146314_g();
        this.func_146319_h();
    }

    private void func_146314_g() {
        this.field_146336_i = this.field_146333_g.getText().trim();
        for (char c0 : ChatAllowedCharacters.allowedCharactersArray) {
            this.field_146336_i = this.field_146336_i.replace(c0, '_');
        }
        if (StringUtils.isEmpty((CharSequence)this.field_146336_i)) {
            this.field_146336_i = "World";
        }
        this.field_146336_i = GuiCreateWorld.func_146317_a(this.mc.getSaveLoader(), this.field_146336_i);
    }

    private void func_146319_h() {
        this.btnGameMode.displayString = I18n.format("selectWorld.gameMode", new Object[0]) + ": " + I18n.format("selectWorld.gameMode." + this.gameMode, new Object[0]);
        this.field_146323_G = I18n.format("selectWorld.gameMode." + this.gameMode + ".line1", new Object[0]);
        this.field_146328_H = I18n.format("selectWorld.gameMode." + this.gameMode + ".line2", new Object[0]);
        this.btnMapFeatures.displayString = I18n.format("selectWorld.mapFeatures", new Object[0]) + " ";
        this.btnMapFeatures.displayString = this.field_146341_s ? this.btnMapFeatures.displayString + I18n.format("options.on", new Object[0]) : this.btnMapFeatures.displayString + I18n.format("options.off", new Object[0]);
        this.btnBonusItems.displayString = I18n.format("selectWorld.bonusItems", new Object[0]) + " ";
        this.btnBonusItems.displayString = this.field_146338_v && !this.field_146337_w ? this.btnBonusItems.displayString + I18n.format("options.on", new Object[0]) : this.btnBonusItems.displayString + I18n.format("options.off", new Object[0]);
        this.btnMapType.displayString = I18n.format("selectWorld.mapType", new Object[0]) + " " + I18n.format(WorldType.worldTypes[this.selectedIndex].getTranslateName(), new Object[0]);
        this.btnAllowCommands.displayString = I18n.format("selectWorld.allowCommands", new Object[0]) + " ";
        this.btnAllowCommands.displayString = this.allowCheats && !this.field_146337_w ? this.btnAllowCommands.displayString + I18n.format("options.on", new Object[0]) : this.btnAllowCommands.displayString + I18n.format("options.off", new Object[0]);
    }

    public static String func_146317_a(ISaveFormat p_146317_0_, String p_146317_1_) {
        p_146317_1_ = p_146317_1_.replaceAll("[\\./\"]", "_");
        for (String s : disallowedFilenames) {
            if (!p_146317_1_.equalsIgnoreCase(s)) continue;
            p_146317_1_ = "_" + p_146317_1_ + "_";
        }
        while (p_146317_0_.getWorldInfo(p_146317_1_) != null) {
            p_146317_1_ = p_146317_1_ + "-";
        }
        return p_146317_1_;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents((boolean)false);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 1) {
                this.mc.displayGuiScreen(this.parentScreen);
            } else if (button.id == 0) {
                this.mc.displayGuiScreen(null);
                if (this.field_146345_x) {
                    return;
                }
                this.field_146345_x = true;
                long i = new Random().nextLong();
                String s = this.field_146335_h.getText();
                if (!StringUtils.isEmpty((CharSequence)s)) {
                    try {
                        long j = Long.parseLong(s);
                        if (j != 0L) {
                            i = j;
                        }
                    }
                    catch (NumberFormatException var7) {
                        i = s.hashCode();
                    }
                }
                WorldSettings.GameType worldsettings$gametype = WorldSettings.GameType.getByName(this.gameMode);
                WorldSettings worldsettings = new WorldSettings(i, worldsettings$gametype, this.field_146341_s, this.field_146337_w, WorldType.worldTypes[this.selectedIndex]);
                worldsettings.setWorldName(this.chunkProviderSettingsJson);
                if (this.field_146338_v && !this.field_146337_w) {
                    worldsettings.enableBonusChest();
                }
                if (this.allowCheats && !this.field_146337_w) {
                    worldsettings.enableCommands();
                }
                this.mc.launchIntegratedServer(this.field_146336_i, this.field_146333_g.getText().trim(), worldsettings);
            } else if (button.id == 3) {
                this.func_146315_i();
            } else if (button.id == 2) {
                if (this.gameMode.equals("survival")) {
                    if (!this.field_146339_u) {
                        this.allowCheats = false;
                    }
                    this.field_146337_w = false;
                    this.gameMode = "hardcore";
                    this.field_146337_w = true;
                    this.btnAllowCommands.enabled = false;
                    this.btnBonusItems.enabled = false;
                    this.func_146319_h();
                } else if (this.gameMode.equals("hardcore")) {
                    if (!this.field_146339_u) {
                        this.allowCheats = true;
                    }
                    this.field_146337_w = false;
                    this.gameMode = "creative";
                    this.func_146319_h();
                    this.field_146337_w = false;
                    this.btnAllowCommands.enabled = true;
                    this.btnBonusItems.enabled = true;
                } else {
                    if (!this.field_146339_u) {
                        this.allowCheats = false;
                    }
                    this.gameMode = "survival";
                    this.func_146319_h();
                    this.btnAllowCommands.enabled = true;
                    this.btnBonusItems.enabled = true;
                    this.field_146337_w = false;
                }
                this.func_146319_h();
            } else if (button.id == 4) {
                this.field_146341_s = !this.field_146341_s;
                this.func_146319_h();
            } else if (button.id == 7) {
                this.field_146338_v = !this.field_146338_v;
                this.func_146319_h();
            } else if (button.id == 5) {
                ++this.selectedIndex;
                if (this.selectedIndex >= WorldType.worldTypes.length) {
                    this.selectedIndex = 0;
                }
                while (!this.func_175299_g()) {
                    ++this.selectedIndex;
                    if (this.selectedIndex < WorldType.worldTypes.length) continue;
                    this.selectedIndex = 0;
                }
                this.chunkProviderSettingsJson = "";
                this.func_146319_h();
                this.func_146316_a(this.field_146344_y);
            } else if (button.id == 6) {
                this.field_146339_u = true;
                this.allowCheats = !this.allowCheats;
                this.func_146319_h();
            } else if (button.id == 8) {
                if (WorldType.worldTypes[this.selectedIndex] == WorldType.FLAT) {
                    this.mc.displayGuiScreen(new GuiCreateFlatWorld(this, this.chunkProviderSettingsJson));
                } else {
                    this.mc.displayGuiScreen(new GuiCustomizeWorldScreen(this, this.chunkProviderSettingsJson));
                }
            }
        }
    }

    private boolean func_175299_g() {
        WorldType worldtype = WorldType.worldTypes[this.selectedIndex];
        return worldtype != null && worldtype.getCanBeCreated() ? (worldtype == WorldType.DEBUG_WORLD ? GuiCreateWorld.isShiftKeyDown() : true) : false;
    }

    private void func_146315_i() {
        this.func_146316_a(!this.field_146344_y);
    }

    private void func_146316_a(boolean p_146316_1_) {
        this.field_146344_y = p_146316_1_;
        if (WorldType.worldTypes[this.selectedIndex] == WorldType.DEBUG_WORLD) {
            this.btnGameMode.visible = !this.field_146344_y;
            this.btnGameMode.enabled = false;
            if (this.field_175300_s == null) {
                this.field_175300_s = this.gameMode;
            }
            this.gameMode = "spectator";
            this.btnMapFeatures.visible = false;
            this.btnBonusItems.visible = false;
            this.btnMapType.visible = this.field_146344_y;
            this.btnAllowCommands.visible = false;
            this.btnCustomizeType.visible = false;
        } else {
            this.btnGameMode.visible = !this.field_146344_y;
            this.btnGameMode.enabled = true;
            if (this.field_175300_s != null) {
                this.gameMode = this.field_175300_s;
                this.field_175300_s = null;
            }
            this.btnMapFeatures.visible = this.field_146344_y && WorldType.worldTypes[this.selectedIndex] != WorldType.CUSTOMIZED;
            this.btnBonusItems.visible = this.field_146344_y;
            this.btnMapType.visible = this.field_146344_y;
            this.btnAllowCommands.visible = this.field_146344_y;
            this.btnCustomizeType.visible = this.field_146344_y && (WorldType.worldTypes[this.selectedIndex] == WorldType.FLAT || WorldType.worldTypes[this.selectedIndex] == WorldType.CUSTOMIZED);
        }
        this.func_146319_h();
        this.btnMoreOptions.displayString = this.field_146344_y ? I18n.format("gui.done", new Object[0]) : I18n.format("selectWorld.moreWorldOptions", new Object[0]);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.field_146333_g.isFocused() && !this.field_146344_y) {
            this.field_146333_g.textboxKeyTyped(typedChar, keyCode);
            this.field_146330_J = this.field_146333_g.getText();
        } else if (this.field_146335_h.isFocused() && this.field_146344_y) {
            this.field_146335_h.textboxKeyTyped(typedChar, keyCode);
            this.field_146329_I = this.field_146335_h.getText();
        }
        if (keyCode == 28 || keyCode == 156) {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
        ((GuiButton)this.buttonList.get((int)0)).enabled = this.field_146333_g.getText().length() > 0;
        this.func_146314_g();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.field_146344_y) {
            this.field_146335_h.mouseClicked(mouseX, mouseY, mouseButton);
        } else {
            this.field_146333_g.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiCreateWorld.drawCenteredString(this.fontRendererObj, I18n.format("selectWorld.create", new Object[0]), width / 2, 20, -1);
        if (this.field_146344_y) {
            this.drawString(this.fontRendererObj, I18n.format("selectWorld.enterSeed", new Object[0]), width / 2 - 100, 47.0f, -6250336);
            this.drawString(this.fontRendererObj, I18n.format("selectWorld.seedInfo", new Object[0]), width / 2 - 100, 85.0f, -6250336);
            if (this.btnMapFeatures.visible) {
                this.drawString(this.fontRendererObj, I18n.format("selectWorld.mapFeatures.info", new Object[0]), width / 2 - 150, 122.0f, -6250336);
            }
            if (this.btnAllowCommands.visible) {
                this.drawString(this.fontRendererObj, I18n.format("selectWorld.allowCommands.info", new Object[0]), width / 2 - 150, 172.0f, -6250336);
            }
            this.field_146335_h.drawTextBox();
            if (WorldType.worldTypes[this.selectedIndex].showWorldInfoNotice()) {
                this.fontRendererObj.drawSplitString(I18n.format(WorldType.worldTypes[this.selectedIndex].func_151359_c(), new Object[0]), this.btnMapType.xPosition + 2, this.btnMapType.yPosition + 22, this.btnMapType.getButtonWidth(), 0xA0A0A0);
            }
        } else {
            this.drawString(this.fontRendererObj, I18n.format("selectWorld.enterName", new Object[0]), width / 2 - 100, 47.0f, -6250336);
            this.drawString(this.fontRendererObj, I18n.format("selectWorld.resultFolder", new Object[0]) + " " + this.field_146336_i, width / 2 - 100, 85.0f, -6250336);
            this.field_146333_g.drawTextBox();
            this.drawString(this.fontRendererObj, this.field_146323_G, width / 2 - 100, 137.0f, -6250336);
            this.drawString(this.fontRendererObj, this.field_146328_H, width / 2 - 100, 149.0f, -6250336);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void func_146318_a(WorldInfo p_146318_1_) {
        this.field_146330_J = I18n.format("selectWorld.newWorld.copyOf", p_146318_1_.getWorldName());
        this.field_146329_I = p_146318_1_.getSeed() + "";
        this.selectedIndex = p_146318_1_.getTerrainType().getWorldTypeID();
        this.chunkProviderSettingsJson = p_146318_1_.getGeneratorOptions();
        this.field_146341_s = p_146318_1_.isMapFeaturesEnabled();
        this.allowCheats = p_146318_1_.areCommandsAllowed();
        if (p_146318_1_.isHardcoreModeEnabled()) {
            this.gameMode = "hardcore";
        } else if (p_146318_1_.getGameType().isSurvivalOrAdventure()) {
            this.gameMode = "survival";
        } else if (p_146318_1_.getGameType().isCreative()) {
            this.gameMode = "creative";
        }
    }
}

