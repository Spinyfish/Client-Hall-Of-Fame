package club.pulsive.client.intent.yay;

import club.pulsive.api.main.Pulsive;
import lombok.experimental.UtilityClass;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

@UtilityClass
public class VersionControl {
    public String getLatestVersion(){
        try{
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://intent.store/product/18/latestVersion").openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String currentln;
            while ((currentln = in.readLine()) != null) {
                if(!currentln.isEmpty())
                    return currentln;
            }
        }catch(Exception ignored){}
        return "null";
    }

    public boolean isLatestVersion(){
        return getLatestVersion().equals(String.valueOf(Pulsive.INSTANCE.getClientInfo().getClientVersion()));
    }

}
