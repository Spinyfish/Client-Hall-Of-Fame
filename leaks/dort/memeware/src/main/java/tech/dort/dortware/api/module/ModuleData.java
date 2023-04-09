package tech.dort.dortware.api.module;

import tech.dort.dortware.api.module.enums.ModuleCategory;

public class ModuleData {

    private final ModuleCategory category;
    private final String name;
    private final String otherName;
    private final String otherNameDumb;
    private final int defaultBind;

    public ModuleData(String name, int defaultBind, ModuleCategory category) {
        this(name, null, null, defaultBind, category);
    }

    public ModuleData(String name, String otherName, String otherNameDumb, int defaultBind, ModuleCategory category) {
        this.name = name;
        this.otherName = otherName;
        this.otherNameDumb = otherNameDumb;
        this.defaultBind = defaultBind;
        this.category = category;
    }

    public int getDefaultBind() {
        return defaultBind;
    }

    public String getName() {
        return name;
    }

    public String getOtherName() {
        return otherName;
    }

    public String getOtherNameDumb() {
        return otherNameDumb;
    }

    public boolean hasOtherName() {
        return otherName != null;
    }

    public boolean hasOtherNameDumb() {
        return otherNameDumb != null;
    }

    public ModuleCategory category() {
        return category;
    }

}
