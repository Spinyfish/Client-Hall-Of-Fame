package axolotl.cheats.settings;

public class SpecialSettings extends Setting{

    public SpecialSetting[] settingsData;

    public SpecialSettings(SpecialSetting...settingsData) {
        super("SpecialSettings");
        this.name = "missingno";
        this.settingsData = settingsData;
    }

    @Override
    public Object getObjectValue() {
        return null;
    }

    @Override
    public void setValue(Object value) { }

    @Override
    public String getValue() {
        String value = "";
        for(SpecialSetting s : settingsData) {
            value = value + " | " + (s.shortName.equalsIgnoreCase("") ? "" : s.shortName + ": ") + s.setting.getValue().split(" ")[1];
        }
        return value.replaceFirst("\\|", "");
    }

    @Override
    public String getSpecificValue() {
        return getValue();
    }
}
