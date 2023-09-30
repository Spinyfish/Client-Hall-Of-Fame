
package Reality.Realii.mods.modules.render;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import Reality.Realii.Client;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.rendering.EventRender2D;
import Reality.Realii.event.events.world.EventPostUpdate;
import Reality.Realii.event.value.Mode;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.event.value.Option;
import Reality.Realii.guis.font.CFontRenderer;
import Reality.Realii.guis.font.FontLoaders;
import Reality.Realii.guis.notification.NotificationsManager;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.mods.modules.render.UI.TabUI;
import Reality.Realii.mods.modules.render.hud.ArrayList;
import Reality.Realii.utils.math.AnimationUtils;
import Reality.Realii.utils.render.RenderUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import Reality.Realii.Client;
import Reality.Realii.guis.font.CFontRenderer;
import Reality.Realii.guis.font.FontLoaders;
import Reality.Realii.managers.ModuleManager;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.mods.modules.ClientSettings;
import Reality.Realii.mods.modules.render.HUD;
import Reality.Realii.utils.cheats.player.Helper;
import Reality.Realii.utils.cheats.world.TimerUtil;
import Reality.Realii.utils.render.RenderUtil;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;

import org.lwjgl.opengl.GL11;


public class HUD
        extends Module {
    public TabUI tabui;
    public static Option<Boolean> tabGui = new Option<Boolean>("TabGUI", "TabGUI", true);
    public static Option<Boolean> noRender = new Option<Boolean>("NoRender", "NoRender", true);
    public static Option<Boolean> Outline = new Option<Boolean>("Outline", "Outline", true);
    public static Option<Boolean> backround = new Option<Boolean>("Backround", "Backround", true);
  
    public static Mode USerinfp = new Mode("ShowUserInfo", "ShowUserInfo", new String[]{"True", "False"}, "True");
    public static Option<Boolean> notification = new Option<Boolean>("Notification", "Notification", true);
    public static Option<Boolean> arraylist = new Option<Boolean>("Arraylist", "Arraylist", true);
    public static Mode colorMode = new Mode("ArrayListColor", "ArrayListColor", new String[]{"ColoredRainbow", "Color", "Rainbow"}, "ColoredRainbow");
    public static Mode logoMode = new Mode("LogoMode", "LogoMode", new String[]{"Logo", "Text","Fulllogo","ImageLogo","ImageLogoGlow"}, "Logo");
    public static Mode fontvchanger = new Mode("ArrayListFont", "ArrayListFont", new String[]{"Vannila", "Sigma","Arial","Swanses","Thick"}, "Vannila");
    public static Mode fontvchanger12 = new Mode("WaterMarkFont", "WaterMarkFont", new String[]{"Vannila", "Sigma","Arial","Thick","Swanses"}, "Vannila");
    public static Mode tabuifont = new Mode("TabUiFont", "TabUiFont", new String[]{"Vannila", "Sigma","Arial","Thick","Swanses"}, "Vannila");
    public static Mode outlinemode = new Mode("OutlineMode", "OutlineMode", new String[]{"Normal", "Reality"}, "Reality");
    public static Mode bkcolor = new Mode("BkColor", "BkColor", new String[]{"Normal", "Client","Nigger"}, "Client");
    public static Numbers<Number>  capacity = new Numbers<>("BkCapacity", 45f, 5f, 255f, 100f);
  
    
    

    public Mode mod = new Mode("Mode", "Mode", new String[]{"Reality"}, "Reality");
    
    private static ArrayList arrayList = new ArrayList();
    private AnimationUtils animationUtils = new AnimationUtils();

    public HUD() {
        super("HUD", ModuleType.Render);
        this.setEnabled(true);
        this.addValues(this.tabGui, this.notification, this.arraylist, this.mod, fontvchanger , colorMode, Outline, noRender, logoMode, fontvchanger12, tabuifont, backround, outlinemode, bkcolor, capacity, USerinfp);
    }

    int rainbowTick = 0;

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
    }

    float x;

    @EventHandler
    public void renderHud(EventRender2D event) {
        ScaledResolution sr = new ScaledResolution(mc);

        if (((boolean) notification.getValue())) {
            NotificationsManager.renderNotifications();
            NotificationsManager.update();
        }
        if (!mc.gameSettings.showDebugInfo) {
            arrayList.drawObject();
        }
        
        if (USerinfp.getModeAsString().equals("True")) {
         	Color c = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue());
         	

       	  FontLoaders.arial18.drawStringWithShadow(Client.CLIENT_NAME.substring(0, 1)  +  Client.CLIENT_NAME.substring(1, Client.CLIENT_NAME.length()) + " Client Nyghtfull (0069) {Beta DevBuilt} ", 3, 530, c.getRGB());

        }
        
        if (USerinfp.getModeAsString().equals("False")) {
         
        }
        
        if (logoMode.getModeAsString().equals("Logo")) {
            Color rainbow = new Color(Color.HSBtoRGB((float) ((double) this.mc.thePlayer.ticksExisted / 50.0 + Math.sin((double) rainbowTick / 50.0)) % 1.0f, 0.5f, 1.0f));

            if (!mc.gameSettings.showDebugInfo) {
                RenderUtil.drawCustomImageAlpha(10, 2, 41, 41, new ResourceLocation("client/onletterlogo.png"), new Color(255, 255, 255).getRGB(), rainbow.getBlue());
                TabUI.height = 42;
            }
        } else if (logoMode.getModeAsString().equals("Text")) {
            if (!mc.gameSettings.showDebugInfo) {
            	
            	if (fontvchanger12.getValue().equals("Arial")) {
            	Color c = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue());
                FontLoaders.arial18.drawStringWithShadow(Client.CLIENT_NAME.substring(0, 1)  + Client.CLIENT_NAME.substring(1, Client.CLIENT_NAME.length()) + " " + ChatFormatting.GRAY, 5, 5, c.getRGB());
                TabUI.height = 21;
            	}
            	
            	else if (this.fontvchanger12.getValue().equals("Sigma")) {

                 	Color c = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue());
                 	

                 	  FontLoaders.roboto16.drawStringWithShadow(Client.CLIENT_NAME.substring(0, 1)  +  Client.CLIENT_NAME.substring(1, Client.CLIENT_NAME.length()) + " ", 5, 5, c.getRGB());

                     TabUI.height = 21;
             		  }
            	
        		else if (this.fontvchanger12.getValue().equals("Vannila")) {
        			FontRenderer fontv2 = Minecraft.getMinecraft().fontRendererObj;

                  	Color c = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue());
                  	
                  	

                  	fontv2.drawStringWithShadow(Client.CLIENT_NAME.substring(0, 1)  +  Client.CLIENT_NAME.substring(1, Client.CLIENT_NAME.length()) + " ", 5, 5, c.getRGB());
                      TabUI.height = 21;
        		 }
            	
            	
            	else if (this.fontvchanger12.getValue().equals("Thick")) {

                 	Color c = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue());
                 	

                 	  FontLoaders.arial16B.drawStringWithShadow(Client.CLIENT_NAME.substring(0, 1)  +  Client.CLIENT_NAME.substring(1, Client.CLIENT_NAME.length()) + " ", 5, 5, c.getRGB());

                     TabUI.height = 21;
             		  }
            	
            	
            	
            	

            	
            	
            	
            	
            }
            
            } else if (logoMode.getModeAsString().equals("Fulllogo")) {
                Color rainbow = new Color(Color.HSBtoRGB((float) ((double) this.mc.thePlayer.ticksExisted / 50.0 + Math.sin((double) rainbowTick / 50.0)) % 1.0f, 0.5f, 1.0f));

                if (!mc.gameSettings.showDebugInfo) {
                    RenderUtil.drawCustomImageAlpha(1, 3, 80, 20, new ResourceLocation("client/hudlogo.png"), new Color(255, 255, 255).getRGB(), rainbow.getBlue());
                    TabUI.height = 26;
                }
                      
                
    } else if (logoMode.getModeAsString().equals("ImageLogo")) {
        Color rainbow = new Color(Color.HSBtoRGB((float) ((double) this.mc.thePlayer.ticksExisted / 50.0 + Math.sin((double) rainbowTick / 50.0)) % 1.0f, 0.5f, 1.0f));

        if (!mc.gameSettings.showDebugInfo) {
            RenderUtil.drawCustomImageAlpha(1, 3, 80, 80, new ResourceLocation("client/Coollogo.png"), new Color(255, 255, 255).getRGB(), rainbow.getBlue());
            TabUI.height = 26;
        }
            
 else if (logoMode.getModeAsString().equals("ImageLogoGlow")) {
   

    if (!mc.gameSettings.showDebugInfo) {
        RenderUtil.drawCustomImageAlpha(1, 3, 80, 80, new ResourceLocation("client/ColllogoGlow.png"), new Color(255, 255, 255).getRGB(), rainbow.getBlue());
        TabUI.height = 26;
    }
          
    }
            
        
        if (mod.getValue().equals("Reality")) {
            double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            double moveSpeed = Math.sqrt(xDist * xDist + zDist * zDist) * 20;
            String text = (EnumChatFormatting.GRAY) + "FPS" + (Object) ((Object) EnumChatFormatting.WHITE) + ": " + Minecraft.debugFPS + "  " + Math.round(moveSpeed) + " \2477bps\247r";
            
            Client.fontLoaders.msFont18.drawStringWithShadow(text, 4.0F, new ScaledResolution(mc).getScaledHeight() - 10, new Color(11, 12, 17).getRGB());
            drawPotionStatus(sr);
        } else if (mod.getValue().equals("OverWatch")) {
            GlStateManager.rotate(-15, 1, 1, 1);
            FontLoaders.arial24.drawString((int) mc.thePlayer.getHealth() + "/" + (int) mc.thePlayer.getMaxHealth(), 10, sr.getScaledHeight() - 75, -1);
            for (int i = 0; i < 20; i++) {
                if (i > ((int) mc.thePlayer.getHealth())) {
                    RenderUtil.drawRect(i * 9, sr.getScaledHeight() - 60, (i % 2 == 0 ? 9 : 8) + i * 9, sr.getScaledHeight() - 43, new Color(255, 255, 255, 170));
                }
            }

            for (int i = 0; i < 20; i++) {
                if (i <= ((int) mc.thePlayer.getHealth())) {
                    RenderUtil.drawRect(i * 9, sr.getScaledHeight() - 60, (i % 2 == 0 ? 9 : 8) + i * 9, sr.getScaledHeight() - 43, new Color(255, 255, 255, 230));
                }
            }

            RenderUtil.drawRect(0, sr.getScaledHeight() - 40, 5 + 7 * 18, sr.getScaledHeight() - 38.5f, new Color(255, 255, 255, 180));
            RenderUtil.drawRect(0, sr.getScaledHeight() - 40, (5 + 7 * 18) * ((float) mc.thePlayer.getTotalArmorValue() / 20f), sr.getScaledHeight() - 38.5f, new Color(71, 142, 255, 255));


            GlStateManager.rotate(15, 1, 1, 1);

        }
    }
    }

    @EventHandler
    public void onUpdate(EventPostUpdate e) {
        if (rainbowTick++ > 50) {
            rainbowTick = 0;
        }


    }

    private void drawPotionStatus(ScaledResolution sr) {
        CFontRenderer font = FontLoaders.arial16;
        int y = -5;
        for (PotionEffect effect : this.mc.thePlayer.getActivePotionEffects()) {
            int ychat;
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            String PType = I18n.format(potion.getName(), new Object[0]);
            switch (effect.getAmplifier()) {
                case 1: {
                    PType = String.valueOf(PType) + " II";
                    break;
                }
                case 2: {
                    PType = String.valueOf(PType) + " III";
                    break;
                }
                case 3: {
                    PType = String.valueOf(PType) + " IV";
                    break;
                }
            }
            if (effect.getDuration() < 600 && effect.getDuration() > 300) {
                PType = String.valueOf(PType) + "\u00a77:\u00a76 " + Potion.getDurationString(effect);
            } else if (effect.getDuration() < 300) {
                PType = String.valueOf(PType) + "\u00a77:\u00a7c " + Potion.getDurationString(effect);
            } else if (effect.getDuration() > 600) {
                PType = String.valueOf(PType) + "\u00a77:\u00a77 " + Potion.getDurationString(effect);
            }
            font.drawStringWithShadow(PType, sr.getScaledWidth() - font.getStringWidth(PType) - 2, sr.getScaledHeight() - font.getHeight() + y, potion.getLiquidColor());
            y -= 12;
        }
    }

}

