package intent.AquaDev.aqua.alt.design;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.thealtening.api.TheAltening;
import com.thealtening.api.retriever.AsynchronousDataRetriever;
import com.thealtening.api.retriever.BasicDataRetriever;
import com.thealtening.auth.TheAlteningAuthentication;
import com.thealtening.auth.service.AlteningServiceType;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.altloader.AltLoader;
import intent.AquaDev.aqua.altloader.Api;
import intent.AquaDev.aqua.altloader.Callback;
import intent.AquaDev.aqua.altloader.RedeemResponse;
import intent.AquaDev.aqua.msauth.MicrosoftAuthentication;
import io.netty.util.internal.ThreadLocalRandom;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class AltManager extends GuiScreen {
   private static final AltLoader altLoader = new AltLoader();
   private static final String ALTENING_AUTH_SERVER = "http://authserver.thealtening.com/";
   private static final String ALTENING_SESSION_SERVER = "http://sessionserver.thealtening.com/";
   private static TheAlteningAuthentication alteningAuthentication = TheAlteningAuthentication.mojang();
   public GuiScreen parentScreen;
   private final Gson parser = new GsonBuilder().create();
   private final String status = "";
   public GuiTextField email;
   public GuiTextField password;
   public static String emailName = "";
   public static String passwordName = "";
   public static int i = 0;
   String apiKey = "";

   public AltManager(GuiScreen event) {
      this.parentScreen = event;
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.add(new GuiButton(13, 10, height / 4 + 120 + 205, 68, 20, "TheAltening"));
      this.buttonList.add(new GuiButton(12, 10, height / 4 + 120 + 180, 68, 20, "EasyMC"));
      this.buttonList.add(new GuiButton(11, 10, height / 4 + 144 + 130, 68, 20, "Microsoft"));
      this.buttonList.add(new GuiButton(9, 10, height / 4 + 96 + 130, 68, 20, "Add"));
      this.buttonList.add(new GuiButton(3, 10, height / 4 + 96 + 96, 68, 20, "Remove"));
      this.buttonList.add(new GuiButton(1, 10, height / 4 + 96 + 50, 68, 20, "Login"));
      this.buttonList.add(new GuiButton(5, 10, height / 4 + 96 + 73, 68, 20, "Back"));
      this.buttonList.add(new GuiButton(2, 10, height / 4 + 96 + 15, 68, 20, "Clipboard"));
      this.email = new GuiTextField(3, this.fontRendererObj, 10, 50, 200, 20);
      this.password = new GuiTextField(4, this.fontRendererObj, 10, 100, 200, 20);
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      this.email.textboxKeyTyped(typedChar, keyCode);
      this.password.textboxKeyTyped(typedChar, keyCode);
      if (keyCode == 1) {
         this.mc.displayGuiScreen(this.parentScreen);
      }
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void updateScreen() {
      this.email.updateCursorCounter();
      this.password.updateCursorCounter();
   }

   @Override
   public void mouseClicked(int x, int y, int m) {
      this.email.mouseClicked(x, y, m);
      this.password.mouseClicked(x, y, m);

      try {
         super.mouseClicked(x, y, m);
      } catch (Exception var5) {
         var5.printStackTrace();
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      new ScaledResolution(this.mc);
      if (!GL11.glGetString(7937).contains("AMD") && !GL11.glGetString(7937).contains("RX ")) {
         Aqua.INSTANCE.shaderBackgroundMM.renderShader();
      } else {
         this.drawDefaultBackground();
      }

      int i1 = 0;
      Aqua.INSTANCE.comfortaa3.drawStringWithShadow("Email:", 10.0F, 36.0F, -1);
      Aqua.INSTANCE.comfortaa3.drawStringWithShadow("Password:", 10.0F, 86.0F, -1);
      Aqua.INSTANCE.comfortaa3.drawStringWithShadow("§3Logged in with §a" + this.mc.getSession().getUsername(), (float)(width / 2), 20.0F, -1);
      this.email.drawTextBox();
      this.password.drawTextBox();
      int boxY = 26;
      int boxX = 50;
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public boolean login1(String email, String password) {
      try {
         YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
         YggdrasilUserAuthentication auth = new YggdrasilUserAuthentication(service, Agent.MINECRAFT);
         auth.setUsername(email);
         auth.setPassword(password);
         if (auth.canLogIn()) {
            try {
               auth.logIn();
               Minecraft.getMinecraft().session = new Session(
                  auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang"
               );
               return true;
            } catch (AuthenticationException var6) {
               return false;
            }
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }

      return false;
   }

   public void login(String Email, String password) {
      Aqua.INSTANCE.ircClient.setIngameName(Minecraft.getMinecraft().session.getUsername());
      alteningAuthentication.updateService(AlteningServiceType.MOJANG);

      try {
         Minecraft.getMinecraft().session = Login.logIn(Email, password);
      } catch (Exception var8) {
         YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
         YggdrasilUserAuthentication authentication = (YggdrasilUserAuthentication)authenticationService.createUserAuthentication(Agent.MINECRAFT);
         authentication.setUsername(Email);
         authentication.setPassword(password);

         try {
            authentication.logIn();
            Minecraft.getMinecraft().session = new Session(
               authentication.getSelectedProfile().getName(),
               authentication.getSelectedProfile().getId().toString(),
               authentication.getAuthenticatedToken(),
               "mojang"
            );
         } catch (Exception var7) {
            Minecraft.getMinecraft().session = new Session(Email, "", "", "mojang");
         }
      }
   }

   public void loginAltening(String Email, String password) {
      Aqua.INSTANCE.ircClient.setIngameName(Minecraft.getMinecraft().session.getUsername());
      alteningAuthentication.updateService(AlteningServiceType.THEALTENING);

      try {
         Minecraft.getMinecraft().session = Login.logIn(Email, password);
      } catch (Exception var8) {
         YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
         YggdrasilUserAuthentication authentication = (YggdrasilUserAuthentication)authenticationService.createUserAuthentication(Agent.MINECRAFT);
         authentication.setUsername(Email);
         authentication.setPassword(password);

         try {
            authentication.logIn();
            Minecraft.getMinecraft().session = new Session(
               authentication.getSelectedProfile().getName(),
               authentication.getSelectedProfile().getId().toString(),
               authentication.getAuthenticatedToken(),
               "mojang"
            );
         } catch (Exception var7) {
            Minecraft.getMinecraft().session = new Session(Email, "", "", "mojang");
         }
      }
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 11) {
         MicrosoftAuthentication.getInstance().loginWithPopUpWindow();
      }

      if (button.id == 13) {
         String clipboard = getClipboardString();
         if (!clipboard.contains("@alt")) {
            if (clipboard.contains("api-")) {
               this.apiKey = clipboard;
            }

            if (this.apiKey != null) {
               BasicDataRetriever basicDataRetriever = TheAltening.newBasicRetriever(this.apiKey);
               AsynchronousDataRetriever asynchronousDataRetriever = basicDataRetriever.toAsync();
               this.loginAltening(basicDataRetriever.getAccount().getToken(), "test");
               return;
            }
         }

         this.loginAltening(getClipboardString(), "test");
      }

      if (button.id == 10) {
      }

      if (button.id == 9 && !this.email.getText().isEmpty() && !this.password.getText().isEmpty()) {
         emailName = this.email.getText();
         passwordName = this.password.getText();
         AltsSaver.AltTypeList.add(new AltTypes(emailName, passwordName));
      }

      if (button.id == 12) {
         String token = GuiScreen.getClipboardString();
         if (token.equals("")) {
            try {
               Class<?> oclass = Class.forName("java.awt.Desktop");
               Object object = oclass.getMethod("getDesktop").invoke(null);
               oclass.getMethod("browse", URI.class).invoke(object, new URI("https://easymc.io/"));
            } catch (Throwable var6) {
            }
         }

         Api.redeem(token, new Callback<Object>() {
            @Override
            public void done(Object o) {
               if (!(o instanceof String)) {
                  if (AltManager.altLoader.savedSession == null) {
                     AltManager.altLoader.savedSession = AltManager.this.mc.getSession();
                  }

                  RedeemResponse response;
                  AltManager.altLoader.easyMCSession = response = (RedeemResponse)o;
                  AltManager.altLoader.setEasyMCSession(response);
               }
            }
         });
      }

      if (button.id == 1) {
         if (!this.email.getText().isEmpty() && !this.password.getText().isEmpty()) {
            this.login(this.email.getText().trim(), this.password.getText().trim());
            this.email.setText("");
            this.password.setText("");
         } else if (this.email.getText().isEmpty() && this.password.getText().isEmpty()) {
            this.password.setText("a");
            StringBuilder randomName = new StringBuilder();
            String alphabet = "1234567891012121314151638926704982";
            int random = ThreadLocalRandom.current().nextInt(2, 5);

            for(int i = 0; i < random; ++i) {
               randomName.append(alphabet.charAt(ThreadLocalRandom.current().nextInt(1, alphabet.length())));
            }

            this.email.setText("AquaClient" + randomName);
         }
      }

      if (button.id == 2) {
         String Copy = getClipboardString();
         String[] WomboCombo = Copy.split(":");
         String Email1 = WomboCombo[0];
         String Passwort = WomboCombo[1];
         this.email.writeText(Email1);
         this.password.writeText(Passwort);
      }

      if (button.id == 26) {
         this.mc.displayGuiScreen(this.parentScreen);
      }

      if (button.id == 5) {
         this.mc.displayGuiScreen(new GuiMainMenu());
      }
   }

   public static boolean loginToken(String token) {
      Aqua.INSTANCE.ircClient.setIngameName(Minecraft.getMinecraft().session.getUsername());
      URL url = null;

      try {
         url = new URL("https://api.minecraftservices.com/minecraft/profile/");
      } catch (MalformedURLException var15) {
         var15.printStackTrace();
      }

      HttpURLConnection conn = null;

      try {
         conn = (HttpURLConnection)url.openConnection();
      } catch (IOException var14) {
         var14.printStackTrace();
      }

      conn.setRequestProperty("Authorization", "Bearer " + token);
      conn.setRequestProperty("Content-Type", "application/json");

      try {
         conn.setRequestMethod("GET");
      } catch (ProtocolException var13) {
         var13.printStackTrace();
      }

      BufferedReader in = null;

      try {
         in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      } catch (IOException var12) {
         var12.printStackTrace();
      }

      StringBuilder response = new StringBuilder();

      while(true) {
         String output;
         try {
            if ((output = in.readLine()) == null) {
               break;
            }
         } catch (IOException var16) {
            var16.printStackTrace();
            return false;
         }

         response.append(output);
      }

      String jsonString = response.toString();
      JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();
      String username = obj.get("name").getAsString();
      String uuid = obj.get("id").getAsString();
      Minecraft.getMinecraft().session = new Session(username, uuid, token, "null");

      try {
         in.close();
      } catch (IOException var11) {
         var11.printStackTrace();
      }

      return true;
   }

   public Session generateAltening(String apiKey) throws IOException {
      alteningAuthentication.updateService(AlteningServiceType.THEALTENING);
      URL url = new URL("https://api.thealtening.com/v2/generate?key=" + apiKey);
      HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
      connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
      connection.setRequestMethod("GET");
      connection.setDoInput(true);
      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Type", "application/json");
      InputStream inputStream = connection.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      StringBuilder builder = new StringBuilder();

      String line;
      while((line = reader.readLine()) != null) {
         builder.append(line).append("\n");
      }

      JsonObject json = this.parser.fromJson(builder.toString(), JsonObject.class);
      String token = json.get("token").getAsString();
      connection.disconnect();
      return this.getSession(token, Aqua.name);
   }

   public Session getSession(String name, String password) {
      Aqua.INSTANCE.ircClient.setIngameName(Minecraft.getMinecraft().session.getUsername());
      YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
      YggdrasilUserAuthentication yggdrasilUserAuthentication = new YggdrasilUserAuthentication(yggdrasilAuthenticationService, Agent.MINECRAFT);
      yggdrasilUserAuthentication.setUsername(name);
      yggdrasilUserAuthentication.setPassword(password);

      try {
         yggdrasilUserAuthentication.logIn();
         System.out.println("Login successful!");
         return new Session(
            yggdrasilUserAuthentication.getSelectedProfile().getName(),
            yggdrasilUserAuthentication.getSelectedProfile().getId().toString(),
            yggdrasilUserAuthentication.getAuthenticatedToken(),
            "mojang"
         );
      } catch (Exception var6) {
         return null;
      }
   }
}
