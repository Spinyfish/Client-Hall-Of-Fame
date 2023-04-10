package crispy.util.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import crispy.Crispy;
import crispy.features.hacks.impl.misc.SelfDestruct;
import crispy.notification.NotificationPublisher;
import crispy.notification.NotificationType;
import crispy.util.server.cape.CapeUser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public class CapeManager {
    public static boolean running;

    private final ScheduledExecutorService capeThread = Executors.newSingleThreadScheduledExecutor();

    public static ArrayList<String> ignoreUsers = new ArrayList<>();
    public static ArrayList<CapeUser> capeUsers = new ArrayList<>();
    public static ArrayList<String> coolGuy = new ArrayList<>();
    public int i;

    public void updateIndex() {
        i++;
        if (i > 14) {
            i = 1;
        }

    }


    public void sendCapeRequest(String player, String cape) {

        capeThread.execute(() -> {
            try {
                JsonObject json = new JsonObject();
                json.addProperty("session", player);
                json.addProperty("discordid", Crispy.INSTANCE.getDiscordID());
                if (!cape.isEmpty()) json.addProperty("cape", cape);
                json.addProperty("hwid", Crispy.INSTANCE.getHwid());
                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpPost request = new HttpPost("https://crispyclient.herokuapp.com/api/crispy/updateSession");
                StringEntity entity = new StringEntity(json.toString(),
                        ContentType.APPLICATION_JSON);
                request.setEntity(entity);
                HttpResponse response = httpClient.execute(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    public void getCape(String player) {
       // capeThread.execute(() -> {
       //     if(!SelfDestruct.selfDestruct) {
       //         try {
       //             Thread.sleep(200);
       //             if (!ignoreUsers.contains(StringUtils.stripControlCodes(player)) && !containsName(StringUtils.stripControlCodes(player)) && !player.contains(" ")) {
       //                 JsonObject json = new JsonObject();
       //                 ignoreUsers.add(player);
       //                 //"/cape?ses=" + Minecraft.getMinecraft().thePlayer.getCommandSenderName() + "&hwid=" + Crispy.INSTANCE.getHwid()
       //                 json.addProperty("ses", Minecraft.getMinecraft().thePlayer.getCommandSenderName());
       //                 json.addProperty("hwid", Crispy.INSTANCE.getHwid());
       //                 HttpClient httpClient = HttpClientBuilder.create().build();
       //                 HttpPost request = new HttpPost("https://crispyclient.herokuapp.com/api/crispy/" + StringUtils.stripControlCodes(player) + "/cape");
       //                 StringEntity entity = new StringEntity(json.toString(),
       //                         ContentType.APPLICATION_JSON);
       //                 request.setEntity(entity);
       //                 HttpResponse response = httpClient.execute(request);
       //                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
       //                 System.out.println("Checking cape");
       //                 JsonParser jsonParser = new JsonParser();
       //                 JsonObject jsonElement = (JsonObject) jsonParser.parse(bufferedReader);
       //                 boolean dev = jsonElement.get("coolguy").getAsBoolean();
       //                 if (dev) coolGuy.add(player);
       //                 if (jsonElement.get("type").getAsString().equalsIgnoreCase("Crispy")) {
       //                     Crispy.addChatMessage("A user has been detected for using crispy!");
       //                     capeUsers.add(new CapeUser(StringUtils.stripControlCodes(player), new ResourceLocation("Crispy")));
       //                     NotificationPublisher.queue("Crispy Detector ", StringUtils.stripControlCodes(player) + " has been detected for using crispy!", NotificationType.INFO);
       //                 } else if (jsonElement.get("type").getAsString().equalsIgnoreCase("custom")) {
       //                     Crispy.addChatMessage("A user has been detected for using crispy!");
       //                     NotificationPublisher.queue("Crispy Detector ", StringUtils.stripControlCodes(player) + " has been detected for using crispy!", NotificationType.INFO);
       //                     ResourceLocation resourceLocation = new ResourceLocation(String.format("capes/%s.png", jsonElement.get("identifier").getAsString()));
       //                     String capeUrl = jsonElement.get("url").getAsString();
       //                     ThreadDownloadImageData threadDownloadImageData = new ThreadDownloadImageData(null, capeUrl, null, new IImageBuffer() {
       //                         @Override
       //                         public BufferedImage parseUserSkin(BufferedImage var1) {
       //                             return var1;
       //                         }
//
       //                         @Override
       //                         public void skinAvailable() {
       //                             //BLABLABLAA
       //                         }
//
//
       //                     });
//
       //                     Minecraft.getMinecraft().getTextureManager().loadTexture(resourceLocation, threadDownloadImageData);
       //                     capeUsers.add(new CapeUser(StringUtils.stripControlCodes(player), resourceLocation));
//
       //                 }
       //             }
//
       //         } catch (Exception e) {
       //             e.printStackTrace();
       //         }
       //     }
       // });


    }

    public boolean containsName(String name) {
        for (CapeUser capeUser : capeUsers) {
            if (capeUser.player.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

}
