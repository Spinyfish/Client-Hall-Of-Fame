//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.levansj01.verus.storage.database.check;

import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.manager.CheckManager;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.storage.config.VerusConfiguration;
import me.levansj01.verus.type.VerusTypeLoader;
import org.bukkit.configuration.file.YamlConfiguration;

public class FlatCheckManager extends CheckManager {
    public void setEnabled(String string, boolean bool) {
        super.setEnabled(string, bool);
        this.saveChecks();
    }

    public void setEnabled(Check check, boolean bool) {
        this.setEnabled(check.identifier(), bool);
    }

    public void saveChecks() {
        VerusConfiguration verusConfiguration = StorageEngine.getInstance().getVerusConfig();
        YamlConfiguration yamlConfiguration = verusConfiguration.getCheckConfiguration();
        Check[] checks = this.checks;
        int i = 0;
        if (i >= checks.length) {
            verusConfiguration.saveConfig(yamlConfiguration, verusConfiguration.getCheckFile());
            return;
        }
        Check check = checks[i];
        String type = check.getType().getName() + "." + check.getSubType();
        yamlConfiguration.set(type + ".enabled", this.isEnabled(check));
        yamlConfiguration.set(type + ".autoban", this.isAutoban(check));
        if (VerusTypeLoader.isCustom()) {
            yamlConfiguration.set(type + ".maxViolation", this.getMaxViolation(check));
            yamlConfiguration.set(type + ".commands", this.getCommands(check));
        }
        ++i;
    }

    public void addCommand(String string, String string2) {
    }

    public void setMaxViolation(String string, int i) {
        super.setMaxViolation(string, i);
        this.saveChecks();
    }

    public void removeCommand(String string, int i) {
    }

    public void setAutoban(String string, boolean bool) {
        super.setAutoban(string, bool);
        this.saveChecks();
    }

    public void removeCommand(CheckValues checkValues, int i) {
    }

    public void setAutoban(Check check, boolean bool) {
        this.setAutoban(check.identifier(), bool);
    }

    public void loadChecks() {
        YamlConfiguration yamlConfiguration = StorageEngine.getInstance().getVerusConfig().getCheckConfiguration();
        Check[] checks = this.checks;
        int i = 0;
        if (i >= checks.length) {
            return;
        }
        Check check = checks[i];
        this.values.compute(check.identifier(), (v, v2) -> {
            if (v2 == null) {
                v2 = new CheckValues(check);
            }
            String type = check.getType().getName() + "." + check.getSubType();
            String typeEnabled = type + ".enabled";
            v2.setAlert(yamlConfiguration.getBoolean(typeEnabled, true));
            typeEnabled = type + ".autoban";
            v2.setPunish(yamlConfiguration.getBoolean(typeEnabled, true));
            if (VerusTypeLoader.isCustom()) {
                v2.setMaxViolation(yamlConfiguration.getInt(type + ".maxViolation", check.getMaxViolation()));
                v2.setCommands(yamlConfiguration.getStringList(type + ".commands"));
            }
            return v2;
        });
        ++i;
    }

    public void addCommand(CheckValues checkValues, String string) {
    }
}
