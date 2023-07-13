package me.r.touchgrass.module.modules.player;

import com.darkmagician6.eventapi.EventTarget;
import me.r.touchgrass.touchgrass;
import me.r.touchgrass.events.EventText;
import me.r.touchgrass.module.Category;
import me.r.touchgrass.module.Info;
import me.r.touchgrass.module.Module;
import me.r.touchgrass.settings.Setting;
import net.minecraft.client.network.NetworkPlayerInfo;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by r on 20/12/2021
 */
@Info(name = "NameProtect", category = Category.Player, description = "Changes your name clientside")
public class NameProtect extends Module {

    public NameProtect() {
        addSetting(new Setting("Your Name", this, "§cMe"));
        addSetting(new Setting("Other Names", this, "User"));
        addSetting(new Setting("All Players", this, false));
        addSetting(new Setting("SkinProtect", this, true));
    }

    @EventTarget
    public void onText(EventText event) {
        if (NameProtect.mc.thePlayer == null || event.getText().contains(touchgrass.prefix)) {
            return;
        }
        if (!this.isEnabled()) {
            return;
        }
        event.setText(StringUtils.replace(event.getText(), mc.thePlayer.getName(), h2.settingsManager.getSettingByName(this, "Your Name").getText()));
        if (h2.settingsManager.getSettingByName(this, "All Players").isEnabled()) {
            for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                event.setText(StringUtils.replace(event.getText(), playerInfo.getGameProfile().getName(), h2.settingsManager.getSettingByName(this, "Other Names").getText()));
            }
        }
    }
}
