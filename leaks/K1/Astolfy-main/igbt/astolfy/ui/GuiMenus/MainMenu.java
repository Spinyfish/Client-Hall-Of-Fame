package igbt.astolfy.ui.GuiMenus;

import com.google.common.collect.Lists;
import igbt.astolfy.Astolfy;
import igbt.astolfy.utils.ColorUtils;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.List;

public class MainMenu extends GuiScreen
{
    protected List<GuiMenuButton> buttonList = Lists.newArrayList();
    protected List<GuiMiniButton> miniButtonList = Lists.newArrayList();

    public void initGui()
    {
        this.addButtons(this.height / 4 + 68);
        //this.mc.func_181537_a(false);
    }

    private void addButtons(int p_73969_1_)
    {
        this.miniButtonList.clear();
        this.buttonList.clear();
        this.buttonList.add(new GuiMenuButton(1, this.width /2 - 75, p_73969_1_, "SINGLEPLAYER"));
        this.buttonList.add(new GuiMenuButton(2, this.width / 2 - 75, p_73969_1_ + 24, "MULTIPLAYER"));
        this.buttonList.add(new GuiMenuButton(14, this.width / 2 - 75, p_73969_1_ + 24 * 2, "ALT MANAGER"));
        
        this.miniButtonList.add(new GuiMiniButton(4, width - 25, 5, "", new ResourceLocation("Images/MainMenu/Buttons/exit.png")));
        this.miniButtonList.add(new GuiMiniButton(0, width - 25 - 25, 5, "Options", new ResourceLocation("Images/MainMenu/Buttons/Options.png")));
        this.miniButtonList.add(new GuiMiniButton(5, width - 25 - 25 - mc.customFont.getStringWidth("Options") - 25 - 5, 5, "Language", new ResourceLocation("Images/MainMenu/Buttons/Language.png")));
    }
    protected void actionPerformed(GuiButton button) throws IOException
    {
        switch(button.id) {
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
                this.mc.displayGuiScreen(new GuiAltLogin(this));
                break;
            case 4:
                this.mc.shutdown();
            break;
        }
    }


    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.color(1,1,1);
        Gui.drawImage(mc,0,0,this.width, this.height, new ResourceLocation("Images/MainMenu/Background.png"));
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawImage(mc, width / 2 - 50, this.height / 4 - 30, 100, new ResourceLocation("Images/MainMenu/Icon.png"));
        GlStateManager.color(1, 1, 1,0.7f);
        Gui.drawImage(mc, this.width - 250, this.height - 150 - mc.customFont.getHeight() - 4, 250,150, new ResourceLocation("Images/MainMenu/AstolfoCon.png"));
        String title = Astolfy.i.clientName + " " + Astolfy.i.buildVersion;
        mc.customFont.drawString(title.substring(0,1), 4, this.height - 16 -  mc.customFont.getHeight(), ColorUtils.astolfo(height - 16 - mc.customFont.getHeight()));
        mc.customFont.drawString(title.substring(1), 4 + mc.customFont.getStringWidth(title.substring(0,1)), this.height - 12 -  mc.customFont.getHeight() - 4, -1);
        mc.customFont.drawString("Welcome To Astolfy", this.width - mc.customFont.getStringWidth("Welcome To Astolfy") - 4, this.height - 12 - mc.customFont.getHeight() - 2, -1);
        for(GuiMenuButton b : this.buttonList) {
        	b.drawButton(mc, mouseX, mouseY);
        }
        for(GuiMiniButton b : this.miniButtonList) {
        	b.drawButton(mc, mouseX, mouseY);
        }
        drawMOTD();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawMOTD() {
        Gui.drawRect(0,height - mc.customFont.getHeight() - 4, this.width, height, 0x80000000);
        mc.customFont.drawString("Welcome to Astolfy <3! This is a DEV build!", 5, this.height - mc.customFont.getHeight() - 3, ColorUtils.astolfo(this.height - 2));
    }
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        for(GuiMenuButton b : this.buttonList) {
        	if(b.mousePressed(mc, mouseX, mouseY)) {
        		actionPerformed(new GuiButton(b.id, 0, 0, ""));
        	}
        }
        for(GuiMiniButton b : this.miniButtonList) {
        	if(b.mousePressed(mc, mouseX, mouseY)) {
        		actionPerformed(new GuiButton(b.id, 0, 0, ""));
        	}
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
