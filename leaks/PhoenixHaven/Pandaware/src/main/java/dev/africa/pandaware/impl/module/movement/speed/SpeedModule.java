package dev.africa.pandaware.impl.module.movement.speed;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.module.movement.speed.modes.*;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.impl.ui.notification.Notification;
import dev.africa.pandaware.utils.player.MovementUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;
import lombok.Getter;

@Getter
@ModuleInfo(name = "Speed", category = Category.MOVEMENT)
public class SpeedModule extends Module {
    public SpeedModule() {
        this.registerModes(
                new BhopSpeed("Bhop", this),
                new NCPSpeed("NCP", this),
                new HypixelSpeed("Hypixel", this),
                new TickSpeed("Tick Speed", this),
                new CubecraftSpeed("Cubecraft", this),
                new VerusSpeed("Verus", this),
                new VulcanSpeed("Vulcan", this),
                new FuncraftSpeed("Funcraft", this),
                new CustomSpeed("Custom", this),
                new BlocksMCSpeed("BlocksMC", this),
                new DEVSpeed("DEV", this)
        );
    }

    @Override
    public void onDisable() {
        if (PlayerUtils.inLiquid() && (this.getCurrentMode() instanceof HypixelSpeed || this.getCurrentMode() instanceof NCPSpeed)) {
            Client.getInstance().getNotificationManager().addNotification(Notification.Type.INFO, "Disabled speed to prevent flags", 1);
        }
        MovementUtils.slowdown();
        mc.timer.timerSpeed = 1f;
    }

    @Override
    public String getSuffix() {
        String add = this.getCurrentMode().getInformationSuffix() != null ? " "
                + this.getCurrentMode().getInformationSuffix() : "";

        return this.getCurrentMode().getName() + add;
    }
}
