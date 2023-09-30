package dev.africa.pandaware.impl.module.player.antivoid;

import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.module.player.antivoid.modes.*;
import dev.africa.pandaware.impl.setting.NumberSetting;
import lombok.Getter;

@Getter
@ModuleInfo(name = "Anti Void", category = Category.PLAYER)
public class AntiVoidModule extends Module {
    private final NumberSetting fallDistance = new NumberSetting("Fall Distance", 10, 0, 3, 0.5);

    public AntiVoidModule() {
        this.registerSettings(this.fallDistance);

        this.registerModes(
                new PacketAntiVoid("Packet", this),
                new CollideAntiVoid("Collide", this),
                new BlinkAntiVoid("Blink", this),
                new FuncraftAntiVoid("Funcraft", this),
                new DEVAntiVoid("DEV", this)
        );
    }
}
