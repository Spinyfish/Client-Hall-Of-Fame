package me.r.touchgrass.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by r on 18/01/2022
 */
public class HTTPUtil {

    public static String commitDate;
    public static String commitTime;

    private static CacheValue<String> cacheGetCurrentCommitHash = new CacheValue<String>(120);
    public static String getCurrentCommitHash() {
        return "null";
    }

    private static Cache cacheGetCurrentCommitDate = new Cache(120);
    public static void getCurrentCommitDate() {
    }

    public static String getWebsiteLine(String URL) {
        return "null";
    }
}
