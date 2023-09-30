package dev.africa.pandaware.impl.module.misc;

import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.utils.client.Printer;

@ModuleInfo(name = "Billionaire", category = Category.MISC)
public class BillionaireModule extends Module {
    @Override
    public void onDisable() {
        for (int i = 0; i < 5; i++) {
            Printer.chat("Made by The_Bi11iona1re");
        }
    }
}
