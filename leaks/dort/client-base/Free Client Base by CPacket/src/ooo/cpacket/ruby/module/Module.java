package ooo.cpacket.ruby.module;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import ooo.cpacket.lemongui.settings.Setting;
import ooo.cpacket.ruby.ClientBase;
import ooo.cpacket.ruby.api.event.EventManager;

public abstract class Module {

	private String name;

	protected boolean enabled = false;

	private Category category;

	private int key;

	protected static Minecraft mc = Minecraft.getMinecraft();

	public Module(String name, int key, Category category) {
		this.name = name;
		this.category = category;
		this.key = key;
	}

	public String getName() {
		return this.name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public Category getCategory() {
		return category;
	}

	public int getKey() {
		return key;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public enum Category {
		ATTACK, SELF, MOVE, MISC, VISUAL, MEMES, HIDDEN, WORLD;
	}

	public abstract void onEnable();

	public abstract void onDisable();

	public void toggle() {
		this.enabled = !this.enabled;
		if (this.enabled) {
			EventManager.register(this);
			this.onEnable();
		}
		if (!this.enabled) {
			EventManager.unregister(this);
			this.onDisable();
		}
	}

	public String getSuffixedName() {
		if (ClientBase.INSTANCE.setmgr.getSettingByName("Mode", this) != null) {
			return this.name + " \u00A77- " + ClientBase.INSTANCE.setmgr.getSettingByName("Mode", this).getValString();
		} else {
			return this.getName();
		}
	}

	public void addBool(String string, boolean b) {
		ClientBase.INSTANCE.setmgr.rSetting(new Setting(string, this, b));
	}

	public void addModes(String... XDD) {
		ArrayList<String> XD = new ArrayList();
		for (String XDDD : XDD) {
			XD.add(XDDD);
		}
		ClientBase.INSTANCE.setmgr.rSetting(new Setting("Mode", this, XDD[0], XD));
	}
	public void addNumberOption(String string, double d, double e, double f) {
		ClientBase.INSTANCE.setmgr.rSetting(new Setting(string, this, d, e, f, false));
	}

	public void addNumberOption(String string, double d, double e, double f, boolean xd) {
		ClientBase.INSTANCE.setmgr.rSetting(new Setting(string, this, d, e, f, xd));
	}

	public boolean getBool(String string) {
		return ClientBase.INSTANCE.setmgr.getSettingByName(string, this).getValBoolean();
	}

	public double getDouble(String string) {
		return ClientBase.INSTANCE.setmgr.getSettingByName(string, this).getValDouble();
	}

	protected String getString(String string) {
		return ClientBase.INSTANCE.setmgr.getSettingByName(string, this).getValString();
	}

	protected boolean isMode(String xd) {
		return this.getString("Mode").equalsIgnoreCase(xd);
	}

}
