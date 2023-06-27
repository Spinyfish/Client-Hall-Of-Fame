package rip.athena.client.modules.impl.render;

import rip.athena.client.config.*;
import java.awt.*;
import rip.athena.client.gui.hud.*;
import rip.athena.client.modules.*;
import java.util.concurrent.*;
import rip.athena.client.events.types.network.*;
import net.minecraft.network.play.server.*;
import rip.athena.client.events.*;
import org.lwjgl.opengl.*;
import rip.athena.client.utils.render.*;
import java.util.*;

public class TPS extends Module
{
    @ConfigValue.List(name = "Display Mode", values = { "Circle", "Modern", "Fade", "Old" }, description = "Chose display of background")
    public static String backgroundMode;
    @ConfigValue.Boolean(name = "Preset Color")
    private boolean presetColor;
    @ConfigValue.Color(name = "TPS Color")
    private Color tpsColor;
    @ConfigValue.List(name = "Mode", values = { "Simple", "Number", "Graph", "Both" })
    private String mode;
    @ConfigValue.Boolean(name = "Custom Font")
    public static boolean customFont;
    @ConfigValue.Boolean(name = "Background")
    private boolean backGround;
    @ConfigValue.Color(name = "Background Color")
    private Color bColor;
    private List<Float> lastMeasurements;
    private long lastTime;
    private boolean first;
    private float tps;
    private Color color;
    private int count;
    private HUDElement hud;
    private int width;
    private int height;
    
    public TPS() {
        super("TPS", Category.RENDER, "Athena/gui/mods/tps.png");
        this.presetColor = true;
        this.tpsColor = Color.WHITE;
        this.mode = "Number";
        this.backGround = true;
        this.bColor = new Color(0, 0, 0, 90);
        this.lastMeasurements = new CopyOnWriteArrayList<Float>();
        this.lastTime = 0L;
        this.first = true;
        this.width = 56;
        this.height = 18;
        this.addHUD(this.hud = new HUDElement("tps", this.width, this.height) {
            @Override
            public void onRender() {
                TPS.this.render();
            }
        });
    }
    
    @SubscribeEvent
    public void onPacket(final IngoingPacketEvent event) {
        if (event.getPacket() instanceof S03PacketTimeUpdate) {
            if (this.lastTime != 0L) {
                if (this.first) {
                    this.first = false;
                    return;
                }
                float tps = 20.0f * (1000.0f / (System.currentTimeMillis() - this.lastTime));
                if (tps > 20.0f) {
                    tps = 20.0f;
                }
                if (this.lastMeasurements.isEmpty()) {
                    for (int i = 0; i < 49; ++i) {
                        this.lastMeasurements.add(20.0f);
                    }
                }
                if (this.lastMeasurements.size() > 56) {
                    this.lastMeasurements.remove(0);
                }
                this.lastMeasurements.add(tps);
            }
            this.lastTime = System.currentTimeMillis();
        }
    }
    
    public void render() {
        GL11.glPushMatrix();
        final int posX = this.hud.getX();
        final int posY = this.hud.getY();
        try {
            if (this.lastMeasurements.size() > 0) {
                final Float value = this.lastMeasurements.get(this.lastMeasurements.size() - 1);
                if (value != null) {
                    if (value < 17.0f && value >= 15.0f) {
                        this.color = Color.YELLOW;
                    }
                    else if (value < 15.0f && value >= 12.0f) {
                        this.color = Color.ORANGE;
                    }
                    else if (value < 12.0f) {
                        this.color = Color.RED;
                    }
                    else {
                        this.color = Color.GREEN;
                    }
                    this.tps = value;
                }
                else {
                    this.color = Color.GREEN;
                    this.tps = 20.0f;
                }
            }
            else {
                this.color = Color.GREEN;
                this.tps = 20.0f;
            }
        }
        catch (Exception ex) {}
        final String string = (int)this.tps + " TPS";
        if (this.mode.equalsIgnoreCase("Number")) {
            HUDUtil.drawHUD(string, posX, posY, this.width, this.height, this.bColor, this.backGround, this.presetColor ? this.color : this.tpsColor, TPS.customFont);
        }
        if (this.mode.equalsIgnoreCase("Graph")) {
            HUDUtil.drawHUD("", posX, posY, this.width, this.height, this.bColor, this.backGround, this.presetColor ? this.color : this.tpsColor, TPS.customFont);
        }
        if (this.mode.equalsIgnoreCase("Both")) {
            HUDUtil.drawHUD(string, posX, posY, this.width, this.height, this.bColor, this.backGround, this.presetColor ? this.color : this.tpsColor, TPS.customFont);
            HUDUtil.drawHUD("", posX, posY + this.height, this.width, 10, this.bColor, this.backGround, this.presetColor ? this.color : this.tpsColor, TPS.customFont);
        }
        if (this.mode.equalsIgnoreCase("Both")) {
            this.hud.setHeight(10 + this.height);
        }
        else {
            this.hud.setHeight(this.height);
        }
        this.hud.setWidth(this.width);
        if (this.mode.equalsIgnoreCase("Simple")) {
            HUDUtil.drawString("[" + string + "]", posX, posY, this.presetColor ? this.color : this.tpsColor, TPS.customFont);
            this.hud.setHeight(TPS.mc.fontRendererObj.FONT_HEIGHT);
            this.hud.setWidth(TPS.mc.fontRendererObj.getStringWidth("[" + string + "]"));
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        float x = 14.0f;
        float lastValue = 0.0f;
        int i = 0;
        if (this.mode.equalsIgnoreCase("Graph") || this.mode.equalsIgnoreCase("Both")) {
            GL11.glPushMatrix();
            GL11.glDisable(2884);
            GL11.glDisable(3553);
            GL11.glLineWidth(1.0f);
            for (final float value2 : this.lastMeasurements) {
                GL11.glBegin(1);
                Color color;
                if (value2 < 17.0f && value2 >= 15.0f) {
                    color = Color.YELLOW;
                }
                else if (value2 < 15.0f && value2 >= 12.0f) {
                    color = Color.ORANGE;
                }
                else if (value2 < 12.0f) {
                    color = Color.RED;
                }
                else {
                    color = Color.GREEN;
                }
                GL11.glColor3f((float)color.getRed(), (float)color.getGreen(), (float)color.getBlue());
                if (this.mode.equalsIgnoreCase("Graph")) {
                    if (i > 0) {
                        GL11.glVertex2f(70 + posX - (x - 1.0f), posY + 20 + this.height / 2 - lastValue);
                        GL11.glVertex2f(70 + posX - x, posY + 20 + this.height / 2 - value2);
                    }
                    else {
                        GL11.glVertex2f(70 + posX - x, posY + 20 + this.height / 2 - value2);
                        GL11.glVertex2f(70 + posX - x, posY + 20 + this.height / 2 - value2);
                    }
                }
                else if (i > 0) {
                    GL11.glVertex2f(70 + posX - (x - 1.0f), posY + 20 + this.height - lastValue);
                    GL11.glVertex2f(70 + posX - x, posY + 20 + this.height - value2);
                }
                else {
                    GL11.glVertex2f(70 + posX - x, posY + 20 + this.height - value2);
                    GL11.glVertex2f(70 + posX - x, posY + 20 + this.height - value2);
                }
                GL11.glEnd();
                lastValue = value2;
                ++x;
                ++i;
            }
            GL11.glEnable(3553);
            GL11.glEnable(2884);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
    }
    
    static {
        TPS.backgroundMode = "Circle";
        TPS.customFont = false;
    }
}
