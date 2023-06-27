package net.minecraft.client.resources;

import com.google.common.collect.*;
import com.google.common.base.*;
import com.google.common.io.*;
import net.minecraft.util.*;
import com.google.gson.*;
import org.apache.commons.io.*;
import java.io.*;
import java.util.*;
import org.apache.logging.log4j.*;

public class ResourceIndex
{
    private static final Logger logger;
    private final Map<String, File> resourceMap;
    
    public ResourceIndex(final File p_i1047_1_, final String p_i1047_2_) {
        this.resourceMap = (Map<String, File>)Maps.newHashMap();
        if (p_i1047_2_ != null) {
            final File file1 = new File(p_i1047_1_, "objects");
            final File file2 = new File(p_i1047_1_, "indexes/" + p_i1047_2_ + ".json");
            BufferedReader bufferedreader = null;
            try {
                bufferedreader = Files.newReader(file2, Charsets.UTF_8);
                final JsonObject jsonobject = new JsonParser().parse((Reader)bufferedreader).getAsJsonObject();
                final JsonObject jsonobject2 = JsonUtils.getJsonObject(jsonobject, "objects", null);
                if (jsonobject2 != null) {
                    for (final Map.Entry<String, JsonElement> entry : jsonobject2.entrySet()) {
                        final JsonObject jsonobject3 = (JsonObject)entry.getValue();
                        final String s = entry.getKey();
                        final String[] astring = s.split("/", 2);
                        final String s2 = (astring.length == 1) ? astring[0] : (astring[0] + ":" + astring[1]);
                        final String s3 = JsonUtils.getString(jsonobject3, "hash");
                        final File file3 = new File(file1, s3.substring(0, 2) + "/" + s3);
                        this.resourceMap.put(s2, file3);
                    }
                }
            }
            catch (JsonParseException var20) {
                ResourceIndex.logger.error("Unable to parse resource index file: " + file2);
            }
            catch (FileNotFoundException var21) {
                ResourceIndex.logger.error("Can't find the resource index file: " + file2);
            }
            finally {
                IOUtils.closeQuietly((Reader)bufferedreader);
            }
        }
    }
    
    public Map<String, File> getResourceMap() {
        return this.resourceMap;
    }
    
    static {
        logger = LogManager.getLogger();
    }
}
