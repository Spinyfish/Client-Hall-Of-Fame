package alos.stella.ui.clickgui.dropdown;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
public class ScaleUtils {
    public static int[] getScaledMouseCoordinates(Minecraft mc, int mouseX, int mouseY){
        int x = mouseX;
        int y = mouseY;
                x*=1;
                y*=1;
        return new int[]{x,y};
    }
    public static double[] getScaledMouseCoordinates(Minecraft mc, double mouseX, double mouseY){
        double x = mouseX;
        double y = mouseY;
        x*=1;
        y*=1;
        return new double[]{x,y};
    }

    public static void scale(Minecraft mc){

                GlStateManager.scale(1,1,1);


    }

}
