/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.misc.script;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventPacket;
import wtf.monsoon.impl.event.EventRender2D;
import wtf.monsoon.impl.event.EventUpdate;
import wtf.monsoon.misc.script.ScriptHandler;
import wtf.monsoon.misc.script.wrapper.ScriptUtil;

public class ScriptManager {
    public static Module registerModule(String name, String description, final ScriptHandler scriptHandler) {
        Module module = new Module(name, description, Category.SCRIPT){
            @EventLink
            public final Listener<EventUpdate> eventUpdateListener;
            @EventLink
            public final Listener<EventRender2D> eventRender2DListener;
            @EventLink
            public final Listener<EventPacket> eventPacketListener;
            {
                super(name, description, category);
                this.eventUpdateListener = e -> scriptHandler.onUpdate();
                this.eventRender2DListener = e -> scriptHandler.onRender2D();
                this.eventPacketListener = scriptHandler::onPacket;
            }

            @Override
            public void onEnable() {
                super.onEnable();
                try {
                    scriptHandler.onEnable();
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }

            @Override
            public void onDisable() {
                super.onDisable();
                try {
                    scriptHandler.onDisable();
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        };
        ScriptUtil.settings.forEach((mod, set) -> {
            if (mod.toLowerCase().contains(module.getName().toLowerCase())) {
                module.getSettings().add((Setting<?>)set);
            }
        });
        Wrapper.getMonsoon().getModuleManager().putModule(module.getClass(), module);
        Wrapper.getLogger().info("Added module " + module.getName() + " from a script.");
        return module;
    }
}

