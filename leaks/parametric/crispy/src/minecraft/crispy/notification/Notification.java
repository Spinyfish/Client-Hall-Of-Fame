package crispy.notification;


import crispy.fonts.decentfont.MinecraftFontRenderer;
import crispy.fonts.greatfont.TTFFontRenderer;
import crispy.util.animation.Translate;
import crispy.util.time.Stopwatch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;


public final class Notification {
    public static final int HEIGHT = 30;
    private final String title;
    private final String content;
    private final int time;
    private final NotificationType type;
    private final Stopwatch timer;
    private final Translate translate;
    private final TTFFontRenderer fontRenderer;
    public double scissorBoxWidth;

    public Notification(String title, String content, NotificationType type, TTFFontRenderer fontRenderer) {
        this.title = title;
        this.content = content;
        this.time = 2500;
        this.type = type;
        this.timer = new Stopwatch();
        this.fontRenderer = fontRenderer;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        this.translate = new Translate((float)(ScaledResolution.getScaledWidth() - this.getWidth()), (float)(ScaledResolution.getScaledHeight() - 30));
    }


    public Notification(String title, String content, NotificationType type, TTFFontRenderer fontRenderer, int time) {
        this.title = title;
        this.content = content;
        this.time = time;
        this.type = type;
        this.timer = new Stopwatch();
        this.fontRenderer = fontRenderer;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        this.translate = new Translate((float)(ScaledResolution.getScaledWidth() - this.getWidth()), (float)(ScaledResolution.getScaledHeight() - 30));
    }
    public final int getWidth() {
        return (int) Math.max(100, Math.max(this.fontRenderer.getWidth(this.title), this.fontRenderer.getWidth(this.content)) + 20);
    }

    public final String getTitle() {
        return this.title;
    }

    public final String getContent() {
        return this.content;
    }

    public final int getTime() {
        return this.time;
    }

    public final NotificationType getType() {
        return this.type;
    }

    public final Stopwatch getTimer() {
        return this.timer;
    }

    public final Translate getTranslate() {
        return this.translate;
    }
}
