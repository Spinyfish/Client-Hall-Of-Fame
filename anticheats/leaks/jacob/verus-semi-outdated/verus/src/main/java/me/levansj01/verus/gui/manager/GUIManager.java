package me.levansj01.verus.gui.manager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.levansj01.verus.VerusPlugin;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.gui.GUI;
import me.levansj01.verus.gui.impl.CheckGUI;
import me.levansj01.verus.gui.impl.MainGUI;
import me.levansj01.verus.gui.impl.TypeGUI;

public class GUIManager {
    private MainGUI mainGui;
    private static GUIManager instance;
    private CheckGUI checkGui;
    private final Map<CheckType, GUI> typeGuis = new HashMap();

    public Map<CheckType, GUI> getTypeGuis() {
        return this.typeGuis;
    }

    public MainGUI getMainGui() {
        return this.mainGui;
    }

    public GUI getTypeGui(CheckType checkType) {
        return (GUI)this.typeGuis.get(checkType);
    }

    public CheckGUI getCheckGui() {
        return this.checkGui;
    }

    public void disable() {
        if (this.mainGui != null) {
            this.mainGui.clear();
        }
        if (this.checkGui != null) {
            this.checkGui.clear();
        }
        this.typeGuis.values().forEach(GUI::clear);
    }

    public static GUIManager getInstance() {
        GUIManager gUIManager;
        if (instance == null) {
            gUIManager = instance = new GUIManager();
        } else {
            gUIManager = instance;
        }
        return gUIManager;
    }

    public void enable(VerusPlugin verusPlugin) {
        Object[] objectArray = verusPlugin.getTypeLoader().loadChecks();
        for (CheckType checkType : CheckType.values()) {
            if (checkType.ignore()) {
            }
            this.typeGuis.put(checkType, (Object)new TypeGUI(checkType, (List)Arrays.stream((Object[])objectArray).filter(check -> {
                boolean bl = check.getType() == checkType;
                return bl;
            }).collect(Collectors.toList())));
        }
        this.mainGui = new MainGUI();
        this.checkGui = new CheckGUI();
    }
}
