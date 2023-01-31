package intent.AquaDev.aqua;

import de.Hero.settings.SettingsManager;
import de.liquiddev.ircclient.api.IrcClient;
import de.liquiddev.ircclient.client.ClientType;
import de.liquiddev.ircclient.client.IrcClientFactory;
import events.Event;
import intent.AquaDev.aqua.cape.GIF;
import intent.AquaDev.aqua.cape.GIFManager;
import intent.AquaDev.aqua.cape.GifLoader;
import intent.AquaDev.aqua.command.CommandSystem;
import intent.AquaDev.aqua.config.Config;
import intent.AquaDev.aqua.discord.DiscordRPC;
import intent.AquaDev.aqua.gui.novoline.ClickguiScreenNovoline;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.modules.ModuleManager;
import intent.AquaDev.aqua.utils.FileUtil;
import intent.AquaDev.aqua.utils.IrcChatListener;
import intent.AquaDev.aqua.utils.MouseWheelUtil;
import intent.AquaDev.aqua.utils.ShaderBackground;
import intent.AquaDev.aqua.utils.ShaderBackgroundMM;
import intent.AquaDev.aqua.utils.UnicodeFontRenderer;
import intent.AquaDev.aqua.utils.UnicodeFontRenderer2;
import intent.AquaDev.aqua.utils.UnicodeFontRenderer3;
import intent.AquaDev.aqua.utils.UnicodeFontRenderer4;
import intent.AquaDev.aqua.utils.UnicodeFontRenderer5;
import intent.AquaDev.aqua.utils.UnicodeFontRenderer6;
import intent.AquaDev.aqua.utils.UnicodeFontRenderer7;
import intent.AquaDev.aqua.utils.UnicodeFontRenderer8;
import intent.AquaDev.aqua.utils.UnicodeFontRenderer9;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import viamcp.ViaMCP;

public class Aqua {
   public static Aqua INSTANCE;
   private ResourceLocation resourceLocation;
   public static DiscordRPC discordRPC = new DiscordRPC();
   public UnicodeFontRenderer comfortaa;
   public UnicodeFontRenderer comfortaa2;
   public UnicodeFontRenderer comfortaa3;
   public UnicodeFontRenderer comfortaa4;
   public UnicodeFontRenderer comfortaa5;
   public UnicodeFontRenderer comfortaaGamster;
   public UnicodeFontRenderer rise;
   public UnicodeFontRenderer2 japan;
   public UnicodeFontRenderer3 roboto;
   public UnicodeFontRenderer3 roboto2;
   public UnicodeFontRenderer3 robotoTabguiName;
   public UnicodeFontRenderer3 robotoBlockCount;
   public UnicodeFontRenderer3 robotoBold;
   public UnicodeFontRenderer3 robotoPanel;
   public UnicodeFontRenderer4 sigma;
   public UnicodeFontRenderer4 sigma2;
   public UnicodeFontRenderer4 sigma3;
   public UnicodeFontRenderer5 jelloClickguiPanelTop;
   public UnicodeFontRenderer5 jelloClickguiPanelBottom;
   public UnicodeFontRenderer5 jelloTabGUI;
   public UnicodeFontRenderer5 jelloTabGUIBottom;
   public UnicodeFontRenderer5 jelloClickguiSettings;
   public UnicodeFontRenderer6 novoline;
   public UnicodeFontRenderer6 novlineBigger;
   public UnicodeFontRenderer6 novolineSmall;
   public UnicodeFontRenderer7 verdana2;
   public UnicodeFontRenderer8 esp;
   public UnicodeFontRenderer9 tenacityNormal;
   public UnicodeFontRenderer9 tenacitySmall;
   public UnicodeFontRenderer9 tenacityBig;
   public UnicodeFontRenderer9 tenacity;
   public GifLoader GIFLoader;
   public GIFManager GIFmgr;
   public static String dev = "LCA_ROTZ aka LCA_KOTZ";
   public static String name = "Aqua";
   public static String build = "13";
   public static boolean allowed = true;
   public IrcClient ircClient;
   public static ModuleManager moduleManager;
   public FileUtil fileUtil;
   public static SettingsManager setmgr = new SettingsManager();
   public static CommandSystem commandSystem;
   public MouseWheelUtil mouseWheelUtil;
   public ClickguiScreenNovoline clickGuiNovo;
   public ShaderBackground shaderBackground = new ShaderBackground();
   public ShaderBackgroundMM shaderBackgroundMM = new ShaderBackgroundMM();
   public long lastConnection;

   public void AquaStart() throws MalformedURLException {
      INSTANCE = this;
      String ign = Minecraft.getMinecraft().session.getUsername();
      this.ircClient = IrcClientFactory.getDefault().createIrcClient(ClientType.FANTA, "6XK7pNhw3wUxJwCL", ign, "b" + build);
      this.ircClient.getApiManager().registerApi(new IrcChatListener());
      String beta = !Objects.equals(this.ircClient.getNickname(), "luigiaqua") && !Objects.equals(INSTANCE.ircClient.getNickname(), "LCA_MODZ") ? "" : " Beta";
      Display.setTitle("" + name + " b" + build + beta + " by " + dev);
      this.ircClient.getApiManager().registerCustomDataListener((sender, tag, data) -> {
         ByteBuffer buffer = ByteBuffer.wrap(data);
         int id = buffer.getInt();
         if (tag.toLowerCase().startsWith("report_")) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/report " + tag.split("report_")[1] + " hacking confirm");
         }
      });
      moduleManager = new ModuleManager();
      if (GL11.glGetString(7937).contains("Nvidia") || GL11.glGetString(7937).contains("AMD") || GL11.glGetString(7937).contains("RX")) {
         System.out.println("Found GraphicCard");
         discordRPC.start();
      }

      commandSystem = new CommandSystem();
      this.fileUtil = new FileUtil();
      this.fileUtil.createFolder();
      this.fileUtil.createPicFolder();
      this.fileUtil.createConfigFolder();
      this.fileUtil.loadKeys();
      this.fileUtil.loadModules();
      this.mouseWheelUtil = new MouseWheelUtil();
      this.clickGuiNovo = new ClickguiScreenNovoline(null);

      try {
         ViaMCP.getInstance().start();
         ViaMCP.getInstance().initAsyncSlider();
      } catch (Exception var7) {
         var7.printStackTrace();
      }

      new Config("settings.txt", false, true).loadOnStart();
      this.GIFLoader = new GifLoader();
      this.GIFmgr = new GIFManager();
      this.GIFmgr.addGif(new GIF("test2", "test2"));
      this.GIFmgr.addGif(new GIF("rias2", "rias2"));
      this.GIFmgr.addGif(new GIF("rias", new URL("https://i.ibb.co/qdM3pq7/rias.gif")));
      this.GIFmgr.addGif(new GIF("aqua", new URL("https://i.imgur.com/aNm2z1W.gif")));
      this.GIFmgr.addGif(new GIF("anime", new URL("https://media.tenor.com/9xGP1UCWLw4AAAAM/weathering-with-you-hina-amano.gif")));

      for(File f : FileUtil.PIC.listFiles()) {
         setmgr.getSetting("GuiElementsMode").modes = this.addToStringArray(setmgr.getSetting("GuiElementsMode").getModes(), f.getName().split("\\.")[0]);
      }

      Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
   }

   public Aqua() throws UnknownHostException {
      INSTANCE = this;
      if (GL11.glGetString(7937).contains("NVIDIA") || GL11.glGetString(7937).contains("AMD") || GL11.glGetString(7937).contains("RX")) {
         System.out.println("Found GraphicCard");
         discordRPC.start();
      }

      this.shaderBackground = new ShaderBackground();
      this.shaderBackgroundMM = new ShaderBackgroundMM();
      this.japan = UnicodeFontRenderer2.getFontFromAssets("Japanese", 40, 0, 0.0F, 1.0F);
      this.comfortaa = UnicodeFontRenderer.getFontFromAssets("Comfortaa-Regular", 40, 0, 0.0F, 1.0F);
      this.comfortaa2 = UnicodeFontRenderer.getFontFromAssets("Comfortaa-Regular", 37, 0, 0.0F, 1.0F);
      this.rise = UnicodeFontRenderer.getFontFromAssets("Comfortaa-Regular", 18, 0, 0.0F, 1.0F);
      this.comfortaa3 = UnicodeFontRenderer.getFontFromAssets("Comfortaa-Regular", 20, 0, 0.0F, 1.0F);
      this.comfortaa4 = UnicodeFontRenderer.getFontFromAssets("Comfortaa-Regular", 17, 0, 0.0F, 1.0F);
      this.comfortaa5 = UnicodeFontRenderer.getFontFromAssets("Comfortaa-Regular", 15, 0, 0.0F, 1.0F);
      this.comfortaaGamster = UnicodeFontRenderer.getFontFromAssets("Comfortaa-Regular", 80, 0, 0.0F, 1.0F);
      this.roboto = UnicodeFontRenderer3.getFontFromAssets("Roboto-Thin", 70, 0, 0.0F, 1.0F);
      this.robotoPanel = UnicodeFontRenderer3.getFontFromAssets("Roboto-Light", 18, 0, 0.0F, 1.0F);
      this.robotoBold = UnicodeFontRenderer3.getFontFromAssets("Roboto-Light", 23, 0, 0.0F, 1.0F);
      this.roboto2 = UnicodeFontRenderer3.getFontFromAssets("Roboto-Thin", 25, 0, 0.0F, 1.0F);
      this.robotoTabguiName = UnicodeFontRenderer3.getFontFromAssets("Roboto-Thin", 50, 0, 0.0F, 1.0F);
      this.robotoBlockCount = UnicodeFontRenderer3.getFontFromAssets("Roboto-Thin", 18, 0, 0.0F, 1.0F);
      this.sigma = UnicodeFontRenderer4.getFontFromAssets("Geomanist-Regular", 20, 0, 0.0F, 1.0F);
      this.sigma2 = UnicodeFontRenderer4.getFontFromAssets("Geomanist-Regular", 30, 0, 0.0F, 1.0F);
      this.sigma3 = UnicodeFontRenderer4.getFontFromAssets("Geomanist-Regular", 16, 0, 0.0F, 1.0F);
      this.jelloClickguiPanelTop = UnicodeFontRenderer5.getFontFromAssets("jelloregular", 25, 0, 0.0F, 1.0F);
      this.jelloClickguiSettings = UnicodeFontRenderer5.getFontFromAssets("jelloregular", 15, 0, 0.0F, 1.0F);
      this.jelloClickguiPanelBottom = UnicodeFontRenderer5.getFontFromAssets("jelloregular", 20, 0, 0.0F, 1.0F);
      this.jelloTabGUI = UnicodeFontRenderer5.getFontFromAssets("jellolight", 45, 0, 0.0F, 1.0F);
      this.jelloTabGUIBottom = UnicodeFontRenderer5.getFontFromAssets("jellolight", 20, 0, 0.0F, 1.0F);
      this.novoline = UnicodeFontRenderer6.getFontOnPC("Arial", 18, 0, 0.0F, 1.0F);
      this.novlineBigger = UnicodeFontRenderer6.getFontOnPC("Arial", 22, 0, 0.0F, 1.0F);
      this.novolineSmall = UnicodeFontRenderer6.getFontOnPC("Arial", 15, 0, 0.0F, 1.0F);
      this.verdana2 = UnicodeFontRenderer7.getFontFromAssets("verdana", 16, 0, 0.0F, 1.0F);
      this.esp = UnicodeFontRenderer8.getFontFromAssets("esp", 55, 0, 0.0F, 1.0F);
      this.tenacity = UnicodeFontRenderer9.getFontFromAssets("tenacity", 40, 0, 0.0F, 1.0F);
      this.tenacityBig = UnicodeFontRenderer9.getFontFromAssets("tenacity", 22, 0, 0.0F, 1.0F);
      this.tenacityNormal = UnicodeFontRenderer9.getFontFromAssets("tenacity", 20, 0, 0.0F, 1.0F);
      this.tenacitySmall = UnicodeFontRenderer9.getFontFromAssets("tenacity", 15, 0, 0.0F, 1.0F);
   }

   public void shutdown() {
      this.fileUtil.saveKeys();
      this.fileUtil.saveModules();
      new Config("settings.txt", false, true).saveCurrent();
   }

   public void onEvent(Event e) {
      try {
         if (Minecraft.getMinecraft().thePlayer == null) {
            return;
         }

         for(Module mod : moduleManager.modules) {
            if (mod.isToggled()) {
               if (e == null || mod == null) {
                  return;
               }

               mod.onEvent(e);
            }
         }
      } catch (Exception var4) {
      }
   }

   public String[] addToStringArray(String[] oldArray, String newString) {
      String[] newArray = Arrays.copyOf(oldArray, oldArray.length + 1);
      newArray[oldArray.length] = newString;
      return newArray;
   }
}
