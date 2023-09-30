package Reality.Realii.mods.modules;

import Reality.Realii.event.value.Numbers;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;

// TODO: 2021/7/5 æŠŠè¿™ä¸ªmoduleçš„ç±»åž‹æ”¹ä¸ºæ— ç±»åž‹å¹¶æ˜¾ç¤ºåœ¨clickgui settingsç•Œé�¢ä¸­
public class ClientSettings extends Module {
    public static Numbers<Number> r = new Numbers<>("R", "R", 0, 0, 255, 1);
    public static Numbers<Number> g = new Numbers<>("G", "G", 120, 0, 255, 1);
    public static Numbers<Number> b = new Numbers<>("B", "B", 255, 0, 255, 1);
    public static Numbers<Number> r2 = new Numbers<>("R2 ", "R2 ", 0, 0, 255, 1);
    public static Numbers<Number> g2 = new Numbers<>("G2 ", "G2 ", 120, 0, 255, 1);
    public static Numbers<Number> b2 = new Numbers<>("B2 ", "B2 ", 255, 0, 255, 1);
    public static Numbers<Number> saturation = new Numbers<>("saturation", "saturation", 0.5, 0.1, 3, 0.01);
    public static Numbers<Number> brightness = new Numbers<>("brightness", "brightness", 0.5, 0.1, 1, 0.01);
    


    public ClientSettings() {
        super("ClientSettings", ModuleType.Misc);
        addValues(r, g, b, saturation, brightness, r2, g2 , b2);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.setEnabled(false);
    }
}
