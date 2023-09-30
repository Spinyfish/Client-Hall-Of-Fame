/**
 * @project LiriumV4
 * @author Skush/Duzey
 * @at 06.07.2022
 */
package de.lirium.util.weather;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class WeatherManager {

    /* Weather */
    private static final String KEY = "0476cbcfb851aec5f3b5834ac6f687db";

    /* Stuff */
    public boolean autoDetect = true;
    public String location = "London,uk";
    public String lastUsedLocation;

    /* Weather Data */
    public String lastWeatherTemp, lastWeatherWindSpeed;

    public WeatherManager() {
        final Thread thread = new Thread(this::refreshWeather);
        thread.start();
    }

    private void refreshWeather() {
        while (true) {
            try {
                final String autoLocation = getLocation();
                final String api = "https://api.openweathermap.org/data/2.5/weather?q=" + (this.autoDetect ? autoLocation : location) + "&appid=" + KEY;

                final URLConnection conn = new URL(api).openConnection();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                final StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    result.append(line);
                reader.close();

                final Map<String, Object> respMap = this.jsonToMap(result.toString());
                final Map<String, Object> mainMap = this.jsonToMap(respMap.get("main").toString());
                final Map<String, Object> windMap = this.jsonToMap(respMap.get("wind").toString());

                final double temp = Double.parseDouble(mainMap.get("temp").toString()) - 273.15;
                final DecimalFormat df = new DecimalFormat("0.00");
                this.lastWeatherTemp = df.format(temp).replaceAll(",", ".");

                final double windSpeed = Double.parseDouble(windMap.get("speed").toString()) * 3.6;
                this.lastWeatherWindSpeed = df.format(windSpeed).replaceAll(",", ".");

                this.lastUsedLocation = this.autoDetect ? autoLocation : location;
            } catch (Exception ignored) {}
            try {
                Thread.sleep(30000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getLocation() {
        String location = "London,uk";

        try {
            final String ip = new Scanner(new URL("http://checkip.amazonaws.com").openStream(), "UTF-8").useDelimiter("\\A").next();
            final String api = "http://ip-api.com/json/" + ip;
            final URLConnection connection = new URL(api).openConnection();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            final JsonObject jsonObject = new JsonParser().parse(reader).getAsJsonObject();
            final String countryCode = jsonObject.get("countryCode").toString().replaceAll("\"", "");
            final String city = jsonObject.get("city").toString().replaceAll("\"", "");

            return city + "," + countryCode.toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    private Map<String, Object> jsonToMap(final String input) {
        return new Gson().fromJson(input, new TypeToken<HashMap<String, Object>>() {}.getType());
    }
}
