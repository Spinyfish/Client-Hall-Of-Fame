package rip.athena.client.modules.impl.render;

import rip.athena.client.modules.*;

public class Fullbright extends Module
{
    private float savedBrightness;
    
    public Fullbright() {
        super("Fullbright", Category.RENDER);
        this.savedBrightness = 0.0f;
    }
    
    @Override
    public void onEnable() {
        this.savedBrightness = Fullbright.mc.gameSettings.gammaSetting;
        Fullbright.mc.gameSettings.gammaSetting = 1000.0f;
        Fullbright.mc.gameSettings.saveOptions();
        Fullbright.mc.gameSettings.saveOfOptions();
        super.onEnable();
    }
    
    @Override
    public void onDisable() {
        Fullbright.mc.gameSettings.gammaSetting = this.savedBrightness;
        Fullbright.mc.gameSettings.saveOptions();
        Fullbright.mc.gameSettings.saveOfOptions();
        super.onDisable();
    }
}
