/*
 * Decompiled with CFR 0.152.
 */
package wtf.opengui;

import wtf.opengui.CStyle;
import wtf.opengui.IRenderer;
import wtf.opengui.prims.EleBox;

public abstract class Element {
    String id;
    String type;
    IRenderer renderer;
    float x = 0.0f;
    float y = 0.0f;
    float w;
    float h;
    int mx;
    int my;
    boolean click_down;
    CStyle style = new CStyle();

    public abstract void render(int var1, int var2);

    public abstract void mouseClicked(int var1, int var2, int var3);

    public abstract void mouseReleased(int var1, int var2, int var3);

    protected boolean hovered() {
        return (float)this.mx >= this.x && (float)this.mx <= this.x + this.w && (float)this.my >= this.y && this.y <= this.y + this.h;
    }

    public Element setID(String id) {
        this.id = id;
        return this;
    }

    public static Element getByType(String type) {
        switch (type) {
            case "box": {
                return new EleBox();
            }
        }
        return null;
    }

    public String getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public IRenderer getRenderer() {
        return this.renderer;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getW() {
        return this.w;
    }

    public float getH() {
        return this.h;
    }

    public int getMx() {
        return this.mx;
    }

    public int getMy() {
        return this.my;
    }

    public boolean isClick_down() {
        return this.click_down;
    }

    public CStyle getStyle() {
        return this.style;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRenderer(IRenderer renderer) {
        this.renderer = renderer;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setW(float w) {
        this.w = w;
    }

    public void setH(float h) {
        this.h = h;
    }

    public void setMx(int mx) {
        this.mx = mx;
    }

    public void setMy(int my) {
        this.my = my;
    }

    public void setClick_down(boolean click_down) {
        this.click_down = click_down;
    }

    public void setStyle(CStyle style) {
        this.style = style;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Element)) {
            return false;
        }
        Element other = (Element)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (Float.compare(this.getX(), other.getX()) != 0) {
            return false;
        }
        if (Float.compare(this.getY(), other.getY()) != 0) {
            return false;
        }
        if (Float.compare(this.getW(), other.getW()) != 0) {
            return false;
        }
        if (Float.compare(this.getH(), other.getH()) != 0) {
            return false;
        }
        if (this.getMx() != other.getMx()) {
            return false;
        }
        if (this.getMy() != other.getMy()) {
            return false;
        }
        if (this.isClick_down() != other.isClick_down()) {
            return false;
        }
        String this$id = this.getId();
        String other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        String this$type = this.getType();
        String other$type = other.getType();
        if (this$type == null ? other$type != null : !this$type.equals(other$type)) {
            return false;
        }
        IRenderer this$renderer = this.getRenderer();
        IRenderer other$renderer = other.getRenderer();
        if (this$renderer == null ? other$renderer != null : !this$renderer.equals(other$renderer)) {
            return false;
        }
        CStyle this$style = this.getStyle();
        CStyle other$style = other.getStyle();
        return !(this$style == null ? other$style != null : !this$style.equals(other$style));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Element;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + Float.floatToIntBits(this.getX());
        result = result * 59 + Float.floatToIntBits(this.getY());
        result = result * 59 + Float.floatToIntBits(this.getW());
        result = result * 59 + Float.floatToIntBits(this.getH());
        result = result * 59 + this.getMx();
        result = result * 59 + this.getMy();
        result = result * 59 + (this.isClick_down() ? 79 : 97);
        String $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        String $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        IRenderer $renderer = this.getRenderer();
        result = result * 59 + ($renderer == null ? 43 : $renderer.hashCode());
        CStyle $style = this.getStyle();
        result = result * 59 + ($style == null ? 43 : $style.hashCode());
        return result;
    }

    public String toString() {
        return "Element(id=" + this.getId() + ", type=" + this.getType() + ", renderer=" + this.getRenderer() + ", x=" + this.getX() + ", y=" + this.getY() + ", w=" + this.getW() + ", h=" + this.getH() + ", mx=" + this.getMx() + ", my=" + this.getMy() + ", click_down=" + this.isClick_down() + ", style=" + this.getStyle() + ")";
    }
}

