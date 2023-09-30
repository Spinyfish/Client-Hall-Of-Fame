package axolotl.cheats.config;

import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.ModeSetting;
import axolotl.cheats.settings.Setting;
import axolotl.cheats.settings.SpecialSettings;

import java.util.ArrayList;

public class JSONModuleManager {

    public JSONModule getJMFM(Module m) {
        return new JSONModule(m.name, m.toggled, getJSFM(m));
    }

    private Object[] getJSFM(Module m) {

        ArrayList<JSONSetting> settingArr = new ArrayList<>();
        Object[] settings = m.settings.getSettingsArray();

        for(Object obj : settings) {
            if(!(obj instanceof SpecialSettings)) {
                Setting set = (Setting)obj;
                settingArr.add(new JSONSetting(set));
                if(set.sname.equalsIgnoreCase("ModeSetting")) {
                    if(((ModeSetting)set).getModeSettings() != null) {
                        for (Object obj2 : ((ModeSetting) set).getModeSettings()) {
                            Setting set2 = (Setting) obj2;
                            settingArr.add(new JSONSetting("." + set2.name, set2.getObjectValue(), set2.sname));
                        }
                    }
                }
            }
        }

        return settingArr.toArray();

    }

}
