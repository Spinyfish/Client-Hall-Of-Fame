package axolotl.cheats.config;

public class JSONModule {

    public String name;
    public boolean toggled;
    public Object[] settings;

    public JSONModule(String name, boolean toggled, Object[] settings) {
        this.name = name;
        this.toggled = toggled;
        this.settings = settings;
    }

}
