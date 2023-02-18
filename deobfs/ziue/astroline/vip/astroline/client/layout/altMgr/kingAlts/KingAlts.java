/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.layout.altMgr.kingAlts.AltJson
 *  vip.astroline.client.layout.altMgr.kingAlts.ProfileJson
 *  vip.astroline.client.storage.utils.other.HttpUtil
 */
package vip.astroline.client.layout.altMgr.kingAlts;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URL;
import vip.astroline.client.layout.altMgr.kingAlts.AltJson;
import vip.astroline.client.layout.altMgr.kingAlts.ProfileJson;
import vip.astroline.client.storage.utils.other.HttpUtil;

public class KingAlts {
    public static String API_KEY = "";

    public static void setApiKey(String key) {
        API_KEY = key;
    }

    public static ProfileJson getProfile() {
        Gson gson = new Gson();
        String result = null;
        try {
            result = HttpUtil.performGetRequest((URL)new URL("https://kinggen.info/api/v2/profile?key=" + API_KEY));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return gson.fromJson(result, ProfileJson.class);
    }

    public static AltJson getAlt() {
        Gson gson = new Gson();
        String result = null;
        try {
            result = HttpUtil.performGetRequest((URL)new URL("https://kinggen.info/api/v2/alt?key=" + API_KEY));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return gson.fromJson(result, AltJson.class);
    }
}
