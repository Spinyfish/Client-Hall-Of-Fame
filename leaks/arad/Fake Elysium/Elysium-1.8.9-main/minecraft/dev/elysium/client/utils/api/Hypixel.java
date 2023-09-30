package dev.elysium.client.utils.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class Hypixel {
    public static int lastTotalStaffBans = -1;
    public static int lastTotalWatchdogBans = -1;

    public static int[] bansSinceStart;
    public static int[] bansAtStart;

    public static long timeSinceLastCheck;

    public static List<String> getInfo(String username) {
        try {
            URL url = new URL("https://api.slothpixel.me/api/players/" + username);
            Scanner scanner = new Scanner(url.openStream());
            StringBuilder sb = new StringBuilder();

            while(scanner.hasNext())
                sb.append(scanner.nextLine());

            JsonParser json = new JsonParser();
            JsonElement element = json.parse(sb.toString());
            scanner.close();

            if(element.isJsonObject()) {
                JsonObject details = element.getAsJsonObject();
                JsonElement online = details.getAsJsonArray();
                List<String> out = null;
                for(JsonElement el : details.getAsJsonArray()) {
                    out.add(el.toString());
                }
                return out;
            }
        } catch (Exception e)  {
            System.out.println(e.getMessage());
            return null;
        }
        return null;
    }
    public static boolean isPlayerOnline(String username) {
        try {
            URL url = new URL("https://api.slothpixel.me/api/players/" + username + "/status");
            Scanner scanner = new Scanner(url.openStream());
            StringBuilder sb = new StringBuilder();

            while(scanner.hasNext())
                sb.append(scanner.nextLine());

            JsonParser json = new JsonParser();
            JsonElement element = json.parse(sb.toString());
            scanner.close();

            if(element.isJsonObject()) {
                JsonObject details = element.getAsJsonObject();
                JsonElement online = details.get("online");
                return online.toString().equals("true");
            }
        } catch (Exception e)  {
            System.out.println(e.getMessage());
            return false;
        }
        return false;
    }
    public static int[] getBanStats() {
        try {
            URL url = new URL("https://api.slothpixel.me/api/bans");
            Scanner scanner = new Scanner(url.openStream());
            StringBuilder sb = new StringBuilder();

            while(scanner.hasNext())
                sb.append(scanner.nextLine());

            JsonParser json = new JsonParser();
            JsonElement element = json.parse(sb.toString());
            scanner.close();

            if(element.isJsonObject()) {
                JsonObject details = element.getAsJsonObject();
                JsonElement watchdog = details.get("watchdog").getAsJsonObject().get("total");
                JsonElement staff = details.get("staff").getAsJsonObject().get("total");

                int totalStaffBans = Integer.parseInt(staff.toString());
                int totalWatchdogBans = Integer.parseInt(watchdog.toString());

                if(lastTotalStaffBans == -1 && lastTotalWatchdogBans == -1) {
                    lastTotalStaffBans = totalStaffBans;
                    lastTotalWatchdogBans = totalWatchdogBans;
                    bansAtStart = new int[] {totalWatchdogBans, totalStaffBans};
                }

                int staffBans = totalStaffBans - lastTotalStaffBans;
                int watchdogBans = totalWatchdogBans - lastTotalWatchdogBans;

                bansSinceStart = new int[] {totalWatchdogBans - bansAtStart[0], totalStaffBans - bansAtStart[1]};

                lastTotalStaffBans = totalStaffBans;
                lastTotalWatchdogBans = totalWatchdogBans;

                timeSinceLastCheck = System.currentTimeMillis();

                return new int[] {watchdogBans,staffBans};

            }
        } catch (Exception e)  {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
