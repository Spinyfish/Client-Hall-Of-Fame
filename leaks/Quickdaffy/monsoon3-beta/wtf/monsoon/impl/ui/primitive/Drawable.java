/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.ui.primitive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import wtf.monsoon.impl.ui.primitive.Click;

public abstract class Drawable {
    private float x;
    private float y;
    private float width;
    private float height;
    protected Minecraft mc = Minecraft.getMinecraft();
    protected FontRenderer fr;

    public Drawable(float x, float y, float width, float height) {
        this.fr = this.mc.fontRendererObj;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void draw(float var1, float var2, int var3);

    public abstract boolean mouseClicked(float var1, float var2, Click var3);

    public abstract void mouseReleased(float var1, float var2, Click var3);

    public abstract void keyTyped(char var1, int var2);

    public float getOffset() {
        return 0.0f;
    }

    public boolean hovered(float mouseX, float mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public Minecraft getMc() {
        return this.mc;
    }

    public FontRenderer getFr() {
        return this.fr;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setMc(Minecraft mc) {
        this.mc = mc;
    }

    public void setFr(FontRenderer fr) {
        this.fr = fr;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Drawable)) {
            return false;
        }
        Drawable other = (Drawable)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Float.compare(this.getX(), other.getX()) != 0) {
            return false;
        }
        if (Float.compare(this.getY(), other.getY()) != 0) {
            return false;
        }
        if (Float.compare(this.getWidth(), other.getWidth()) != 0) {
            return false;
        }
        if (Float.compare(this.getHeight(), other.getHeight()) != 0) {
            return false;
        }
        Minecraft this$mc = this.getMc();
        Minecraft other$mc = other.getMc();
        if (this$mc == null ? other$mc != null : !this$mc.equals(other$mc)) {
            return false;
        }
        FontRenderer this$fr = this.getFr();
        FontRenderer other$fr = other.getFr();
        return !(this$fr == null ? other$fr != null : !this$fr.equals(other$fr));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Drawable;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Float.floatToIntBits(this.getX());
        result = result * 59 + Float.floatToIntBits(this.getY());
        result = result * 59 + Float.floatToIntBits(this.getWidth());
        result = result * 59 + Float.floatToIntBits(this.getHeight());
        Minecraft $mc = this.getMc();
        result = result * 59 + ($mc == null ? 43 : $mc.hashCode());
        FontRenderer $fr = this.getFr();
        result = result * 59 + ($fr == null ? 43 : $fr.hashCode());
        return result;
    }

    public String toString() {
        return "Drawable(x=" + this.getX() + ", y=" + this.getY() + ", width=" + this.getWidth() + ", height=" + this.getHeight() + ", mc=" + this.getMc() + ", fr=" + this.getFr() + ")";
    }
}

