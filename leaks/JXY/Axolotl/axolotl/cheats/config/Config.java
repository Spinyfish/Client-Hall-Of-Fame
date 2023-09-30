package axolotl.cheats.config;

import axolotl.Axolotl;

public class Config {

    public String json, location, name;

    public Config(String json, String name) {
        this.json = json;
        this.name = name;
        this.location = System.getProperty("user.dir") + "\\" + Axolotl.INSTANCE.name + "\\configs\\" + name + ".json";
    }

}
