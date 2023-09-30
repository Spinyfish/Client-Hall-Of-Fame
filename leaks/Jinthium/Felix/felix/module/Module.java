package felix.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;

import felix.Client;
import felix.api.annotations.Handler;
import felix.events.Event;
import felix.events.player.EventKeyPress;
import felix.module.impl.visuals.HUD;
import felix.util.visual.Translate;
import felix.value.Value;
import felix.value.impl.ColorValue;
import net.minecraft.client.Minecraft;

/**
* @param main class for all modules in the client.
*/
public abstract class Module {
	
	private ModuleCategory category;
	private String modName, suffix;
	private int keybind;
	private boolean enabled, hidden;
    private List<Value> values = new ArrayList<>();
	
	protected Minecraft mc = Minecraft.getMinecraft();
	
	public Translate translate = new Translate(0f, 0f);
	
	public Module(String modName, int keybind, ModuleCategory category) {
		this.category = category;
		this.modName = modName;
		this.keybind = keybind;
		this.enabled = false;
	}
	
	public enum ModuleCategory {
		
		COMBAT, MOVEMENT, PLAYER, WORLD, VISUALS;
		
	}
	
	public void onEnable() {
        Client.INSTANCE.getEventManager().register(this);
    }

    public void onDisable() {
    	Client.INSTANCE.getEventManager().unregister(this);
    }
    
    public void onToggle() {
    	final HUD hud = (HUD) Client.INSTANCE.getModuleManager().getModule("hud");
    	if (hud.isEnabled() && hud.hasToggleSoundsEnabled() && mc.thePlayer != null) {
    		mc.thePlayer.playSound("random.click", 0.15F, enabled ? 0.7F : 0.6F);
    	}
    }
    
    public boolean isEnabled() {
    	return enabled;
    }
    
    public void toggle() {
    	
    	enabled = !enabled;
    	onToggle();
    	if (enabled)
    		onEnable();
    	else
    		onDisable();
    }
    
    public int getKey() {
	    return keybind;
    }
    
    public String getName() {
    	return modName;
    }
    
    public void setHidden(boolean hidden) {
    	this.hidden = hidden;
    }
    
    public boolean isHidden() {
    	return hidden;
    }
    
    public void setSuffix(final String suffix) {
    	this.suffix = suffix;
    }
    
    public String getSuffix() {
    	return suffix;
    }
    
    protected void addValues(Value... value) {
        values.addAll(Arrays.asList(value));
    }

    public List<Value> getValues() {
        return values;
    }
    
    public ModuleCategory getCategory() {
        return category;
    }
    
    public void setKeyBind(int keybind) {
    	this.keybind = keybind;
    }

    public void load(JsonObject directory) {
        directory.entrySet().forEach(data -> {
            switch (data.getKey()) {
                case "key":
                    setKeybind(data.getValue().getAsInt());
                    break;
                case "enabled":
                    if (!(isEnabled() && data.getValue().getAsBoolean()) && !(!isEnabled() && !data.getValue().getAsBoolean()))
                        toggle();
                    break;
            }
            Value val = find(data.getKey());
            if (val != null) val.setValue(data.getValue().getAsString());
            if (val instanceof ColorValue && val != null) {
            	val.setValue(data.getValue().getAsInt());
            } else {
            	if (val != null) {
            		val.setValue(data.getValue().getAsString());
            	}
            }
        });
    }

    public Value find(String term) {
        for (Value value : values) {
            if (value.getLabel().equalsIgnoreCase(term)) {
                return value;
            }
        }
        return null;
    }

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	private void setKeybind(int keybind) {
		this.keybind = keybind;
	}

    public void save(JsonObject directory) {
        directory.addProperty("key", getKey());
        directory.addProperty("enabled", isEnabled());
        values.forEach(val -> {
            directory.addProperty(val.getLabel(), val.getValueAsString());
        });
    }
}
