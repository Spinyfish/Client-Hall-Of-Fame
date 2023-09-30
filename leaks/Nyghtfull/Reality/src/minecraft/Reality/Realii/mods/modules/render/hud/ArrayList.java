package Reality.Realii.mods.modules.render.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import Reality.Realii.Client;
import Reality.Realii.event.value.Mode;
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

public class ArrayList {
	
    private int rainbowTick;
    private int rainbowTick2;
    private TimerUtil timer = new TimerUtil();
   

    public void drawObject() {
        if (!HUD.arraylist.getValue()) {
            return;
        }
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        float x1 = sr.getScaledWidth(), y1 = 0;
        FontRenderer fontv2 = Minecraft.getMinecraft().fontRendererObj;
        CFontRenderer fontv3 = FontLoaders.roboto16;
        CFontRenderer fontv4 = FontLoaders.arial18;
     
        CFontRenderer fontv6 = FontLoaders.arial16B;
       
        
       
            if (HUD.fontvchanger.getValue().equals("Vannila")) {
        	
            ModuleManager.modules.sort(((o1, o2) -> fontv2.getStringWidth(o2.getSuffix().isEmpty() ? o2.getName() : String.format("%s %s", o2.getName(), o2.getSuffix())) - fontv2.getStringWidth(o1.getSuffix().isEmpty() ? o1.getName() : String.format("%s %s", o1.getName(), o1.getSuffix()))));
            }
             if (HUD.fontvchanger.getValue().equals("Sigma")) {
        	
            ModuleManager.modules.sort(((o1, o2) -> fontv3.getStringWidth(o2.getSuffix().isEmpty() ? o2.getName() : String.format("%s %s", o2.getName(), o2.getSuffix())) - fontv3.getStringWidth(o1.getSuffix().isEmpty() ? o1.getName() : String.format("%s %s", o1.getName(), o1.getSuffix()))));
            }
             if (HUD.fontvchanger.getValue().equals("Arial")) {
             	
            ModuleManager.modules.sort(((o1, o2) -> fontv4.getStringWidth(o2.getSuffix().isEmpty() ? o2.getName() : String.format("%s %s", o2.getName(), o2.getSuffix())) - fontv4.getStringWidth(o1.getSuffix().isEmpty() ? o1.getName() : String.format("%s %s", o1.getName(), o1.getSuffix()))));
            }
             
             
             if (HUD.fontvchanger.getValue().equals("Thick")) {
               	
            ModuleManager.modules.sort(((o1, o2) -> fontv6.getStringWidth(o2.getSuffix().isEmpty() ? o2.getName() : String.format("%s %s", o2.getName(), o2.getSuffix())) - fontv6.getStringWidth(o1.getSuffix().isEmpty() ? o1.getName() : String.format("%s %s", o1.getName(), o1.getSuffix()))));
            }
             
             
        

        rainbowTick = 0;
        rainbowTick2 = 0;

        java.util.ArrayList<Module> mods = new java.util.ArrayList<>();
        for (Module m : ModuleManager.modules) {
            if (m.isEnabled()) {
                mods.add(m);
            } else {
                m.animX = x1;
            }
        }

        float ys = y1;
        if (timer.delay(10)) {
            for (Module mod : mods) {
                mod.animY = mod.animationUtils.animate(ys, mod.animY, 0.15f);
                if (HUD.fontvchanger.getValue().equals("Vannila")) {
                	
                
                mod.animX = mod.animationUtils2.animate(x1 - (fontv2.getStringWidth(mod.getName() + (mod.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + mod.getSuffix()) - 4), mod.animX, 0.15f);
                }
                if (HUD.fontvchanger.getValue().equals("Sigma")) {
                	 mod.animX = mod.animationUtils2.animate(x1 - (fontv3.getStringWidth(mod.getName() + (mod.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + mod.getSuffix()) - 4), mod.animX, 0.15f);
                	
                }
                
                if (HUD.fontvchanger.getValue().equals("Arial")) {
               	 mod.animX = mod.animationUtils2.animate(x1 - (fontv4.getStringWidth(mod.getName() + (mod.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + mod.getSuffix()) - 4), mod.animX, 0.15f);
               	
               }
                
                
                
                if (HUD.fontvchanger.getValue().equals("Thick")) {
                  	 mod.animX = mod.animationUtils2.animate(x1 - (fontv6.getStringWidth(mod.getName() + (mod.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + mod.getSuffix()) - 4), mod.animX, 0.15f);
                  	
                  }
                ys += 12;
            }
            timer.reset();
        }

        float arrayListY = y1;
        int i = 0;
        for (Module mod : mods) {
            if (!mod.isEnabled())
                return;
            if (++rainbowTick2 > 50) {
                rainbowTick2 = 0;
            }
            Color arrayRainbow2 = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue());
            if (HUD.colorMode.getValue().equals("Rainbow")) {
                arrayRainbow2 = new Color(Color.HSBtoRGB((float) ((double) Client.instance.mc.thePlayer.ticksExisted / 50.0 + Math.sin((double) (rainbowTick + (arrayListY - 4) / 12) / 50.0 * 1.6)) % 1.0f, ClientSettings.saturation.getValue().floatValue(), ClientSettings.brightness.getValue().floatValue()));
            } else if (HUD.colorMode.getValue().equals("ColoredRainbow")) {
                Color temp = new Color(Color.HSBtoRGB((float) ((double) Client.instance.mc.thePlayer.ticksExisted / 50.0 + Math.sin((double) (rainbowTick + (arrayListY - 4) / 12) / 50.0 * 1.6)) % 1.0f, 0.5f, 1));
                arrayRainbow2 = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue(), temp.getRed());
            } else if (HUD.colorMode.getValue().equals("Color")) {
                arrayRainbow2 = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue());
            }
            

            if(HUD.Outline.getValue()) {
                if (i + 1 <= mods.size() - 1) {
                    Module m2 = mods.get(i + 1);
                    if (HUD.fontvchanger.getValue().equals("Vannila")) {
                    	
                    RenderUtil.drawRect((int) mod.animX - 10, ((float) mod.animY) + 11, (int) mod.animX - 9 + fontv2.getStringWidth(mod.getName() + (mod.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + mod.getSuffix()) - fontv2.getStringWidth(m2.getName() + (m2.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + m2.getSuffix()), (int) mod.animY + 12, arrayRainbow2.getRGB());
                    }
                    if (HUD.fontvchanger.getValue().equals("Sigma")) {
                    	RenderUtil.drawRect((int) mod.animX - 10, ((float) mod.animY) + 11, (int) mod.animX - 9 + fontv3.getStringWidth(mod.getName() + (mod.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + mod.getSuffix()) - fontv3.getStringWidth(m2.getName() + (m2.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + m2.getSuffix()), (int) mod.animY + 12, arrayRainbow2.getRGB());
                        
                    }
                    if (HUD.fontvchanger.getValue().equals("Arial")) {
                    	
                        RenderUtil.drawRect((int) mod.animX - 10, ((float) mod.animY) + 11, (int) mod.animX - 9 + fontv4.getStringWidth(mod.getName() + (mod.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + mod.getSuffix()) - fontv4.getStringWidth(m2.getName() + (m2.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + m2.getSuffix()), (int) mod.animY + 12, arrayRainbow2.getRGB());
                        }
                    
                    
                    if (HUD.fontvchanger.getValue().equals("Thick")) {
                    	
                        RenderUtil.drawRect((int) mod.animX - 10, ((float) mod.animY) + 11, (int) mod.animX - 9 + fontv6.getStringWidth(mod.getName() + (mod.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + mod.getSuffix()) - fontv6.getStringWidth(m2.getName() + (m2.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + m2.getSuffix()), (int) mod.animY + 12, arrayRainbow2.getRGB());
                        }
                } else if (i == mods.size() - 1) {

                	 
                     if (HUD.outlinemode.getValue().equals("Normal")) {
                    	 RenderUtil.drawRect((int) mod.animX - 10, ((float) mod.animY) + 11, x1, (int) mod.animY + 12, arrayRainbow2.getRGB());
                     	
                     	
                     }
                }
                
           	 if (HUD.outlinemode.getValue().equals("Reality")) {
           		RenderUtil.drawRect((int) mod.animX - 10, ((float) mod.animY), ((int) mod.animX - 9), (int) mod.animY + 11, arrayRainbow2.getRGB());
            	 
           }
                if (HUD.outlinemode.getValue().equals("Normal")) {
                	RenderUtil.drawRect((int) mod.animX - 10, ((float) mod.animY), ((int) mod.animX - 9), (int) mod.animY + 11, arrayRainbow2.getRGB());
                	
                }
                
               
            }
            
            
            if(HUD.backround.getValue()) {
            	if (HUD.bkcolor.getValue().equals("Client")) {
            	Color c = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue(), HUD.capacity.getValue().intValue());
                RenderUtil.drawRect((int) mod.animX - 10, ((float) mod.animY), (x1), (int) mod.animY + 12, c.getRGB());
            	} else if (HUD.bkcolor.getValue().equals("Normal")) {
            		Color c2 = new Color(50,50,50,HUD.capacity.getValue().intValue());
            		RenderUtil.drawRect((int) mod.animX - 10, ((float) mod.animY), (x1), (int) mod.animY + 12, c2.getRGB());
            		
            	} else if (HUD.bkcolor.getValue().equals("Nigger")) {
            		Color c3 = new Color(10,10,10,HUD.capacity.getValue().intValue());
            		RenderUtil.drawRect((int) mod.animX - 10, ((float) mod.animY), (x1), (int) mod.animY + 12, c3.getRGB());
            		
            	}
            	
                }
            
            if (HUD.fontvchanger.getValue().equals("Vannila")) {
            	
            
            fontv2.drawStringWithShadow(mod.getName() + (mod.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + mod.getSuffix(), mod.animX - 8, mod.animY + 3, arrayRainbow2.getRGB());
            arrayListY += 12f;
            i++;
            }
            if (HUD.fontvchanger.getValue().equals("Sigma")) {
            	
                
            fontv3.drawStringWithShadow(mod.getName() + (mod.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + mod.getSuffix(), mod.animX - 8, mod.animY + 3, arrayRainbow2.getRGB());
            arrayListY += 12f;
            i++;
            }
            
            if (HUD.fontvchanger.getValue().equals("Arial")) {
            	
                
            fontv4.drawStringWithShadow(mod.getName() + (mod.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + mod.getSuffix(), mod.animX - 8, mod.animY + 3, arrayRainbow2.getRGB());
            arrayListY += 12f;
            i++;
            }
            
            
            
            if (HUD.fontvchanger.getValue().equals("Thick")) {
            	
                
            fontv6.drawStringWithShadow(mod.getName() + (mod.getSuffix().isEmpty() ? "" : " ") + ChatFormatting.WHITE + mod.getSuffix(), mod.animX - 8, mod.animY + 3, arrayRainbow2.getRGB());
            arrayListY += 12f;
            i++;
            }
        }
        if (rainbowTick++ > 50) {
            rainbowTick = 0;
        }
    }
}
