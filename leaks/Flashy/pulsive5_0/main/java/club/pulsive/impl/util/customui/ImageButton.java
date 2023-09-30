package club.pulsive.impl.util.customui;

import club.pulsive.api.minecraft.MinecraftUtil;
import club.pulsive.impl.util.client.Logger;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.secondary.RenderUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class ImageButton extends GuiButton implements MinecraftUtil {
    private int x, y, width, height;
    
    public ImageButton(int buttonId, int x, int y) {
        super(buttonId, x, y, "");
        this.x = x;
        this.y = y;
    }
    
    public void drawScreen(ResourceLocation image, int width, int height) {
        this.width = width;
        this.height = height;
        RenderUtils.drawBorderedRoundedRect(x, y, width + 1.5f, height + 1.5f, 0, 0.5f, new Color(40, 40, 40, 90).getRGB(), 0);
        glColor4f(1, 1, 1, 1);
        RenderUtil.drawImage(image, x, y, width, height);
    }
    
    public void mouseClicked(int mouseX, int mouseY, int buttonID){
        if(RenderUtil.isHovered(x, y, width, height, mouseX, mouseY)){
            switch (buttonID) {
                case 0: {
                    Logger.printSysLog("clicked");
                    break;
                }
                case 1:{
                    Logger.printSysLog("clicked 1");
                    break;
                }
            }
        }
    }
    
    
}
