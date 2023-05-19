package club.marsh.bloom.api.module;

import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.Bloom;
import club.marsh.bloom.api.value.ModeValue;
import net.minecraft.client.Minecraft;

public class Mode {
	public Minecraft mc = Minecraft.getMinecraft();
	public Module original;
	public boolean enabled = false;
	public String name;
	public ModeValue mode;
	public Mode(Module original, String name, Value mode) {
		this.original = original;
		this.name = name;
		this.mode = (ModeValue) mode;
	}
	public void registerValues(Value... values) {
		for (Value value : values) {
			Bloom.INSTANCE.valueManager.values.get(original.getName()).add(value);
		}
	}
	public boolean canBeUsed() {return this.name.toLowerCase().replace(" ", "").endsWith(mode.getMode().toLowerCase().replace(" ", ""));};
	public void onEnable() { enabled = true; };
	public void onDisable() { enabled = false; };
}
