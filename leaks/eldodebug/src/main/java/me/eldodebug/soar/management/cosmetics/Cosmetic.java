package me.eldodebug.soar.management.cosmetics;

import me.eldodebug.soar.utils.animation.simple.SimpleAnimation;
import net.minecraft.util.ResourceLocation;

public class Cosmetic {

	private String name;
	
	private ResourceLocation sample, cape;
	
	public SimpleAnimation opacityAnimation = new SimpleAnimation(0.0F);
	
	public Cosmetic(String name, ResourceLocation sample, ResourceLocation cape) {
		this.name = name;
		this.sample = sample;
		this.cape = cape;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ResourceLocation getSample() {
		return sample;
	}

	public void setSample(ResourceLocation sample) {
		this.sample = sample;
	}

	public ResourceLocation getCape() {
		return cape;
	}

	public void setCape(ResourceLocation cape) {
		this.cape = cape;
	}
}