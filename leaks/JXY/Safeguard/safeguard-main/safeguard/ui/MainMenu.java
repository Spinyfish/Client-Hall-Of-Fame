package intentions.ui;

import intentions.Client;
import intentions.modules.render.TabGUI;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class MainMenu extends GuiScreen {
  public void initGui() {}
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
	if(!TabGUI.openTabGUI) {
		mc.displayGuiScreen(null);
		mc.displayGuiScreen(new GuiMainMenu());
		return;
	}
    this.mc.getTextureManager().bindTexture(new ResourceLocation("SafeGuard/background.jpg"));
    drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, this.width, this.height, this.width, this.height);
    drawGradientRect(0, this.height - 100, this.width, this.height, 0, -16777216);
    String[] buttons = { "Singleplayer", "Multiplayer", "AltManager", "Settings", "Language", "Quit" };
    int count = 0;
    byte b;
    int i;
    String[] arrayOfString1;
    for (i = (arrayOfString1 = buttons).length, b = 0; b < i; ) {
      String name = arrayOfString1[b];
      float x = (this.width / buttons.length * count) + (this.width / buttons.length) / 2.0F - this.mc.fontRendererObj.getStringWidth(name) / 2.0F;
      float y = (this.height - 20);
      boolean hovered = (mouseX >= x && mouseY >= y && mouseX < x + this.mc.fontRendererObj.getStringWidth(name) && mouseY < y + this.mc.fontRendererObj.FONT_HEIGHT);
      drawCenteredString(this.mc.fontRendererObj, name, (this.width / buttons.length * count) + (this.width / buttons.length) / 2.0F, (this.height - 20), hovered ? -1862332160 : -1);
      count++;
      b++;
    } 
    GlStateManager.pushMatrix();
    GlStateManager.translate(this.width / 2.0F, this.height / 2.0F - this.mc.fontRendererObj.FONT_HEIGHT / 2.0F + 100.0F, 0.0F);
    GlStateManager.scale(2.0F, 2.0F, 1.0F);
    GlStateManager.translate(-(this.width / 2.0F), -(this.height / 2.0F - this.mc.fontRendererObj.FONT_HEIGHT / 2.0F + 100.0F), 0.0F);
    drawCenteredString(this.mc.fontRendererObj, Client.name, this.width / 2.0F, this.height / 2.0F - this.mc.fontRendererObj.FONT_HEIGHT / 2.0F, -1862332160);
    GlStateManager.popMatrix();
    drawCenteredString(this.mc.fontRendererObj, Client.version, this.width / 2.0F, this.height / 2.0F - this.mc.fontRendererObj.FONT_HEIGHT / 2.0F, -1862332160);
  }
  
  public void mouseClicked(int mouseX, int mouseY, int button) {
	if(!TabGUI.openTabGUI) return;
    String[] buttons = { "Singleplayer", "Multiplayer", "AltManager", "Settings", "Language", "Quit" };
    int count = 0;
    byte b;
    int i;
    String[] arrayOfString1;
    for (i = (arrayOfString1 = buttons).length, b = 0; b < i; ) {
      String name = arrayOfString1[b];
      float x = (this.width / buttons.length * count) + (this.width / buttons.length) / 2.0F - this.mc.fontRendererObj.getStringWidth(name) / 2.0F;
      float y = (this.height - 20);
      if (mouseX >= x && mouseY >= y && mouseX < x + this.mc.fontRendererObj.getStringWidth(name) && mouseY < y + this.mc.fontRendererObj.FONT_HEIGHT) {
        String str;
        switch ((str = name).hashCode()) {
          case -2064742086:
            if (!str.equals("Multiplayer"))
              break; 
            this.mc.displayGuiScreen((GuiScreen)new GuiMultiplayer(this));
            break;
          case -1548945544:
            if (!str.equals("Language"))
              break; 
            this.mc.displayGuiScreen((GuiScreen)new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
            break;
          case -1500504759:
            if (!str.equals("Singleplayer"))
              break; 
            this.mc.displayGuiScreen((GuiScreen)new GuiSelectWorld(this));
            break;
          case 2528879:
            if (!str.equals("Quit"))
              break; 
            this.mc.shutdown();
            break;
          case 982419940:
            if (!str.equals("AltManager"))
              break; 
            this.mc.displayGuiScreen(new GuiAltManager(this));
            break;
          case 1499275331:
            if (!str.equals("Settings"))
              break; 
            this.mc.displayGuiScreen((GuiScreen)new GuiOptions(this, this.mc.gameSettings));
            break;
        } 
      } 
      count++;
      b++;
    } 
  }
  
  public void onGuiClosed() {}
}
