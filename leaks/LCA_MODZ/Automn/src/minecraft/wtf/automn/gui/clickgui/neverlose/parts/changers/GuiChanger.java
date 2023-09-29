package wtf.automn.gui.clickgui.neverlose.parts.changers;

import net.minecraft.client.gui.Gui;
import wtf.automn.fontrenderer.ClientFont;
import wtf.automn.fontrenderer.GlyphPageFontRenderer;
import wtf.automn.gui.Position;
import wtf.automn.module.settings.Setting;

import java.awt.*;

public abstract class GuiChanger<V extends Setting> extends Gui{

    public V setting;

    private float x, y;
    private int width, height;

    public Position pos;

    protected GlyphPageFontRenderer settingFont = ClientFont.font(20, "Arial", true);

    public static final int INACTIVE_COLOR = Color.decode("#93A9B6").getRGB();
    public static final int ACTIVE_COLOR = Color.decode("#FFFFFF").getRGB();

    public GuiChanger(V s, float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.setting = s;
        this.pos = new Position(getX(), getY(), getWidth(), getHeight());
    }

    public abstract void draw(int mouseX, int mouseY);
    public void update() {
        pos = new Position(getX(), getY(), getWidth(), getHeight());
    }
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}

