package Reality.Realii.mods.modules.render;

import org.lwjgl.input.Keyboard;
import Reality.Realii.event.value.Mode;
import Reality.Realii.guis.clickgui.MainWindow;
import Reality.Realii.guis.clickgui2.TomoClickGui;
import Reality.Realii.guis.material.themes.Classic;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;


public class ClickGui extends Module {
    public Mode mode = new Mode("Mode", "Mode", new String[]{ "Material","Other"}, "Material");
    public Mode color = new Mode("Theme", "Theme", new String[]{"White", "Dark"}, "White");


    public ClickGui() {
        super("ClickGui", ModuleType.Render);
        this.setKey(Keyboard.KEY_RSHIFT);
        addValues(this.mode, this.color);
    
        
    }


    @Override
    public void onEnable() {
//        mc.displayGuiScreen(new REality.client.guis.clickgui.ClickGui());

        if (color.getValue().equals("Dark")) {
            
            } else if (mode.getValue().equals("Material")) {
               
                mc.displayGuiScreen(new Classic());
            }
            if (mode.getValue().equals("Other")) {
            	mc.displayGuiScreen(new MainWindow());
            }
       
             else if (mode.getValue().equals("Material")) {
                mc.displayGuiScreen(new Classic());
            }
            if (mode.getValue().equals("Other")) {
            	mc.displayGuiScreen(new MainWindow());
            }
        
        

        this.setEnabled(false);
    }
}
