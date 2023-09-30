package markgg.settings;

import java.util.ArrayList;
import java.util.List;

import markgg.Client;
import markgg.modules.Module;
import markgg.modules.Module.Category;

public class Setting {
	
	public String name;
	public boolean focused;	

	public void setName(String name) {
		this.name = name;
	}
	
	public static ArrayList<Setting> getSettingsByMod(Module mod){
		ArrayList<Setting> out = new ArrayList<>();
		for(Setting s : mod.settings){
			if(s instanceof BooleanSetting) {
				if(((BooleanSetting) s).getParentMod().equals(mod)) {
					out.add(s);
				}
			}
			if(s instanceof NumberSetting) {
				if(((NumberSetting) s).getParentMod().equals(mod)) {
					out.add(s);
				}
			}
			if(s instanceof ModeSetting) {
				if(((ModeSetting) s).getParentMod().equals(mod)) {
					out.add(s);
				}
			}
			if(s instanceof KeybindSetting) {
				if(((KeybindSetting) s).getParentMod().equals(mod)) {
					out.add(s);
				}
			}
		}
		if(out.isEmpty()){
			return null;
		}
		return out;
	}

}
