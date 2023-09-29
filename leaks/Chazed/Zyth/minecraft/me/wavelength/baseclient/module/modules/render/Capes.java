package me.wavelength.baseclient.module.modules.render;

import me.wavelength.baseclient.event.events.PacketReceivedEvent;
import me.wavelength.baseclient.event.events.UpdateEvent;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.module.Module;

public class Capes extends Module {

	public Capes() {
		super("Capes", "Choose what cape you are using.", 0, Category.RENDER, AntiCheat.SIGMA, AntiCheat.HENTAI,AntiCheat.HENTAI1, AntiCheat.HENTAI2, AntiCheat.HENTAI3, AntiCheat.HENTAI4, AntiCheat.HENTAI5, AntiCheat.HENTAI6, AntiCheat.HENTAI7, AntiCheat.MOON, AntiCheat.ZYTH, AntiCheat.ZYTH2, AntiCheat.ZYTH3, AntiCheat.ZYTH4);
	}

	public static int cape;

	@Override
	public void setup() {
		this.cape = 1;
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onUpdate(UpdateEvent event) {

		if(this.antiCheat == AntiCheat.SIGMA) {
			cape = 1;
		}

		if(this.antiCheat == AntiCheat.HENTAI) {
			cape = 2;
		}

		if(this.antiCheat == AntiCheat.HENTAI1) {
			cape = 3;
		}

		if(this.antiCheat == AntiCheat.HENTAI2) {
			cape = 4;
		}

		if(this.antiCheat == AntiCheat.HENTAI3) {
			cape = 5;
		}

		if(this.antiCheat == AntiCheat.HENTAI4) {
			cape = 6;
		}

		if(this.antiCheat == AntiCheat.HENTAI5) {
			cape = 7;
		}

		if(this.antiCheat == AntiCheat.HENTAI6) {
			cape = 8;
		}

		if(this.antiCheat == AntiCheat.HENTAI7) {
			cape = 9;
		}

		if(this.antiCheat == AntiCheat.MOON) {
			cape = 10;
		}

		if(this.antiCheat == AntiCheat.ZYTH) {
			cape = 11;
		}

		if(this.antiCheat == AntiCheat.ZYTH2) {
			cape = 12;
		}
		
		if(this.antiCheat == AntiCheat.ZYTH3) {
			cape = 13;
		}
		
		if(this.antiCheat == AntiCheat.ZYTH4) {
			cape = 14;
		}

	}

	@Override
	public void onPacketReceived(PacketReceivedEvent event) {
	}

}