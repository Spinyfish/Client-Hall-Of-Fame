package crispy.features.hacks.impl.render;

import arithmo.gui.altmanager.Colors;
import arithmo.gui.altmanager.RenderUtil;
import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.event.EventDirection;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.event.impl.render.EventRenderGui;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.fonts.decentfont.FontUtil;
import crispy.fonts.greatfont.TTFFontRenderer;
import crispy.notification.NotificationPublisher;
import crispy.util.animation.Translate;
import crispy.util.math.Vec4f;
import crispy.util.player.SpeedUtils;
import crispy.util.render.ColorUtils;
import crispy.util.server.packet.PacketHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.Timer;
import net.superblaubeere27.valuesystem.BooleanValue;
import net.superblaubeere27.valuesystem.ModeValue;
import net.superblaubeere27.valuesystem.NumberValue;
import org.lwjgl.opengl.GL11;
import viamcp.ViaMCP;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL11.glEnable;

@HackInfo(name = "HUD", category = Category.RENDER)
public class HUD extends Hack {

    public static BooleanValue fontChat = new BooleanValue("Font Chat", true);
    public static BooleanValue animatedChat = new BooleanValue("Animated Chat", true);
    public static BooleanValue notifications = new BooleanValue("Notifications", true);
    public static Color followColors = new Color(0, 0, 0, 0);
    public ModeValue toggleSounds = new ModeValue("Toggle Sounds", "Click", "Click", "Jello", "None");
    public BooleanValue irc = new BooleanValue("IRC", true);
    ModeValue defaultFont = new ModeValue("Arraylist Font", "Crispy", "Crispy", "Jello", "Comfortaa", "Minecraft", "Karla-Bold", "Karla-Regular", "Gilroy");
    ModeValue colorMode = new ModeValue("Color Mode", "Rainbow", "Rainbow", "Fade", "Random", "Custom", "Astolfo", "Fade2");
    ModeValue animation = new ModeValue("Animation Mode", "Swoop", "Swoop", "Slide");
    NumberValue<Integer> xOffset = new NumberValue<>("X offset", 0, 0, 40);
    NumberValue<Integer> yOffset = new NumberValue<>("Y offset", 0, 0, 40);
    NumberValue<Integer> Red = new NumberValue<Integer>("Red", 0, 0, 255, () -> colorMode.getMode().equalsIgnoreCase("Custom") || colorMode.getMode().equalsIgnoreCase("Fade"));
    NumberValue<Integer> Green = new NumberValue<Integer>("Green", 0, 0, 255, () -> colorMode.getMode().equalsIgnoreCase("Custom") || colorMode.getMode().equalsIgnoreCase("Fade"));
    NumberValue<Integer> Blue = new NumberValue<Integer>("Blue", 0, 0, 255, () -> colorMode.getMode().equalsIgnoreCase("Custom") || colorMode.getMode().equalsIgnoreCase("Fade"));
    NumberValue<Integer> FRed = new NumberValue<Integer>("Fade Red", 0, 0, 255, () -> colorMode.getMode().equalsIgnoreCase("Fade2"));
    NumberValue<Integer> FGreen = new NumberValue<Integer>("Fade Green", 0, 0, 255, () -> colorMode.getMode().equalsIgnoreCase("Fade2"));
    NumberValue<Integer> FBlue = new NumberValue<Integer>("Fade Blue", 0, 0, 255, () -> colorMode.getMode().equalsIgnoreCase("Fade2"));
    NumberValue<Double> backgroundAlpha = new NumberValue<Double>("Background Alpha", 0.2D, 0D, 1.0D);
    BooleanValue playerModel = new BooleanValue("Player Model", false);
    NumberValue<Integer> playerModelX = new NumberValue<Integer>("Player Model X", 0, 0, 500, () -> playerModel.getObject());
    NumberValue<Integer> playerModelY = new NumberValue<Integer>("Player Model Y", 0, 0, 500, () -> playerModel.getObject());
    BooleanValue leftHud = new BooleanValue("Left HUD", false);
    BooleanValue sideBar = new BooleanValue("Side Bar", false);
    BooleanValue outline = new BooleanValue("Outline", true);
    BooleanValue serverInfo = new BooleanValue("Server Info", true);
    BooleanValue backGround = new BooleanValue("Background", true);
    BooleanValue fps = new BooleanValue("FPS", true);
    BooleanValue bps = new BooleanValue("BPS", true);
    BooleanValue showMode = new BooleanValue("Show Mode", true);
    BooleanValue watermark = new BooleanValue("Water Mark", true);
    ModeValue waterMarkText = new ModeValue("Mark Type", "Crispy", () -> watermark.getObject(), "Crispy", "Rise", "Jello", "Exhibition");
    BooleanValue removeRenders = new BooleanValue("Remove Renders", false);
    BooleanValue loadScreen = new BooleanValue("Load Screen", true);
    BooleanValue coords = new BooleanValue("Coordinates", false);
    NumberValue<Integer> coordsX = new NumberValue<Integer>("Coordinates X", 0, 0, 500, () -> coords.getObject());
    NumberValue<Integer> coordsY = new NumberValue<Integer>("Coordinates Y", 0, 0, 500, () -> coords.getObject());
    BooleanValue speedGraph = new BooleanValue("Motion Graph", true);
    NumberValue<Integer> speedX = new NumberValue<>("Motion X", 150, 30, 150, () -> speedGraph.getObject());
    NumberValue<Integer> speedY = new NumberValue<>("Motion Y", 150, 100, 300, () -> speedGraph.getObject());
    NumberValue<Float> thickNess = new NumberValue<Float>("Thickness", 2F, 1F, 3F, () -> speedGraph.getObject());
    BooleanValue followColor = new BooleanValue("Follow Colors", false, () -> speedGraph
            .getObject());
    /**
     * Target HUD vals
     */

    private arithmo.gui.altmanager.Translate opacity = new arithmo.gui.altmanager.Translate(200, 200);
    private boolean shouldAnimate;
    private int oof;
    private float hue;
    private ArrayList<Double> speedList = new ArrayList<>();
    private double lastTick = -1;


    public static int randomNumber(int max, int min) {
        return Math.round(min + (float) Math.random() * ((max - min)));
    }

    public static Color effect(long offset, float brightness, int speed) {
        float hue = (float) (System.nanoTime() + (offset * speed)) / 1.0E10F % 1.0F;
        long color = Long.parseLong(Integer.toHexString(Integer.valueOf(Color.HSBtoRGB(hue, brightness, 1F)).intValue()), 16);
        Color c = new Color((int) color);
        return new Color(c.getGreen() / 255.0f, c.getBlue() / 255.0F, c.getRed() / 255.0F, c.getAlpha() / 255.0F);
    }

    public static Color fade(final Color color, final int index, final int count) {
        final float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs((System.currentTimeMillis() % 2000L / 1000.0f + index / (float) count * 2.0f) % 2.0f - 1.0f);
        brightness = 0.5f + 0.5f * brightness;
        hsb[2] = brightness % 2;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    public static Color astolfo(int index, int speed, float saturation, float brightness, float opacity) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        angle = (angle > 180 ? 360 - angle : angle) + 180;
        float hue = angle / 360f;

        int color = Color.HSBtoRGB(brightness, saturation, hue);
        Color obj = new Color(color);
        return new Color(obj.getRed(), obj.getGreen(), obj.getBlue(), Math.max(0, Math.min(255, (int) (opacity * 255))));
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventRenderGui) {
            EventRenderGui eventRenderGui = (EventRenderGui) e;
            int width = ScaledResolution.getScaledWidth();
            int height = ScaledResolution.getScaledHeight();
            boolean mcFont = defaultFont.getMode().equals("Minecraft");

            TTFFontRenderer fontRenderer = Crispy.INSTANCE.getFontManager().getDefaultFont();

            if (defaultFont.getMode().equalsIgnoreCase("Jello")) {
                fontRenderer = Crispy.INSTANCE.getFontManager().getFont("JELLO1 20");
            } else if (defaultFont.getMode().equalsIgnoreCase("Comfortaa")) {
                fontRenderer = Crispy.INSTANCE.getFontManager().getFont("comfort 20");
            } else if (defaultFont.getMode().equalsIgnoreCase("Karla-Bold")) {
                fontRenderer = Crispy.INSTANCE.getFontManager().getFont("karla-bold 20");
            } else if (defaultFont.getMode().equalsIgnoreCase("Karla-Regular")) {
                fontRenderer = Crispy.INSTANCE.getFontManager().getFont("karla 20");
            } else if (defaultFont.getMode().equalsIgnoreCase("Gilroy")) {
                fontRenderer = Crispy.INSTANCE.getFontManager().getFont("gilroy 20");
            }


            FontRenderer minecraft = Minecraft.fontRendererObj;
            ArrayList<Hack> mods = new ArrayList<>();
            for (Hack m : Crispy.INSTANCE.getHackManager().getHacks()) {
                if (!removeRenders.getObject()) {
                    mods.add(m);
                } else if (m.getCategory() != Category.RENDER) {
                    mods.add(m);
                }
            }
            if (playerModel.getObject()) {
                GuiInventory.drawEntityOnScreen(playerModelX.getObject(), playerModelY.getObject(), 25, 0, 0, mc.thePlayer);

            }
            if (notifications.getObject()) {
                NotificationPublisher.publish(eventRenderGui.getScaledResolution());
            }
            ArrayList<String> components = new ArrayList<>();
            if (fps.getObject()) {
                components.add("fps: ");
            }
            if (bps.getObject()) {
                components.add("bps: ");
            }


            Color defaultColor = new Color(Red.getObject(), Green.getObject(), Blue.getObject());

            int rgb = defaultColor.getRGB();
            switch (colorMode.getObject()) {
                case 1: {
                    rgb = fade(defaultColor, 100, 10).getRGB();
                    break;
                }
                case 3: {
                    rgb = defaultColor.getRGB();
                    break;
                }
                case 4: {
                    rgb = astolfo(17 * -oof, 10, 0.6f, hue, 1f).getRGB();
                    break;
                }
                case 0:
                default: {
                    rgb = Color.HSBtoRGB(hue, 0.7f, 1.0F);
                    break;
                }
            }

            followColors = new Color(rgb);


            if (serverInfo.getObject()) {
                if (mc.thePlayer != null && Minecraft.theWorld != null) {
                    if (Minecraft.getMinecraft().thePlayer.getClientBrand() != null) {
                        Crispy.INSTANCE.getFontManager().getDefaultFont().drawStringWithShadow("\247lServer Info:", 1.5f, 40.0f, rgb);
                        fontRenderer.drawStringWithShadow(String.valueOf(new StringBuilder().append("\2477Spigot: ").append(Minecraft.getMinecraft().thePlayer.getClientBrand().replaceAll("git:", "").replaceAll("Bootstrap", "").replaceAll("SNAPSHOT", "").replaceAll("BungeeCord ", "").replaceAll("Waterfall ", "").replaceAll("Firefall ", "").replaceAll("SkillCord ", "").replaceAll("MelonBungee ", "").replaceAll("unkown", ""))), 5.0f, 50.5f, -1);
                        fontRenderer.drawStringWithShadow(String.valueOf(new StringBuilder().append("\2477Server Address: ").append(Minecraft.getMinecraft().getNetHandler().getNetworkManager().getRemoteAddress().toString())), 5.0f, 60f, -1);
                        fontRenderer.drawStringWithShadow("\2477Server Lag: " + PacketHelper.INSTANCE.getServerLagTime() + "ms", 5.0f, 70f, -1);
                        fontRenderer.drawStringWithShadow("\2477TPS: " + PacketHelper.INSTANCE.getTps(), 5.0f, 80f, -1);
                    }
                }

            }
            if (coords.getObject()) {
                if (mcFont) {
                    minecraft.drawStringWithShadow("\247cX: \247f" + mc.thePlayer.getPosition().getX() + ", \247c" + "Y: \247f" + mc.thePlayer.getPosition().getY() + ", " + "\247cZ: \247f" + mc.thePlayer.getPosition().getZ(), coordsX.getObject(), coordsY.getObject(), -1);
                } else {
                    fontRenderer.drawStringWithShadow("\247cX: \247f" + mc.thePlayer.getPosition().getX() + ", \247c" + "Y: \247f" + mc.thePlayer.getPosition().getY() + ", " + "\247cZ: \247f" + mc.thePlayer.getPosition().getZ(), coordsX.getObject(), coordsY.getObject(), -1);
                }
            }

            if (watermark.getObject()) {
                switch (waterMarkText.getMode()) {
                    case "Rise": {
                        FontUtil.testFont.drawStringWithShadow("Crispy ", 5, 10, rgb);
                        break;
                    }
                    case "Crispy": {
                        Crispy.INSTANCE.getFontManager().getDefaultFont().drawStringWithShadow("Crispy " + Crispy.INSTANCE.getVersion(), 5, 10, rgb);
                        break;
                    }
                    case "Exhibition": {

                        Crispy.INSTANCE.getFontManager().getDefaultFont().drawStringWithShadow("C", 5, 10, rgb);
                        Crispy.INSTANCE.getFontManager().getDefaultFont().drawStringWithShadow("  \247frispy " + "\2477[\247f" + Crispy.INSTANCE.getVersion() + "\2477]", 6, 10, -1);
                        break;
                    }
                    case "Jello": {
                        if (mc.gameSettings.showDebugInfo) {
                            Crispy.INSTANCE.getFontManager().getFont("JELLO1 36").drawStringWithShadow("Crispy ", 450, 10, -1);
                            Crispy.INSTANCE.getFontManager().getFont("JELLO1 16").drawStringWithShadow("Jello ", 450, 30, -1);
                        } else {
                            Crispy.INSTANCE.getFontManager().getFont("JELLO1 36").drawStringWithShadow("Crispy ", 7, 10, -1);
                            Crispy.INSTANCE.getFontManager().getFont("JELLO1 16").drawStringWithShadow("Jello ", 7, 30, -1);
                        }
                        break;
                    }
                }
            }
            // if(bandicam.getObject()) {
            //     if(mc.gameSettings.thirdPersonView <= 0) {
            //         Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("Client/misc/bandicam.png"));
            //         Gui.drawModalRectWithCustomSizedTexture(ScaledResolution.getScaledWidth() / 2 - 200, 1, 400, 100, 400, 100, 400, 100);
            //     }
            // }
            int keep = 0;
            int count = 0;
            if (speedGraph.getObject()) {
                drawGraph();
            }
            for (String comp : components) {

                if (!(mc.currentScreen instanceof GuiChat)) {
                    if (comp.contains("fps")) {
                        Crispy.INSTANCE.getFontManager().getFont("clean 18").drawStringWithShadow("FPS: " + Minecraft.getDebugFPS(), 5 + (count * 40), 490, new Color(255, 0, 0).getRGB());
                    }

                    if (comp.contains("bps")) {
                        Crispy.INSTANCE.getFontManager().getFont("clean 18").drawStringWithShadow("BPS: " + Math.round((SpeedUtils.getSpeed() * 10 * Timer.timerSpeed) * 100f) / 100f, 5 + (count * 40), 490, new Color(255, 0, 0).getRGB());
                    }


                    if (comp.contains("build")) {
                        Crispy.INSTANCE.getFontManager().getFont("clean 18").drawStringWithShadow("Build: " + Crispy.INSTANCE.getBuild(), 5 + (count * 40), 490, new Color(255, 0, 0).getRGB());

                    }
                    if (comp.contains("Version")) {
                        Crispy.INSTANCE.getFontManager().getFont("clean 18").drawStringWithShadow("Version: " + ViaMCP.getInstance().getVersion(), 5 + (count * 40), 490, new Color(255, 0, 0).getRGB());
                    }
                }
                count++;
                if (count == 3) {
                    count = 4;
                }

            }
            if (!mcFont) {
                TTFFontRenderer finalFontRenderer = fontRenderer;
                mods.sort((o1, o2) -> {
                    return (int) (finalFontRenderer.getWidth(!showMode.getObject() ? o2.getName() : o2.getDisplayName()) - finalFontRenderer.getWidth(!showMode.getObject() ? o1.getName() : o1.getDisplayName()));
                });
            } else {
                mods.sort((o1, o2) -> {
                    return minecraft.getStringWidth(!showMode.getObject() ? o2.getName() : o2.getDisplayName()) - minecraft.getStringWidth(!showMode.getObject() ? o1.getName() : o1.getDisplayName());
                });
            }


            float translationFactor = (float) (14.4 / (float) Minecraft.getDebugFPS());
            int listOffSet = 10;
            if (defaultFont.getMode().equalsIgnoreCase("Jello")) {
                listOffSet = 12;
            }
            int y = 0;
            this.hue += translationFactor / 100.0F;
            if (this.hue > 1.0F) {
                this.hue = 0.0F;
            }

            float h = this.hue;
            glEnable(3042);
            int i = 0;
            this.oof = 0;
            int yText = 2;
            for (int sortedListSize = mods.size(); i < sortedListSize; i++) {

                Hack hack = mods.get(i);
                Color modListColor = new Color(Red.getObject(), Green.getObject(), Blue.getObject());
                Translate translate = hack.getTranslate();
                String displayName = hack.getDisplayName();
                String name = hack.getName();
                float length = mcFont ? minecraft.getStringWidth(!showMode.getObject() ? name : displayName) : fontRenderer.getWidth(!showMode.getObject() ? name : displayName);
                float featureX = leftHud.getObject() ? 1 : width - length - 2.0F;
                boolean enable = hack.isEnabled();
                if (enable) {
                    if (animation.getMode().equalsIgnoreCase("Swoop")) {
                        translate.interpolate(featureX, y + 1, translationFactor);
                    } else {
                        translate.interpolate(featureX, y + 1, translationFactor);
                    }
                } else {

                    if (animation.getMode().equalsIgnoreCase("Swoop")) {
                        translate.interpolate(leftHud.getObject() ? -length - (xOffset.getObject() + 5) : width, -listOffSet - 1, translationFactor);
                    } else {
                        translate.interpolate(leftHud.getObject() ? -length - (xOffset.getObject() + 5) : (double) width + 20, y, translationFactor);
                    }
                }

                int index = mods.indexOf(hack);
                double translateX = translate.getX();
                double translateY = translate.getY();
                boolean visible = translateY > (-listOffSet);
                if (visible) {
                    int color = modListColor.getRGB();
                    switch (colorMode.getObject()) {
                        case 1: {
                            color = fade(modListColor, index, mods.indexOf(hack) + 10).getRGB();
                            break;
                        }
                        case 2: {
                            color = hack.color;
                            break;
                        }
                        case 3: {
                            color = modListColor.getRGB();
                            break;
                        }
                        case 4: {
                            color = astolfo(index * 10, 10, 0.6f, hue, 1f).getRGB();
                            break;
                        }
                        case 5: {
                            int speed = (1000 * (2 > 0 ? 2 : 1));
                            color = fade((System.currentTimeMillis() + index * 30) % speed / (speed / 2.0f));
                            break;
                        }
                        case 0:
                        default: {
                            color = Color.HSBtoRGB(h, 0.7f, 1.0F);
                            break;
                        }
                    }

                    Color fixColor = new Color(color);

                    GlStateManager.color(fixColor.getRed(), fixColor.getGreen(), fixColor.getBlue());
                    Gui.drawRect(0.0, 0.0, 0.0, 0.0, fixColor.getRGB());

                    int nextIndex = index + 1;
                    Hack nextModule = null;
                    if (mods.size() > nextIndex) {
                        nextModule = this.getNextEnabledModule(mods, nextIndex);
                    }
                    if (backGround.getObject()) {

                        Gui.drawRect(leftHud.getObject() ? translateX - 2.0 + xOffset.getObject() : translateX - 2.0 - xOffset.getObject(), translateY - 1.0D + yOffset.getObject(), leftHud.getObject() ? translateX + length + xOffset.getObject() + 2 : width - xOffset.getObject(), translateY + (double) listOffSet - 1.0 + yOffset.getObject(), (new Color(13, 13, 13, (int) (255.0F * backgroundAlpha.getObject().floatValue()))).getRGB());
                    }
                    if (sideBar.getObject()) {
                        Gui.drawRect(leftHud.getObject() ? xOffset.getObject() : translateX + (double) length + 1.0D - xOffset.getObject(), translateY - 1.0D + yOffset.getObject(), leftHud.getObject() ? 1 : width, translateY + (double) listOffSet - 1.0 + yOffset.getObject(), color);
                    }
                    if (outline.getObject()) {
                        Gui.drawRect(leftHud.getObject() ? translateX + length + xOffset.getObject() + 1 : translateX - 2.5D - xOffset.getObject(), translateY - 1.0D + yOffset.getObject(), leftHud.getObject() ? translateX + length + xOffset.getObject() + 1.5f : translateX - 2.0D - xOffset.getObject(), translateY + (double) listOffSet - 1.0D + yOffset.getObject(), color);
                        double offsetY = listOffSet;
                        if (nextModule != null) {
                            double dif = mcFont ? (length - minecraft.getStringWidth(!showMode.getObject() ? nextModule.getName() : nextModule.getDisplayName())) : (length - fontRenderer.getWidth(!showMode.getObject() ? nextModule.getName() : nextModule.getDisplayName()));
                            Gui.drawRect(leftHud.getObject() ? translateX + length + xOffset.getObject() + 1 : translateX - 2.5D - xOffset.getObject(), translateY + offsetY - 1.0D + yOffset.getObject(), leftHud.getObject() ? translateX + length + xOffset.getObject() + 1.5 - dif : translateX - 2.5D + dif - xOffset.getObject(), translateY + offsetY - 0.5D + yOffset.getObject(), color);
                        } else {

                            Gui.drawRect(leftHud.getObject() ? translateX - 2.5D + xOffset.getObject() : translateX - 2.5D - xOffset.getObject(), translateY + offsetY - 1.0D + yOffset.getObject(), leftHud.getObject() ? length + 2 + xOffset.getObject() : width - xOffset.getObject(), translateY + offsetY - 0.5D + yOffset.getObject(), color);
                        }
                    }
                    if (!mcFont) {
                        if (showMode.getObject()) {
                            fontRenderer.drawStringWithShadow(displayName, leftHud.getObject() ? (float) (translateX + xOffset.getObject()) : (float) translateX - xOffset.getObject(), (float) translateY + yOffset.getObject(), color);
                        } else {
                            fontRenderer.drawStringWithShadow(name, leftHud.getObject() ? (float) (translateX + xOffset.getObject()) : (float) translateX - xOffset.getObject(), (float) translateY + yOffset.getObject(), color);
                        }
                    } else {
                        if (showMode.getObject()) {
                            minecraft.drawStringWithShadow(displayName, leftHud.getObject() ? (float) (translateX + xOffset.getObject()) : (float) translateX - xOffset.getObject(), (float) translateY + yOffset.getObject(), color);
                        } else {
                            minecraft.drawStringWithShadow(name, leftHud.getObject() ? (float) (translateX + xOffset.getObject()) : (float) translateX - xOffset.getObject(), (float) translateY + yOffset.getObject(), color);


                        }
                    }
                    if (hack.isEnabled()) {
                        y += listOffSet;
                    }

                    h += translationFactor / 6.0F;
                }

                yText += 12;
                this.oof++;
            }

            if (loadScreen.getObject()) {

                opacity.interpolate(0, 0, 2);
                Gui.drawRect(0, 0, ScaledResolution.getScaledWidth(), ScaledResolution.getScaledHeight(), Colors.getColor(0, 0, 0, (int) opacity.getX()));


            }


        } else if (e instanceof EventPacket) {
            Packet packet = ((EventPacket) e).getPacket();
            if (((EventPacket) e).getDirection() == EventDirection.Incoming) {
                PacketHelper.INSTANCE.onPacketReceive(packet);
            }
            if (packet instanceof S07PacketRespawn) {
                shouldAnimate = true;


            } else if (packet instanceof S08PacketPlayerPosLook) {
                if (shouldAnimate) {
                    shouldAnimate = false;
                    opacity = new arithmo.gui.altmanager.Translate(255, 255);
                }
            }
        } else if (e instanceof EventUpdate) {
            PacketHelper.INSTANCE.onUpdate();
        }
    }

    private int getStaticRainbow(int speed, int offset) {
        float hue = 5000 + (System.currentTimeMillis() + offset) % speed;
        hue /= 5000;
        return Color.getHSBColor(hue, 0.65f, (float) .9).getRGB();
    }

    private ArrayList<Hack> getSortedModules(TTFFontRenderer fr) {
        ArrayList<Hack> sortedList = Crispy.INSTANCE.getHackManager().getHacks();
        sortedList.sort(Comparator.comparingDouble(value -> {
            return -fr.getWidth(value.getDisplayName());
        }));
        return (ArrayList<Hack>) sortedList.clone();
    }

    private Hack getNextEnabledModule(List modules, int startingIndex) {
        int i = startingIndex;

        for (int modulesSize = modules.size(); i < modulesSize; ++i) {
            Hack module = (Hack) modules.get(i);
            if (module.isEnabled()) {
                return module;
            }
        }

        return null;
    }


    public int fade(float time) {
        if (time > 1) time = 1 - time % 1;

        Vec4f from = ColorUtils.getColor(new Color(Red.getObject(), Green.getObject(), Blue.getObject()).getRGB());
        Vec4f to = ColorUtils.getColor(new Color(FRed.getObject(), FGreen.getObject(), FBlue.getObject()).getRGB());

        Vec4f diff = to.clone().sub(from);
        Vec4f newColor = from.clone().add(diff.clone().mul(time));

        return ColorUtils.getColor(newColor);
    }


    public void drawGraph() {

        Color defaultColor = new Color(Red.getObject(), Green.getObject(), Blue.getObject());

        int rgb = defaultColor.getRGB();
        switch (colorMode.getObject()) {
            case 1: {
                rgb = fade(defaultColor, 100, 10).getRGB();
                break;
            }
            case 3: {
                rgb = defaultColor.getRGB();
                break;
            }
            case 4: {
                rgb = astolfo(17 * -oof, 10, 0.6f, hue, 1f).getRGB();
                break;
            }
            case 0:
            default: {
                rgb = Color.HSBtoRGB(hue, 0.7f, 1.0F);
                break;
            }
        }
        float width = this.speedX.getObject();
        if (lastTick != mc.thePlayer.ticksExisted) {
            lastTick = mc.thePlayer.ticksExisted;
            double z2 = mc.thePlayer.posZ;
            double z1 = mc.thePlayer.prevPosZ;
            double x2 = mc.thePlayer.posX;
            double x1 = mc.thePlayer.prevPosX;
            double speed = Math.sqrt((z2 - z1) * (z2 - z1) + (x2 - x1) * (x2 - x1));
            if (speed < 0)
                speed = -speed;

            speedList.add(speed);
            while (speedList.size() > width) {
                speedList.remove(0);
            }
        }
        //110.0
        GlStateManager.pushMatrix();
        float middleY = ScaledResolution.getScaledHeight() / 2;
        float middle = ((ScaledResolution.getScaledWidth() / 2) - width) + 70;
        GlStateManager.translate(middle, middleY - 100, 0);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(thickNess.getObject());
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);

        GL11.glBegin(GL11.GL_LINES);

        int size = speedList.size();

        double start = size > width ? size - width : 0;
        for (double i = start; i < size - 1; i++) {
            double y = speedList.get((int) i) * 10 * 7;
            double y1 = speedList.get((int) (i + 1)) * 10 * 7;

            RenderUtil.glColor(followColor.getObject() ? rgb : new Color(Red.getObject(), Green.getObject(), Blue.getObject(), 255).getRGB());

            if (y > 50) {
                y = 50;
            }
            if (y1 > 50) {
                y1 = 50;
            }
            GL11.glVertex2d(i - start, speedY.getObject() + 1 - y);
            GL11.glVertex2d(i + 1.0 - start, speedY.getObject() + 1 - y1);
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.resetColor();
        GlStateManager.popMatrix();
    }
}

