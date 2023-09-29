package de.fanta.module.impl.visual;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.StringUtils;
import org.lwjgl.input.Keyboard;

import de.fanta.events.Event;
import de.fanta.events.listeners.EventRender2D;
import de.fanta.module.Module;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.setting.settings.Slider;
import de.fanta.setting.settings.TextField;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Crosshair extends Module {

    public Crosshair() {
        super("Crosshair", 0, Type.Visual, Color.pink);

        this.settings.add(new Setting("Alpha", new Slider(0, 255, 1, 50)));
       // this.settings.add(new Setting("RGB", new TextField(true)));

    }
    private final Map<String, Float> cachedStringWidth = new HashMap<>();
    //Later Update: Customizeable

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventRender2D && event.isPre()){
            ScaledResolution sr = new ScaledResolution(mc);
            Color red = new Color(0, 255, 0, (int) ((Slider)this.getSetting("Alpha").getSetting()).curValue);

            Gui.drawRect(sr.getScaledWidth() / 2 - 5, sr.getScaledHeight() / 2, sr.getScaledWidth() / 2 - 1, sr.getScaledHeight() / 2 + 1.15F, red.getRGB());
            Gui.drawRect(sr.getScaledWidth() / 2 + 3, sr.getScaledHeight() / 2, sr.getScaledWidth() / 2 + 7, sr.getScaledHeight() / 2 + 1.15F, red.getRGB());
            Gui.drawRect(sr.getScaledWidth() / 2 + 0.5F, sr.getScaledHeight() / 2 + 2, sr.getScaledWidth() / 2 + 1.5F, sr.getScaledHeight() / 2 + 6, red.getRGB());
            Gui.drawRect(sr.getScaledWidth() / 2 + 0.5F, sr.getScaledHeight() / 2 - 1, sr.getScaledWidth() / 2 + 1.5F, sr.getScaledHeight() / 2 - 5, red.getRGB());

        }

    }
}
