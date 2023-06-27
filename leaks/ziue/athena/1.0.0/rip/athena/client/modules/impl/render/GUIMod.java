package rip.athena.client.modules.impl.render;

import rip.athena.client.gui.clickgui.*;
import rip.athena.client.gui.framework.*;
import rip.athena.client.modules.*;
import net.minecraft.client.gui.*;

public class GUIMod extends Module
{
    private int width;
    private int height;
    public IngameMenu menuImpl;
    private Menu menu;
    
    public GUIMod() {
        super("GUI", Category.HIDDEN);
        this.width = 1035;
        this.height = 525;
        this.setKeyBind(54);
        this.menu = new Menu("", this.width, this.height);
        this.menuImpl = new IngameMenu(this, this.menu);
    }
    
    @Override
    public void onEnable() {
        GUIMod.mc.displayGuiScreen(this.menuImpl);
        super.onEnable();
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
    }
}
