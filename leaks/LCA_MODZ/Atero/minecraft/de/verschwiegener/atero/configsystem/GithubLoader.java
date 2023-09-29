package de.verschwiegener.atero.configsystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GithubLoader {

    public Map<String, String> configs = new LinkedHashMap<>();

    /**
     *
     * @param URL
     * @return String containing config
     * @throws MalformedURLException
     * @throws IOException
     */

    public String loadConfig(String URL) throws MalformedURLException, IOException {
        InputStream in = new URL(URL).openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String input = reader.lines().collect(Collectors.joining());
        return input;
    }

    /**
     *
     * @param Manifest_URL
     * @returns ArrayList<String> containing all the configs
     */

    public void readManifest(String Manifest_URL) {
        try {
            InputStream in = new URL(Manifest_URL)
                    .openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String input = reader.lines().collect(Collectors.joining());
            System.out.println("Input: " + input);
            String[] addresses = input.split(" ");
            System.out.println("Addresses: " + addresses.length);
            for (int i = 0; i < addresses.length; i++) {
                try {
                    //configs.add(new Config(addresses[i].split(":")[0]).loadonlineconfig(loadConfig(addresses[i].split(":")[1])));
                    configs.putIfAbsent(addresses[i].split("#")[0].toLowerCase(), addresses[i].split("#")[1]);
                } catch (Exception ex) {
                    System.out.println("Config not found");
                }
            }
        } catch (Exception e) {
            System.out.println("Error occurred while loading configs");
        }

    }
}
