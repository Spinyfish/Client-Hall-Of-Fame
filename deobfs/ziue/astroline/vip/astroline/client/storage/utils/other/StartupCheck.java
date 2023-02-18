/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.service.config.preset.PresetManager
 */
package vip.astroline.client.storage.utils.other;

import vip.astroline.client.Astroline;
import vip.astroline.client.service.config.preset.PresetManager;

public class StartupCheck {
    public static StartupCheck INSTANCE = new StartupCheck();

    public void checkAuthentication() {
        Astroline.INSTANCE.keyAuth.init();
    }

    public void log() {
        PresetManager.loadAlts();
        String moduleCount = String.valueOf(Astroline.INSTANCE.moduleManager.getModules().size());
        String configCount = String.valueOf(PresetManager.presets.size());
        System.out.println("Loading " + moduleCount + " modules");
        System.out.println("Loading " + configCount + " configs");
    }
}
