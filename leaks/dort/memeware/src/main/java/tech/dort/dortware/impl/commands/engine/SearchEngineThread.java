package tech.dort.dortware.impl.commands.engine;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;
import tech.dort.dortware.impl.commands.SearchEngineCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class SearchEngineThread implements Runnable {

    public ArrayList<String> mojangQueue = new ArrayList<>();
    public ArrayList<String> searchQueue = new ArrayList<>();

    //url requests are done on a separate thread so there arent any lag spikes when doing requests

    @Override
    public void run() {
        start();
    }

    public void start() {
        while (true) {

            if (mojangQueue.size() != 0) {
                String req = mojangQueue.get(0);
                mojangQueue.remove(0);

                String uuid = IGNToUUID(req);
                if (uuid.equals("ERROR_FAILED_IGN_TO_UUID")) {
                    SearchEngineCommand.sendMsg("Error: failed ign to uuid");
                    continue;
                }

                String[] pastIGNs = UUIDToPreviousIGNs(uuid);
                if (pastIGNs[0].equals("ERROR_FAILED_UUID_TO_IGN_ARRAY")) {
                    SearchEngineCommand.sendMsg("Error: failed uuid to ign list");
                    continue;
                }

                StringBuilder request = new StringBuilder("player:" + uuid);
                for (String ign : pastIGNs) {
                    if ((request.length() + ign.length()) < 7900) {
                        request.append(",").append(ign);
                    } else {
                        SearchEngineCommand.sendMsg("Error: request too large, skipping: " + ign);
                    }
                }
                execRequest(request.toString(), req);
            }

            if (searchQueue.size() != 0) {
                execRequest(searchQueue.get(0), searchQueue.get(0));
                searchQueue.remove(0);
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void execRequest(String result, String originalRequest) {
        String link = "http://elgoog.ga/cgi-bin/se.fcgi?" + SearchEngineCommand.key + ":" + result;
        try {

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("http.user_agent", "Bot");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            if (!SearchEngineCommand.postMode) {

                SearchEngineCommand.sendMsg("Search results for: " + originalRequest);
                while ((line = reader.readLine()) != null) {
                    SearchEngineCommand.sendMsg(line);
                }
                SearchEngineCommand.sendMsg("");
            } else {
                StringBuilder reply = new StringBuilder(originalRequest);
                while ((line = reader.readLine()) != null) {
                    if (!line.contains("Finished in"))
                        reply.append(line).append(" ");
                }
                reply = new StringBuilder(reply.toString().replaceAll("\\.", "^^").replace("@", " <a7> ").replace("ips[", "[").replace("emails[", "["));
                Minecraft.getMinecraft().getNetHandler().addToSendQueue(new C01PacketChatMessage(reply.toString()));
            }

            reader.close();

        } catch (Exception e) {
            SearchEngineCommand.sendMsg("Error connecting to search engine");
            e.printStackTrace();
        }
    }

    public static void checkStatus() {
        String link = "http://elgoog.ga/cgi-bin/se.fcgi?" + SearchEngineCommand.key + ":" + "status";
        try {

            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("http.user_agent", "Bot");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("#")) {
                    System.out.println(line);
                    String[] parts = line.split("\t");
                    String[] vars = new String[]{"Initial days: ", "Requests left: ", "Expires in: "};
                    for (int i = 1; i < parts.length - 1; i++) {
                        if (i == 3) {
                            long currentTime = System.currentTimeMillis() / 1000;
                            long endTime = Long.parseLong(parts[i]);
                            long diff = endTime - currentTime;
                            int minutes = (int) (diff / 60);
                            int hours = minutes / 60;
                            int days = hours / 24;
                            minutes = minutes % 60;
                            hours = hours % 60;
                            SearchEngineCommand.sendMsg(vars[i - 1] + " " + days + " days, " + hours + " hours, " + minutes + " minutes");
                            continue;
                        }
                        SearchEngineCommand.sendMsg(vars[i - 1] + " " + parts[i]);
                    }
                }
            }
            reader.close();

        } catch (Exception e) {
            SearchEngineCommand.sendMsg("Error connecting to search engine");
            e.printStackTrace();
        }
    }


    public static String[] UUIDToPreviousIGNs(String uuid) {
        String page = "";
        String link2 = "https://api.mojang.com/user/profiles/" + uuid + "/names";
        ArrayList<String> namess = new ArrayList<>();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        try {
            URL url = new URL(link2);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.7; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                page = line.substring(1, line.length() - 1);
            }
            reader.close();
            String[] names = page.split(",");
            names[0] = names[0].replace("}", "");
            for (String n : names) {
                if (n.contains("name")) {
                    String newName = n.substring(9, n.length() - 1);
                    if (!namess.contains(newName)) {
                        namess.add(newName);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            SearchEngineCommand.sendMsg("Error: Connection timeout to mojang");
            return new String[]{"ERROR_FAILED_UUID_TO_IGN_ARRAY"};
        }
        String[] names = new String[namess.size()];
        for (int i = 0; i < namess.size(); i++) {
            names[i] = namess.get(i);
        }
        return names;
    }


    public static String IGNToUUID(String playername) {
        String link = "https://api.mojang.com/users/profiles/minecraft/" + playername;
        try {
            URL url = new URL(link);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.7; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String page = "";
            String line;
            while ((line = reader.readLine()) != null) {
                page = line;
            }
            reader.close();
            if (page.contains("name")) {
                int length = playername.length();
                return page.substring(17 + length, page.length() - 2);
            } else {
                return "ERROR_FAILED_IGN_TO_UUID";
            }

        } catch (IOException e) {
            e.printStackTrace();
            SearchEngineCommand.sendMsg("Error: Connection timeout to mojang");
            return "ERROR_FAILED_IGN_TO_UUID";
        }
    }
}
