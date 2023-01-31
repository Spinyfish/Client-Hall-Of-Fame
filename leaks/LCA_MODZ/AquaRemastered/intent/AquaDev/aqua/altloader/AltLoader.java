package intent.AquaDev.aqua.altloader;

import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.net.MalformedURLException;
import java.net.URL;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AltLoader {
   public static final String MODID = "altloader";
   public static final String VERSION = "1.0";
   public static final Logger LOGGER = LogManager.getLogger();
   private static AltLoader instance;
   public Session savedSession;
   public RedeemResponse easyMCSession;

   public static AltLoader getInstance() {
      return instance;
   }

   public AltLoader() {
      instance = this;
   }

   public String getCurrentStatus() {
      String status = ChatFormatting.GOLD
         + "No Token redeemed. Using "
         + ChatFormatting.YELLOW
         + Minecraft.getMinecraft().getSession().getUsername()
         + ChatFormatting.GOLD
         + " to login!";
      if (this.savedSession != null) {
         status = ChatFormatting.GREEN + "Token active. Using " + ChatFormatting.AQUA + this.easyMCSession.mcName + ChatFormatting.GREEN + " to login!";
      }

      return status;
   }

   public void setEasyMCSession(RedeemResponse response) {
      Minecraft.getMinecraft().setSession(new Session(response.mcName, response.uuid, response.session, "mojang"));

      try {
         ReflectionUtil.setStaticField(YggdrasilMinecraftSessionService.class, "JOIN_URL", new URL("https://sessionserver.easymc.io/session/minecraft/join"));
      } catch (MalformedURLException var3) {
         var3.printStackTrace();
      }
   }

   public void setMojangSession(Session session) {
      Minecraft.getMinecraft().setSession(session);

      try {
         ReflectionUtil.setStaticField(YggdrasilMinecraftSessionService.class, "JOIN_URL", new URL("https://sessionserver.mojang.com/session/minecraft/join"));
      } catch (MalformedURLException var3) {
         var3.printStackTrace();
      }
   }
}
