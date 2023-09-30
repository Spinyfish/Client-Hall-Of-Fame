package igbt.astolfy.ui.inGame.GuiElement;

import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.module.visuals.Hud;
import igbt.astolfy.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class GuiElement {

    public ModuleBase parent;
    private int x;
    private int y;
    private double width;
    private double height;
    private boolean dragging;
    private double offsetX;
    private double offsetY;
    private int offBasedX = 0;
    private int offBasedY = 0;
    private Boolean hidden = false;


    public GuiElement(String displayName, ModuleBase parent,int x, int y, double width, double height){
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        GuiElementManager.addElement(this);
    }

    public GuiElement(String displayName, int x, int y, double width, double height, int offBasedX, int offBasedY){
        this.offBasedX = offBasedX;
        this.offBasedY = offBasedY;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        GuiElementManager.addElement(this);
    }

    public void renderStart(){
        GlStateManager.pushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtils.prepareScissorBox((float) x + offBasedX, (float) y + offBasedY , (float)(x + width + offBasedX), (float)(y + height + offBasedY));
        GlStateManager.translate(x,y,0);
    }
    public void renderEnd(){
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.translate(-x,-y,0);
        GlStateManager.popMatrix();
    }
    public void setWidth(double width){
        this.width = width;
    }

    public void setHeight(double height){
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean onMouseDown(int mouseX, int mouseY, int button){
        if(hidden) return false;
        if(RenderUtils.isHovered(mouseX,mouseY,x + offBasedX,y + offBasedY,x + width + offBasedX, y + height + offBasedY)){
            if(button == 0){
                offsetX = mouseX - x;
                offsetY = mouseY - y;
                dragging = true;
            }
            return true;
        }
        return false;
    }

    public void onMouseRelease(int mouseX, int mouseY, int button){
        dragging = false;
    }


    public boolean renderOverlay(int mouseX, int mouseY) {
        if(hidden) return false;
        if(dragging){
            x = mouseX - (int)offsetX;
            y = mouseY - (int)offsetY;
        }
        if(RenderUtils.isHovered(mouseX,mouseY,x + offBasedX,y + offBasedY,x + width + offBasedX, y + height + offBasedY)){
            RenderUtils.drawOutlineRect(x + offBasedX - 1,y + offBasedY - 1,x + width + offBasedX,y + height + offBasedY,1, Hud.getColor((int)y));
            Gui.drawRect(x + offBasedX,y + offBasedY,x +width +offBasedX,y + height + offBasedY,0x80000000);
            Minecraft.getMinecraft().customFont.drawString("X: "+ (x + offBasedX) + " Y: " + (y + offBasedY), (x + offBasedX), (y + height + 2 + offBasedY), -1);
            return true;
        }
        return false;
    }

    public int getOffBasedX() {
        return offBasedX;
    }

    public void setOffBasedX(int offBasedX) {
        this.offBasedX = offBasedX;
    }

    public int getOffBasedY() {
        return offBasedY;
    }

    public void setOffBasedY(int offBasedY) {
        this.offBasedY = offBasedY;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }
}
