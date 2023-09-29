package de.fanta.module.impl.visual;

import java.awt.Color;

import de.fanta.Client;
import de.fanta.events.Event;
import de.fanta.events.listeners.PlayerMoveEvent;
import de.fanta.module.Module;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.DropdownBox;

public class BlockHit extends Module {
    public static BlockHit INSTANCE;
	public BlockHit() {
	
		super("BlockHit", 0, Type.Visual, Color.magenta);
		this.settings.add(new Setting("AnimationMode", new DropdownBox("Fanta", new String[] { "Fanta", "Drop", "Astolfo","Centaurus","Exhibition", "Flux", "Sigma4","Violence"})));
		
	}

	@Override
	public void onEvent(Event event) {
	}
}
