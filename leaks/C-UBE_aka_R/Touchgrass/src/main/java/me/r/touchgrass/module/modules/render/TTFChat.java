package me.r.touchgrass.module.modules.render;

import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.settings.Setting;

import java.util.ArrayList;

/**
 * Created by r on 21/12/2021
 */
@Info(name = "TTFChat", description = "Changes the chat font", category = Category.Render)
public class TTFChat extends Module {

    public TTFChat() {
        ArrayList<String> mode = new ArrayList<>();
        mode.add("SF UI Display");
        mode.add("Comfortaa");
        addSetting(new Setting("Type", this, "SF UI Display", mode));
    }
}
