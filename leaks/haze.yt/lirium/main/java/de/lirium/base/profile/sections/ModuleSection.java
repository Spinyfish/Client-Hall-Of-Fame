package de.lirium.base.profile.sections;

import com.google.gson.JsonObject;
import com.viaversion.viaversion.api.protocol.remapper.ValueReader;
import de.lirium.Client;
import de.lirium.base.profile.Profile;
import de.lirium.base.profile.ProfileSection;
import de.lirium.base.setting.SettingRegistry;
import god.buddy.aot.BCompiler;

@ProfileSection.Info(name = "Modules")
public class ModuleSection extends ProfileSection {

    public ModuleSection(Profile profile) {
        super(profile);
    }

    @Override
    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void write(JsonObject base) {
        Client.INSTANCE.getModuleManager().getFeatures().forEach(module -> {
            final JsonObject moduleJson = new JsonObject();
            moduleJson.addProperty("key", module.getKeyBind());
            moduleJson.addProperty("enabled", module.isEnabled());
            moduleJson.addProperty("bypass", module.isBypassing());
            final JsonObject valueJson = new JsonObject();
            SettingRegistry.getValues().get(module).forEach((iSetting) -> {
                valueJson.addProperty(iSetting.getDisplay(), String.valueOf(iSetting.getValue()));
            });
            moduleJson.add("values", valueJson);
            base.add(module.getName(), moduleJson);
        });
    }

    @Override
    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public void read(JsonObject base) {
        Client.INSTANCE.getModuleManager().getFeatures().forEach(module -> {
            if(base.has(module.getName())) {
                final JsonObject moduleJson = base.get(module.getName()).getAsJsonObject();
                module.setKeyBind(moduleJson.get("key").getAsInt());
                module.setEnabled(moduleJson.get("enabled").getAsBoolean());
                module.setBypassing(moduleJson.get("bypass").getAsBoolean());
                if(moduleJson.has("values")) {
                    final JsonObject valueJson = moduleJson.get("values").getAsJsonObject();
                    SettingRegistry.getValues().get(module).forEach(iSetting -> {
                        if(valueJson.has(iSetting.getDisplay())) {
                            iSetting.tryChangeField("value", valueJson.get(iSetting.getDisplay()).getAsString());
                        } else {
                            System.out.println(iSetting.getDisplay() + " not found");
                        }
                    });
                }
            }
        });
    }
}
