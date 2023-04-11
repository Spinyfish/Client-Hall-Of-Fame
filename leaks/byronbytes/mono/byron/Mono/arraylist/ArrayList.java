package byron.Mono.arraylist;

import java.awt.Color;

import byron.Mono.Mono;
import byron.Mono.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class ArrayList
{
    private int posX, posY, width, height;
    
    public ArrayList()
    {
        posX = getScaledResolution().getScaledWidth() / 50;
        posY = getScaledResolution().getScaledHeight() / 50;
        width = posX + 100;
        height = posY + 100;
        
        
    }
    double offset;
    int offs2;
    	
    public void renderArrayList ()
    {
        int offset = 0;
        
        for (Module m : Mono.INSTANCE.getModuleManager().getModules())
        {
            
            
        }
        
        Gui.drawRect(posX, posY, width + 100, height + 100, new Color(250, 250, 250, 200).getRGB());

    }

    public ScaledResolution getScaledResolution ()
    {
        return new ScaledResolution(Minecraft.getMinecraft());
    }

}
