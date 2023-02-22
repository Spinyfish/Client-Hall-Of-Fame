package net.minecraft.client.resources;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.util.JsonUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Map.Entry;

public class ResourceIndex {
    private static final Logger logger = LogManager.getLogger();
    private final Map<String, File> resourceMap = Maps.newHashMap();

    public ResourceIndex(final File p_i1047_1_, final String p_i1047_2_) {
        if (p_i1047_2_ != null) {
            final File file1 = new File(p_i1047_1_, "objects");
            final File file2 = new File(p_i1047_1_, "indexes/" + p_i1047_2_ + ".json");
            BufferedReader bufferedreader = null;

            try {
                bufferedreader = Files.newReader(file2, Charsets.UTF_8);
                final JsonObject jsonobject = (new JsonParser()).parse(bufferedreader).getAsJsonObject();
                final JsonObject jsonobject1 = JsonUtils.getJsonObject(jsonobject, "objects", null);

                if (jsonobject1 != null) {
                    for (final Entry<String, JsonElement> entry : jsonobject1.entrySet()) {
                        final JsonObject jsonobject2 = (JsonObject) entry.getValue();
                        final String s = entry.getKey();
                        final String[] astring = s.split("/", 2);
                        final String s1 = astring.length == 1 ? astring[0] : astring[0] + ":" + astring[1];
                        final String s2 = JsonUtils.getString(jsonobject2, "hash");
                        final File file3 = new File(file1, s2.substring(0, 2) + "/" + s2);
                        this.resourceMap.put(s1, file3);
                    }
                }
            } catch (final JsonParseException var20) {
                logger.error("Unable to parse resource index file: " + file2);
            } catch (final FileNotFoundException var21) {
                logger.error("Can't find the resource index file: " + file2);
            } finally {
                IOUtils.closeQuietly(bufferedreader);
            }
        }
    }

    public Map<String, File> getResourceMap() {
        return this.resourceMap;
    }
}
