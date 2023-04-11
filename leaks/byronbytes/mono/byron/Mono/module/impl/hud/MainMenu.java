package byron.Mono.module.impl.hud;



import java.awt.Color;

import byron.Mono.accounts.GuiAltManager;
import byron.Mono.font.FontUtil;
import byron.Mono.interfaces.HUD;
import byron.Mono.utils.ColorUtils;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class MainMenu extends GuiScreen {


    public MainMenu()
    {

    }

    public void initGui() {

    }


    public void drawScreen(int mouseX, int mouseY, float particalTicks) {
        this.mc.getTextureManager().bindTexture(new ResourceLocation("Mono/background.jpg"));
        drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, this.width, this.height, (float)this.width, (float)this.height);
        this.drawGradientRect(0, this.height - 100, this.width, this.height, 0, -15777216);
        String[] buttons = new String[]{"Singleplayer", "Multiplayer", "Settings", "AltManager", "Quit"};
        int count = 0;
        String[] var9 = buttons;
        int var8 = buttons.length;

        for(int var7 = 0; var7 < var8; ++var7) {
            String name = var9[var7];
            float x = (float)(this.width / buttons.length * count) + (float)(this.width / buttons.length) / 2.0F + 8.0F - (float)FontUtil.normal.getStringWidth(name) / 2.0F;
            float y = (float)(this.height - 20);
            boolean hovered = (float)mouseX >= x && (float)mouseY >= y && (float)mouseX < x + (float)FontUtil.normal.getStringWidth(name) && (float)mouseY < y + (float)FontUtil.normal.getHeight();
            
            FontUtil.normal2.drawCenteredString(name, (int) ((float)(this.width / buttons.length * count) + (float)(this.width / buttons.length) / 2.0F + 8.0F), (int) (this.height - 20), hovered ? ColorUtils.astolfoColors(count * 10, 1000) : -1);
            ++count;
        }

        
        String s2 = "";	
        FontUtil.normal3.drawStringWithShadow("Changelog", 4, 4, ColorUtils.astolfoColors(count * 10 , 1000));
        ++count;
        this.drawString(fontRendererObj, "[~] Updated Verus Fly bypass", 4, 30, ColorUtils.astolfoColors(count * 10 , 1000));
        ++count;
        this.drawString(fontRendererObj, "[~] Updated Astolfo Rainbow  ", 4, 40, ColorUtils.astolfoColors(count * 10 , 1000));
        ++count;
        this.drawString(fontRendererObj, "[+] Added authentication", 4, 50, ColorUtils.astolfoColors(count * 10 , 1000));
        ++count;
        this.drawString(fontRendererObj, "[~] Overhauled main menu", 4, 60, ColorUtils.astolfoColors(count * 10 , 1000));
        ++count;
        this.drawString(fontRendererObj, "[~] Updated Arraylist ", 4, 70, ColorUtils.astolfoColors(count * 10 , 1000));
        ++count;
        this.drawString(fontRendererObj, "[+] Added ColorUtils", 4, 80, ColorUtils.astolfoColors(count * 10 , 1000));
        ++count;
        this.drawString(fontRendererObj, "[+] ", 4, 90, ColorUtils.astolfoColors(count * 10 , 1000));
        ++count;
        this.drawString(fontRendererObj, "[~] Updated DamageFly", 4, 100, ColorUtils.astolfoColors(count * 10 , 1000));
        ++count;
        this.drawString(fontRendererObj, "[~] Fixed scaffold bug", 4, 110, ColorUtils.astolfoColors(count * 10 , 1000));
        ++count;
        this.drawString(fontRendererObj, "[+] Added Nametags", 4, 120, ColorUtils.astolfoColors(count * 10 , 1000));
        ++count;
        this.drawString(fontRendererObj, "[~] Fixed Fullbright", 4, 130, ColorUtils.astolfoColors(count * 10 , 1000));
        ++count;
        this.drawString(fontRendererObj, "[~] Updated Targetstrafe", 4, 140, ColorUtils.astolfoColors(count * 10 , 1000));
        ++count;
        this.drawString(fontRendererObj, "[+] Added WTap", 4, 150, ColorUtils.astolfoColors(count * 10 , 1000));
        ++count;
        this.drawString(fontRendererObj, "[+] Added Criticals", 4, 160, ColorUtils.astolfoColors(count * 10 , 1000));
        ++count;
        this.drawString(fontRendererObj, "", 4, 155, ColorUtils.astolfoColors(count * 10 , 1000));
        ++count;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)this.width / 2.0F, (float)this.height / 2.0F, 0.0F);
        GlStateManager.scale(3.0F, 3.0F, 1.0F);
        GlStateManager.translate(-((float)this.width / 2.0F), -((float)this.height / 2.0F), 0.0F);
        FontUtil.normal3.drawCenteredString("Mono", (int) this.width / 2.0F, (int)this.height / 2.0F - (int)FontUtil.normal3.getHeight() / 2.0F, ColorUtils.astolfoColors(1000, 10));
      
        GlStateManager.popMatrix();
    }
    
    public static int astolfoColors(int yOffset, int yTotal) {
        float speed = 1900.0F;

        float hue;
        for(hue = (float)(System.currentTimeMillis() % (long)((int)speed)) + (float)((yTotal - yOffset) * 9); hue > speed; hue -= speed) {
        }

        hue /= speed;
        if ((double)hue > 1.5D) {
            hue = 0.7F - (hue - 1.0F);
        }

        ++hue;
        return Color.HSBtoRGB(hue, 0.4F, 1.0F);
    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        String[] buttons = new String[]{"Singleplayer", "Multiplayer", "Settings", "AltManager", "Quit"};
        int count = 0;
        String[] var9 = buttons;
        int var8 = buttons.length;

        for(int var7 = 0; var7 < var8; ++var7) {
            String name = var9[var7];
            float x = (float)(this.width / buttons.length * count) + (float)(this.width / buttons.length) / 2.0F + 8.0F - (float)FontUtil.normal.getStringWidth(name) / 2.0F;
            float y = (float)(this.height - 20);
            if ((float)mouseX >= x && (float)mouseY >= y && (float)mouseX < x + (float)FontUtil.normal.getStringWidth(name) && (float)mouseY < y + (float)FontUtil.normal.getHeight()) {
                switch(name) {
                    case "Multiplayer":
                        if (name.equals("Multiplayer")) {
                            this.mc.displayGuiScreen(new GuiMultiplayer(this));
                        }
                        break;
                    case "Singleplayer":
                        if (name.equals("Singleplayer")) {
                            this.mc.displayGuiScreen(new GuiSelectWorld(this));
                        }
                        break;
                    case "Quit":
                        if (name.equals("Quit")) {
                            this.mc.shutdown();
                        }
                        break;
                    case "Settings":
                        if (name.equals("Settings")) {
                            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                        }

                    case "AltManager":
                        if(name.equals("AltManager"))
                        {
                            this.mc.displayGuiScreen(new GuiAltManager());
                        }
                }
            }

            ++count;
        }

    }

}
