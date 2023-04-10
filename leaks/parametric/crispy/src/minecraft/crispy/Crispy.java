package crispy;

import arithmo.gui.altmanager.FileManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import crispy.features.commands.CommandManager;
import crispy.features.event.Event;
import crispy.features.event.impl.player.EventKey;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.event.impl.render.EventRenderGui;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackManager;
import crispy.features.hacks.impl.combat.Aura;
import crispy.features.hacks.impl.player.InvManager;
import crispy.features.script.ScriptManager;
import crispy.fonts.decentfont.FontUtil;
import crispy.fonts.greatfont.FontManager;
import crispy.friend.FriendManager;
import crispy.gui.clickgui.ClickGui;
import crispy.gui.csgui.CSGOGui;
import crispy.gui.novoline.NovoGui;
import crispy.util.file.FileDownloader;
import crispy.util.file.ModuleSaver;
import crispy.util.rotation.Rotation;
import crispy.util.server.CapeManager;
import crispy.util.server.HWID;
import crispy.util.server.IRC;
import crispy.util.time.TimeHelper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.crash.CrashReport;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.superblaubeere27.valuesystem.ValueManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.lwjgl.opengl.Display;
import viamcp.ViaMCP;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Welcome to crispy development!
 */
@Getter
public enum Crispy {

    INSTANCE;

    private final ScheduledExecutorService authThread = Executors.newSingleThreadScheduledExecutor();

    private final String version = "2.0";
    @Getter(AccessLevel.NONE)
    private final TimeHelper timer = new TimeHelper();
    /**
     * Make sure to change when exporting!
     */
    public double Build = 3.67;
    private String[] sortedChange;
    private String[] changeLog;
    private HackManager hackManager;
    private ValueManager valueManager;
    private CommandManager commandManager;
    private FriendManager friendManager;
    private CapeManager capeManager;
    private IRC irc;
    private ScriptManager scriptManager;
    private ResourceLocation discordPng;
    private ClickGui clickGui;
    private NovoGui novoGui;
    private CSGOGui click;
    private String hwid = "invalid";
    private String discordID = "NULL", discordUser, discordName, discordAvatar, avatarLink;
    private Rotation serverRotation = new Rotation(0, 0);
    private FileManager altFile;
    private ModuleSaver saver;
    private FontManager fontManager;
    private boolean running;
    private long created;
    private double delay;
    @Setter
    private String capeUrl = "Crispy";


    /**
     * @param message printed message
     *                Displays client side chat message.
     */
    public static void addChatMessage(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("\247bCrispy\2477 » " + "\2477" + message));
    }

    /**
     * @param s chat message
     * @return boolean
     */
    public static boolean onSendChatMessage(String s) {//EntityPlayerSP

        if (s.startsWith(".") && Crispy.INSTANCE.getHackManager().getModule("Commands", false).isEnabled()) {

            Crispy.INSTANCE.getCommandManager().callCommand(s.substring(1));

            return false;
        }
        if (s.startsWith("#")) {
            String message = s.split("#", 2)[1];
            Crispy.INSTANCE.getIrc().sendMessage(message);
            return false;
        }

        return true;
    }

    /**
     * @param time cooldown time
     * @return boolean
     */
    public static boolean invCooldownElapsed(long time) {
        return InvManager.timer.hasTimeElapsed(time, true);
    }

    public void updateDiscord(String firstline, String secondLine) {

        DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(secondLine);
        b.setBigImage("fixed", "By k1ndled");
        b.setDetails(firstline);
        b.setStartTimestamps(created);

        DiscordRPC.discordUpdatePresence(b.build());

    }

    /**
     * Initializes all the classes and displays crispy :o
     */
    public void loadHandles() {

        System.out.println("Loading handles");
        this.created = System.currentTimeMillis();
        fontManager = new FontManager();
        fontManager.setup();
        FontUtil.bootstrap();
        System.out.println("Finished setting up font manager");
        friendManager = new FriendManager();
        friendManager.load();
        valueManager = new ValueManager();
        commandManager = new CommandManager();
        hackManager = new HackManager();
        scriptManager = new ScriptManager();
        irc = new IRC();
        saver = new ModuleSaver();
        altFile = new FileManager();
        altFile.loadFiles();
        saver.load();
        saver.loadScripts();
        saver.loadConfigs();
        clickGui = new ClickGui();
        ThreadDownloadImageData threadDownloadImageData = new ThreadDownloadImageData(null, avatarLink, null, new IImageBuffer() {
            @Override
            public BufferedImage parseUserSkin(BufferedImage var1) {
                return var1;
            }

            @Override
            public void skinAvailable() {
                //ignored
            }
        });
        discordPng = new ResourceLocation(String.format("capes/%s.png", Crispy.INSTANCE.getDiscordID()));
        Minecraft.getMinecraft().getTextureManager().loadTexture(discordPng, threadDownloadImageData);
        click = new CSGOGui();
        novoGui = new NovoGui();
        Display.setTitle("Crispy " + getVersion());

        capeManager = new CapeManager();
        capeManager.sendCapeRequest(Minecraft.getMinecraft().getSession().getUsername(), "");
        Minecraft.getMinecraft().currentScreen = null;
        Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
    }

    /**
     * Initializes the client
     */
    public void init() {
        hwid = HWID.getHWID();
        running = true;


        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
            System.out.println("Authing with " + user.username + "#" + user.discriminator + "!");
            discordUser = "<@" + user.userId + ">";
            discordID = user.userId;
            discordAvatar = user.avatar;
            discordName = user.username;
            updateDiscord("Playing Crispy " + getVersion(), "Hacking on some servers.");
            avatarLink = "https://cdn.discordapp.com/avatars/710670209777664071/0d35367b6aae7d2a6c9dc0f9eb00d794.png?size=256";
            running = false;
        }).build();
        DiscordRPC.discordInitialize("715655138337095764", handlers, true);
        new Thread("Discord RPC Callback") {
            public void run() {
                while (running) {
                    DiscordRPC.discordRunCallbacks();
                }
            }
        }.run();
        try {
            ViaMCP.getInstance().start();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Stops the client and saves files and modules
     */
    public void stop() {
        try {
            assert saver != null;
            saver.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
        friendManager.save();
        altFile.saveFiles();
    }

    /**
     * @param e event being called
     *          - Calls the events.
     */
    public void onEvent(Event e) {
        if (e instanceof EventPacket) {
            EventPacket eventPacket = (EventPacket) e;
            if (eventPacket.getPacket() instanceof C03PacketPlayer) {
                C03PacketPlayer cPacketPlayer = (C03PacketPlayer) ((EventPacket) e).getPacket();
                if (cPacketPlayer.rotating)
                    serverRotation = new Rotation(cPacketPlayer.getYaw(), cPacketPlayer.getPitch());

            }
        }
        /*
         * Uwu converter for aroze...
         */
        if (e instanceof EventPacket && (discordID.equalsIgnoreCase("273524398483308549") || discordID.equalsIgnoreCase("829297704642412545"))) {
            Packet packet = ((EventPacket) e).getPacket();
            if (packet instanceof S02PacketChat) {
                S02PacketChat s02PacketChat = (S02PacketChat) packet;
                String test = s02PacketChat.getChatComponent().getFormattedText();
                e.setCancelled(true);
                String owo = test.replace("R", "W").replace("L", "W").replace("r", "w").replace("l", "w");
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(owo));
            }
            if (packet instanceof C01PacketChatMessage) {
                C01PacketChatMessage c01 = (C01PacketChatMessage) packet;
                String test = c01.getMessage();
                if (test.startsWith("/"))
                    return;
                boolean owoFirst = false, owoSecond = false;
                final int randomInt = Aura.randomNumber(3, 1);
                if (randomInt == 1) {
                    owoFirst = true;
                } else if (randomInt == 2) {
                    owoSecond = true;
                }
                String owo = test.replace("R", "W").replace("L", "W").replace("r", "w").replace("l", "w");
                if (owoSecond) {
                    owo += " uwu";
                }
                if (owoFirst) {
                    StringBuilder stringBuilder = new StringBuilder(owo);
                    stringBuilder.insert(0, "uwu ");
                    owo = stringBuilder.toString();
                }
                c01.message = owo;

            }
        }
        if (e instanceof EventRenderGui) {
            timer.reset();

        }
        if (e instanceof EventUpdate) {
            delay++;
            if (delay > 5) {
                delay = 0;
                getCapeManager().updateIndex();
            }
        }

        getHackManager().getHacks().stream()
                .filter(Hack::isEnabled)
                .forEach(hack -> hack.onEvent(e));
        getCommandManager().getCommands()
                .forEach(command -> command.onEvent(e));
    }

    /**
     * Calls when a player presses a key
     *
     * @param key,i' keyboard key
     */
    public void onKey(int key) {
        EventKey eventKey = new EventKey(key);
        onEvent(eventKey);
        getHackManager().getHacks().stream()
                .filter(hack -> hack.getKey() == key)
                .forEach(Hack::toggle);

    }

    /**
     * Checks for authentication using H H's API
     */
    public void checkForAuth() {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("hwid", Crispy.INSTANCE.getHwid());
            json.addProperty("id", Crispy.INSTANCE.getDiscordID());
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost("https://crispyclient.herokuapp.com/api/crispy/auth");
            StringEntity entity = new StringEntity(json.toString(),
                    ContentType.APPLICATION_JSON);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            final int responseCode = response.getStatusLine().getStatusCode();
            switch (responseCode) {
                case 403:
                    BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    JsonParser jsonParser = new JsonParser();
                    JsonObject jsonElement = (JsonObject) jsonParser.parse(br);
                    br.close();
                    System.out.println(jsonElement.toString());
                    if (jsonElement.get("status").getAsString().equalsIgnoreCase("fail")) {
                        String err = jsonElement.get("message").getAsString();
                        System.out.println(err);
                        switch (err) {
                            case "No HWID found":
                                StringSelection stringSelection = new StringSelection(hwid);
                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                clipboard.setContents(stringSelection, null);

                                javax.swing.JOptionPane.showConfirmDialog(null, "You haven't set your HWID yet it seems, Click OK to copy " + hwid + " to clipboard", "HWID Not Set",
                                        javax.swing.JOptionPane.DEFAULT_OPTION);

                                Minecraft.getMinecraft().displayCrashReport(new CrashReport("Unset HWID", new NullPointerException()));
                                System.exit(0);
                                break;
                            case "Invalid HWID":
                                Minecraft.getMinecraft().displayCrashReport(new CrashReport("Incorrect HWID", new NullPointerException()));
                                System.exit(0);
                                break;
                            case "Blacklisted":
                                Minecraft.getMinecraft().displayCrashReport(new CrashReport("Blacklisted [gtfo]", new NullPointerException()));
                                System.exit(0);
                                break;
                            case "Unauthorized":
                                Minecraft.getMinecraft().displayCrashReport(new CrashReport("UNAUTHORIZED USER", new NullPointerException()));
                                System.exit(0);
                                break;
                            case "Invalid Data":
                                Minecraft.getMinecraft().displayCrashReport(new CrashReport("INVALID DISCORD USER", new NullPointerException()));
                                System.exit(0);
                                break;
                        }
                        break;
                    }
                case 200:

                    BufferedReader BR = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    JsonParser JsonParser = new JsonParser();
                    JsonObject JsonElement = (JsonObject) JsonParser.parse(BR);
                    BR.close();
                    if (!JsonElement.get("status").getAsString().equalsIgnoreCase("success") && !JsonElement.get("id").getAsString().equalsIgnoreCase(discordID)) {
                        Minecraft.getMinecraft().displayCrashReport(new CrashReport("INVALID SUCCESS", new NullPointerException()));
                    }
                    break;
                case 502:
                default:
                    Minecraft.getMinecraft().displayCrashReport(new CrashReport("Error Occurred During Authentication", new NullPointerException()));
                    System.exit(0);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void checkForUpdates() {
        try {
            URL url = new URL("https://crispyclient.herokuapp.com/checkUpdate/");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            double newversion = Double.parseDouble(reader.readLine());
            if (newversion > getBuild()) {
                downloadLatest();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getAvatarLink(DiscordUser discordUser) {
        if (discordUser != null) {
            return "https://cdn.discordapp.com/avatars/" + discordUser.userId + "/" + discordUser.avatar + ".png?size=128";
        }
        return null;
    }


    public void downloadLatest() {

        File jar = new File(Minecraft.getMinecraft().mcDataDir.toString() + File.separator + "versions" + File.separator + "Crispy" + File.separator + "Crispy.jar");
        try {
            FileDownloader downloadthing = new FileDownloader(jar, "https://crispyclient.herokuapp.com/download/Crispy.jar", "Crispy.jar");
            Thread thread = new Thread(downloadthing);
            thread.start();
        } catch (Exception e) {
            //
        }

    }

    public void loadChangeLog() {
        try {
            URL url = new URL("https://crispyclient.herokuapp.com/changelog");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            changeLog = reader.readLine().split(":");
            sortedChange = changeLog.clone();
            List<String> bop = Arrays.asList(sortedChange);
            bop.sort((o, o2) -> (int) ((int) Crispy.INSTANCE.getFontManager().getFont("ROBO 12").getWidth(o) - Crispy.INSTANCE.getFontManager().getFont("ROBO 12").getWidth(o2)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
