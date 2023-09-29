package frapppyz.cutefurry.pics.util;

import frapppyz.cutefurry.pics.Wrapper;
import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class HWIDUtil {

    public static String UID  = "0000"; //taking uuid as string just to make everytjiong easoer
    public static String readContents(URL url){ //if ur reading this 1. why and 2. yes this is stackoverflow
        BufferedReader in = null;
        try {
            URLConnection con = url.openConnection();
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = in.readLine();
            StringBuilder builder = new StringBuilder();
            do {
                builder.append(line+"\n");
            } while ( (line = in.readLine()) != null);
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    public static String grabHWID(){
        String hwid;
        hwid = System.getenv("user.name") + System.getenv("COMPUTERNAME") + System.getenv("PROCESSOR_IDENTIFIER").replace( " ", "");
        hwid = hwid.replace(",", "");
        byte[] bytes;
        try {
            bytes = hwid.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        hwid = Base64.getEncoder().encodeToString(bytes) + "#" + UID;
        return hwid;
    }

    public static void checkHWID(){
        URL url = null;
        try {
            url = new URL("https://gist.githubusercontent.com/Frapppyz/6a3ba587877840ede7a898aa0a65532a/raw");
        } catch (MalformedURLException e) {
            // When the URL was not well-formed
            e.printStackTrace();
        }
        if(!(url != null && readContents(url).contains(grabHWID()))){
            Wrapper.getLogger().info("Your HWID is " + grabHWID() + ".");
            for(int i = 0; i < Integer.MAX_VALUE; i++){
                Minecraft.getMinecraft().thePlayer.jump();
            }
        }
    }
}
