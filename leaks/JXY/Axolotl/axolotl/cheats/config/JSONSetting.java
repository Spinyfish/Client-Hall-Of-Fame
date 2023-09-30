package axolotl.cheats.config;

import axolotl.cheats.settings.Setting;

public class JSONSetting {

    public String name, type;
    public Object value;

    public JSONSetting(String name, Object value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public JSONSetting(Setting set) {
        this.name = set.name;
        this.value = set.getObjectValue();
        this.type = set.sname;
    }

}
