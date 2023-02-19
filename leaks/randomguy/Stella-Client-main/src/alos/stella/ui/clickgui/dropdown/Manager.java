package alos.stella.ui.clickgui.dropdown;

import alos.stella.value.Value;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Manager {
    private static final List<Value> settingList = new CopyOnWriteArrayList<>();
    public static void put(Value setting) {
        settingList.add(setting);
    }
    public static List<Value> getSettingList() {
        return settingList;
    }

}
