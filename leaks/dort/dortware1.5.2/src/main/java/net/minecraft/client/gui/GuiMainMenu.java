package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.font.CustomFontRenderer;
import tech.dort.dortware.impl.gui.alt.GUIAltManager;
import tech.dort.dortware.impl.gui.alt.GUIProxy;
import tech.dort.dortware.impl.gui.click.GuiUtils;
import tech.dort.dortware.impl.utils.render.ColorUtil;

import java.awt.*;
import java.io.IOException;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }

    public void initGui() {
//        Display.setTitle("Dortware - Main Menu");
        int var3 = this.height / 4 + 48;
        this.addSingleplayerMultiplayerButtons(var3);

        this.buttonList.add(new GuiButton(0, this.width / 2F - 100, var3 + 92 - 8, 102, 20, "Settings"));
        this.buttonList.add(new GuiButton(4, this.width / 2F + 2, var3 + 92 - 8, 98, 20, I18n.format("menu.quit")));
        ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
    }

    private void addSingleplayerMultiplayerButtons(int p_73969_1_) {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, p_73969_1_, I18n.format("menu.singleplayer")));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, p_73969_1_ + 24 - 4, I18n.format("menu.multiplayer")));
        this.buttonList.add(new GuiButton(14, this.width / 2 - 100, p_73969_1_ + 24 + 8 * 2, "Alt Manager"));
        this.buttonList.add(new GuiButton(69420, this.width / 2 - 100, p_73969_1_ + 24 + 12 * 3, "Proxies"));
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;

            case 5:
                this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
                break;

            case 1:
                this.mc.displayGuiScreen(new GuiSelectWorld(this));
                break;

            case 2:
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
                break;

            case 14:
                this.mc.displayGuiScreen(new GUIAltManager(this));
                break;

            case 4:
                this.mc.shutdown();
                break;

            case 11:
                this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
                break;

            case 69420:
                this.mc.displayGuiScreen(new GUIProxy(this));
                break;

            case 12:
                ISaveFormat var2 = this.mc.getSaveLoader();
                WorldInfo var3 = var2.getWorldInfo("Demo_World");

                if (var3 != null) {
                    GuiYesNo var4 = GuiSelectWorld.func_152129_a(this, var3.getWorldName(), 12);
                    this.mc.displayGuiScreen(var4);
                }
                break;
        }
    }


    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ResourceLocation clown = new ResourceLocation("meme/skull.png");

        ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        Gui.drawRect(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), new Color(50, 50, 50).getRGB());
        int var3 = this.height / 4 + 48 - 30;
        final CustomFontRenderer extreme = Client.INSTANCE.getFontManager().getFont("Extreme").getRenderer();
        final CustomFontRenderer large = Client.INSTANCE.getFontManager().getFont("Large").getRenderer();
        extreme.drawCenteredString("M\u00a7reme", (scaledResolution.getScaledWidth() / 2.0F), var3, ColorUtil.getModeColor());
        GuiUtils.drawImage(clown, (scaledResolution.getScaledWidth() / 2.0F) - 19, var3 - 60, 38, 38, -1); // old vals: var3 - 60, 38, 38 | christmas vals: var3 - 70, 38, 48
        String var11 = "Credits to: Aidan, Auth, Dort & Newb"; // old string: "Made by: Dort, Newb, Auth"
        String var12 = "Meme";
        large.drawString(var11, this.width - large.getWidth(var11) - 2, this.height - 14, -1);
        large.drawString(var12, 4, this.height - 14, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

}