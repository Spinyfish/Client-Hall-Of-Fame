package cafe.corrosion.config.base;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.List;

public abstract class Config {
    protected static final Gson GSON = new Gson();
    protected String clientVersion;
    protected String author;
    protected String checksum;
    protected List<JsonObject> configData;

    public abstract void load(String var1);

    public String getClientVersion() {
        return this.clientVersion;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getChecksum() {
        return this.checksum;
    }

    public List<JsonObject> getConfigData() {
        return this.configData;
    }
}
