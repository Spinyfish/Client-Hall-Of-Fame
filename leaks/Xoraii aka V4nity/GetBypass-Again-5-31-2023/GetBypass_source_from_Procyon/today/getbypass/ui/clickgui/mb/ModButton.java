// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.ui.clickgui.mb;

import today.getbypass.ui.clickgui.CurrentScreen;
import today.getbypass.utils.RoundedUtils;
import java.awt.Color;
import today.getbypass.ui.clickgui.ClickGUI;
import today.getbypass.module.Module;

public class ModButton
{
    public Module currentModule;
    public int x;
    public int y;
    public int w;
    public int h;
    public int uFY;
    public int offsetFactor;
    public ClickGUI guiInstance;
    
    public ModButton(final int offsetFactor, final int x, final int y, final int w, final int h, final Module module, final ClickGUI guiInstance) {
        this.x = x;
        this.y = y + offsetFactor;
        this.uFY = y;
        this.w = w;
        this.h = h;
        this.offsetFactor = offsetFactor;
        this.currentModule = module;
        this.guiInstance = guiInstance;
    }
    
    public int getTextColorBasedOnModuleStatus() {
        if (this.currentModule.isToggled()) {
            return Color.green.getRGB();
        }
        return Color.red.getRGB();
    }
    
    public void drawButton(final int mouseX, final int mouseY) {
        final boolean isMouseOnButton = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.w && mouseY < this.y + this.h;
        RoundedUtils.drawRoundedRect((float)this.x, (float)this.y, (float)(this.x + this.w), (float)(this.y + this.h), 4.0f, isMouseOnButton ? new Color(40, 40, 40).getRGB() : new Color(30, 30, 30).getRGB());
        this.currentModule.getNameRen().drawString(this.currentModule.getName(), (float)(this.x + 5), (float)(this.y + 2), Color.white);
        this.currentModule.getDescRen().drawString(this.currentModule.getDesc(), (float)(this.x + 6), (float)(this.y + 22), Color.white);
        RoundedUtils.drawRoundedRect((float)(this.x + 495), (float)(this.y + 4), (float)(this.x + 495 + 31), (float)(this.y + 4 + 31), 8.0f, this.currentModule.isToggled() ? Color.green.getRGB() : Color.red.getRGB());
    }
    
    public void mouseClick(final int mouseX, final int mouseY, final int mouseButton) {
        final boolean isClickOnButton = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.w && mouseY < this.y + this.h;
        if (isClickOnButton & mouseButton == 0) {
            this.currentModule.toggle();
        }
        else if (mouseButton == 1 && isClickOnButton) {
            this.guiInstance.currentModule = this.currentModule;
            this.guiInstance.SCREEN = CurrentScreen.BASIC_SETTINGS_SCREEN;
        }
    }
}
