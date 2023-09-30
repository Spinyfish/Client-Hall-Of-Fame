/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.setting;

import net.minecraft.client.Minecraft;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.obj.MonsoonPlayerObject;

public class ModeProcessor {
    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final MonsoonPlayerObject player = Wrapper.getMonsoon().getPlayer();
    private final Module parentModule;

    public ModeProcessor(Module parentModule) {
        this.parentModule = parentModule;
    }

    public void onEnable() {
        Wrapper.getEventBus().subscribe(this);
    }

    public void onDisable() {
        Wrapper.getEventBus().unsubscribe(this);
    }

    public Setting[] getModeSettings() {
        return new Setting[0];
    }

    public Module getParentModule() {
        return this.parentModule;
    }
}

