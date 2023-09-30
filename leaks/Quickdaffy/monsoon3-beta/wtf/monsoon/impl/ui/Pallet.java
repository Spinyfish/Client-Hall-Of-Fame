/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 */
package wtf.monsoon.impl.ui;

import java.awt.Color;
import lombok.NonNull;

public class Pallet {
    @NonNull
    Color main;
    @NonNull
    Color mainDarker;
    @NonNull
    Color mainDarkest;
    @NonNull
    Color background;
    @NonNull
    Color misc;

    public Pallet(@NonNull Color main, @NonNull Color mainDarker, @NonNull Color mainDarkest, @NonNull Color background, @NonNull Color misc) {
        if (main == null) {
            throw new NullPointerException("main is marked non-null but is null");
        }
        if (mainDarker == null) {
            throw new NullPointerException("mainDarker is marked non-null but is null");
        }
        if (mainDarkest == null) {
            throw new NullPointerException("mainDarkest is marked non-null but is null");
        }
        if (background == null) {
            throw new NullPointerException("background is marked non-null but is null");
        }
        if (misc == null) {
            throw new NullPointerException("misc is marked non-null but is null");
        }
        this.main = main;
        this.mainDarker = mainDarker;
        this.mainDarkest = mainDarkest;
        this.background = background;
        this.misc = misc;
    }

    @NonNull
    public Color getMain() {
        return this.main;
    }

    @NonNull
    public Color getMainDarker() {
        return this.mainDarker;
    }

    @NonNull
    public Color getMainDarkest() {
        return this.mainDarkest;
    }

    @NonNull
    public Color getBackground() {
        return this.background;
    }

    @NonNull
    public Color getMisc() {
        return this.misc;
    }

    public void setMain(@NonNull Color main) {
        if (main == null) {
            throw new NullPointerException("main is marked non-null but is null");
        }
        this.main = main;
    }

    public void setMainDarker(@NonNull Color mainDarker) {
        if (mainDarker == null) {
            throw new NullPointerException("mainDarker is marked non-null but is null");
        }
        this.mainDarker = mainDarker;
    }

    public void setMainDarkest(@NonNull Color mainDarkest) {
        if (mainDarkest == null) {
            throw new NullPointerException("mainDarkest is marked non-null but is null");
        }
        this.mainDarkest = mainDarkest;
    }

    public void setBackground(@NonNull Color background) {
        if (background == null) {
            throw new NullPointerException("background is marked non-null but is null");
        }
        this.background = background;
    }

    public void setMisc(@NonNull Color misc) {
        if (misc == null) {
            throw new NullPointerException("misc is marked non-null but is null");
        }
        this.misc = misc;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Pallet)) {
            return false;
        }
        Pallet other = (Pallet)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Color this$main = this.getMain();
        Color other$main = other.getMain();
        if (this$main == null ? other$main != null : !((Object)this$main).equals(other$main)) {
            return false;
        }
        Color this$mainDarker = this.getMainDarker();
        Color other$mainDarker = other.getMainDarker();
        if (this$mainDarker == null ? other$mainDarker != null : !((Object)this$mainDarker).equals(other$mainDarker)) {
            return false;
        }
        Color this$mainDarkest = this.getMainDarkest();
        Color other$mainDarkest = other.getMainDarkest();
        if (this$mainDarkest == null ? other$mainDarkest != null : !((Object)this$mainDarkest).equals(other$mainDarkest)) {
            return false;
        }
        Color this$background = this.getBackground();
        Color other$background = other.getBackground();
        if (this$background == null ? other$background != null : !((Object)this$background).equals(other$background)) {
            return false;
        }
        Color this$misc = this.getMisc();
        Color other$misc = other.getMisc();
        return !(this$misc == null ? other$misc != null : !((Object)this$misc).equals(other$misc));
    }

    protected boolean canEqual(Object other) {
        return other instanceof Pallet;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Color $main = this.getMain();
        result = result * 59 + ($main == null ? 43 : ((Object)$main).hashCode());
        Color $mainDarker = this.getMainDarker();
        result = result * 59 + ($mainDarker == null ? 43 : ((Object)$mainDarker).hashCode());
        Color $mainDarkest = this.getMainDarkest();
        result = result * 59 + ($mainDarkest == null ? 43 : ((Object)$mainDarkest).hashCode());
        Color $background = this.getBackground();
        result = result * 59 + ($background == null ? 43 : ((Object)$background).hashCode());
        Color $misc = this.getMisc();
        result = result * 59 + ($misc == null ? 43 : ((Object)$misc).hashCode());
        return result;
    }

    public String toString() {
        return "Pallet(main=" + this.getMain() + ", mainDarker=" + this.getMainDarker() + ", mainDarkest=" + this.getMainDarkest() + ", background=" + this.getBackground() + ", misc=" + this.getMisc() + ")";
    }
}

