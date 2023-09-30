package Reality.Realii.login.utils;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.opengl.GL11;
import Reality.Realii.guis.font.FontLoaders;

import java.awt.*;

public final class PasswordInputBox extends Gui {
    private final int field_146209_f;
    private final int field_146210_g;
    private final FontRenderer field_146211_a;
    private boolean field_146212_n;
    private boolean field_146213_o;
    private int field_146214_l;
    private boolean field_146215_m;
    private String field_146216_j;
    private int field_146217_k;
    private final int field_146218_h;
    private final int field_146219_i;
    private boolean field_146220_v;
    private int field_146221_u;
    private int field_146222_t;
    private int field_146223_s;
    private int field_146224_r;
    private int field_146225_q;
    private boolean field_146226_p;

    public PasswordInputBox(final FontRenderer p_i1032_1_, final int p_i1032_2_, final int p_i1032_3_, final int p_i1032_4_, final int p_i1032_5_) {
        super();
        this.field_146212_n = true;
        this.field_146215_m = true;
        this.field_146216_j = "";
        this.field_146217_k = 32;
        this.field_146220_v = true;
        this.field_146221_u = 7368816;
        this.field_146222_t = 14737632;
        this.field_146226_p = true;
        this.field_146211_a = p_i1032_1_;
        this.field_146209_f = p_i1032_2_;
        this.field_146210_g = p_i1032_3_;
        this.field_146218_h = p_i1032_4_;
        this.field_146219_i = p_i1032_5_;
    }

    public void drawTextBox() {
        if (this.func_146176_q()) {
            if (this.func_146181_i()) {
                //Gui.drawRect(this.field_146209_f - 1, this.field_146210_g - 1, this.field_146209_f + this.field_146218_h + 1, this.field_146210_g + this.field_146219_i + 1, -6250336);
                Gui.drawRect(this.field_146209_f, this.field_146210_g, this.field_146209_f + this.field_146218_h, this.field_146210_g + this.field_146219_i, new Color(240, 240, 240).getRGB());
            }
            final int var1 = this.field_146226_p ? new Color(100, 100, 100).getRGB() : this.field_146221_u;
            final int var2 = this.field_146224_r - this.field_146225_q;
            int var3 = this.field_146223_s - this.field_146225_q;
            final String var4 = this.field_146211_a.trimStringToWidth(this.field_146216_j.substring(this.field_146225_q), this.func_146200_o());
            final boolean var5 = var2 >= 0 && var2 <= var4.length();
            final boolean var6 = this.field_146213_o && this.field_146214_l / 6 % 2 == 0 && var5;
            final int var7 = this.field_146215_m ? (this.field_146209_f + 4) : this.field_146209_f;
            final int var8 = this.field_146215_m ? (this.field_146210_g + (this.field_146219_i - 8) / 2) : this.field_146210_g;
            int var9 = var7;
            if (var3 > var4.length()) {
                var3 = var4.length();
            }
            if (var4.length() > 0) {
                final String var10 = var5 ? var4.substring(0, var2) : var4;
                var9 = (int) FontLoaders.arial22.drawString(var10.replaceAll(".", "*"), var7, var8, var1);
            }
            final boolean var11 = this.field_146224_r < this.field_146216_j.length() || this.field_146216_j.length() >= this.func_146208_g();
            int var12 = var9;
            if (!var5) {
                var12 = ((var2 > 0) ? (var7 + this.field_146218_h) : var7);
            } else if (var11) {
                var12 = var9 - 1;
                --var9;
            }
            if (var4.length() > 0 && var5 && var2 < var4.length()) {
                FontLoaders.arial22.drawString(var4.substring(var2).replaceAll(".", "*"), var9, var8, var1);
            }
            if (var6) {
                if (var11) {
                    Gui.drawRect(var12, var8 - 1, var12 + 1, var8 + 1 + this.field_146211_a.FONT_HEIGHT, -3092272);
                } else {
                    FontLoaders.arial18.drawString("_", var12, var8, var1);
                }
            }
            if (var3 != var2) {
                final int var13 = var7 + this.field_146211_a.getStringWidth(var4.substring(0, var3).replaceAll(".", "*"));
                this.func_146188_c(var12, var8 - 1, var13 - 1, var8 + 1 + this.field_146211_a.FONT_HEIGHT);
            }
        }
    }

    public void func_146175_b(final int p_146175_1_) {
        if (this.field_146216_j.length() != 0) {
            if (this.field_146223_s != this.field_146224_r) {
                this.func_146191_b("");
            } else {
                final boolean var2 = p_146175_1_ < 0;
                final int var3 = var2 ? (this.field_146224_r + p_146175_1_) : this.field_146224_r;
                final int var4 = var2 ? this.field_146224_r : (this.field_146224_r + p_146175_1_);
                String var5 = "";
                if (var3 >= 0) {
                    var5 = this.field_146216_j.substring(0, var3);
                }
                if (var4 < this.field_146216_j.length()) {
                    var5 = String.valueOf(var5) + this.field_146216_j.substring(var4);
                }
                this.field_146216_j = var5;
                if (var2) {
                    this.func_146182_d(p_146175_1_);
                }
            }
        }
    }

    public boolean func_146176_q() {
        return this.field_146220_v;
    }

    public void func_146177_a(final int p_146177_1_) {
        if (this.field_146216_j.length() != 0) {
            if (this.field_146223_s != this.field_146224_r) {
                this.func_146191_b("");
            } else {
                this.func_146175_b(this.func_146187_c(p_146177_1_) - this.field_146224_r);
            }
        }
    }

    public boolean func_146181_i() {
        return this.field_146215_m;
    }

    public void func_146182_d(final int p_146182_1_) {
        this.func_146190_e(this.field_146223_s + p_146182_1_);
    }

    public int func_146183_a(final int p_146183_1_, final int p_146183_2_) {
        return this.func_146197_a(p_146183_1_, this.func_146198_h(), true);
    }

    public void func_146184_c(final boolean p_146184_1_) {
        this.field_146226_p = p_146184_1_;
    }

    public void func_146185_a(final boolean p_146185_1_) {
        this.field_146215_m = p_146185_1_;
    }

    public int func_146186_n() {
        return this.field_146223_s;
    }

    public int func_146187_c(final int p_146187_1_) {
        return this.func_146183_a(p_146187_1_, this.func_146198_h());
    }

    private void func_146188_c(int p_146188_1_, int p_146188_2_, int p_146188_3_, int p_146188_4_) {
        if (p_146188_1_ < p_146188_3_) {
            final int var5 = p_146188_1_;
            p_146188_1_ = p_146188_3_;
            p_146188_3_ = var5;
        }
        if (p_146188_2_ < p_146188_4_) {
            final int var5 = p_146188_2_;
            p_146188_2_ = p_146188_4_;
            p_146188_4_ = var5;
        }
        if (p_146188_3_ > this.field_146209_f + this.field_146218_h) {
            p_146188_3_ = this.field_146209_f + this.field_146218_h;
        }
        if (p_146188_1_ > this.field_146209_f + this.field_146218_h) {
            p_146188_1_ = this.field_146209_f + this.field_146218_h;
        }
        GL11.glColor4f(0.0f, 0.0f, 255.0f, 255.0f);
        GL11.glDisable(3553);
        GL11.glEnable(3058);
        GL11.glLogicOp(5387);
        GL11.glDisable(3058);
        GL11.glEnable(3553);
    }

    public void func_146189_e(final boolean p_146189_1_) {
        this.field_146220_v = p_146189_1_;
    }

    public void func_146190_e(final int p_146190_1_) {
        this.field_146224_r = p_146190_1_;
        final int var2 = this.field_146216_j.length();
        if (this.field_146224_r < 0) {
            this.field_146224_r = 0;
        }
        if (this.field_146224_r > var2) {
            this.field_146224_r = var2;
        }
        this.func_146199_i(this.field_146224_r);
    }

    public void func_146191_b(final String p_146191_1_) {
        String var2 = "";
        final String var3 = ChatAllowedCharacters.filterAllowedCharacters(p_146191_1_);
        final int var4 = (this.field_146224_r < this.field_146223_s) ? this.field_146224_r : this.field_146223_s;
        final int var5 = (this.field_146224_r < this.field_146223_s) ? this.field_146223_s : this.field_146224_r;
        final int var6 = this.field_146217_k - this.field_146216_j.length() - (var4 - this.field_146223_s);
        if (this.field_146216_j.length() > 0) {
            var2 = String.valueOf(var2) + this.field_146216_j.substring(0, var4);
        }
        int var7;
        if (var6 < var3.length()) {
            var2 = String.valueOf(var2) + var3.substring(0, var6);
            var7 = var6;
        } else {
            var2 = String.valueOf(var2) + var3;
            var7 = var3.length();
        }
        if (this.field_146216_j.length() > 0 && var5 < this.field_146216_j.length()) {
            var2 = String.valueOf(var2) + this.field_146216_j.substring(var5);
        }
        this.field_146216_j = var2;
        this.func_146182_d(var4 - this.field_146223_s + var7);
    }

    public void func_146193_g(final int p_146193_1_) {
        this.field_146222_t = p_146193_1_;
    }

    public void func_146196_d() {
        this.func_146190_e(0);
    }

    public int func_146197_a(final int p_146197_1_, final int p_146197_2_, final boolean p_146197_3_) {
        int var4 = p_146197_2_;
        final boolean var5 = p_146197_1_ < 0;
        for (int var6 = Math.abs(p_146197_1_), var7 = 0; var7 < var6; ++var7) {
            if (var5) {
                do {
                    --var4;
                } while (!p_146197_3_ || var4 <= 0 || this.field_146216_j.charAt(var4 - 1) == ' ');
                while (--var4 > 0) {
                    if (this.field_146216_j.charAt(var4 - 1) == ' ') {
                        break;
                    }
                }
            } else {
                final int var8 = this.field_146216_j.length();
                var4 = this.field_146216_j.indexOf(32, var4);
                if (var4 == -1) {
                    var4 = var8;
                } else {
                    while (p_146197_3_ && var4 < var8 && this.field_146216_j.charAt(var4) == ' ') {
                        ++var4;
                    }
                }
            }
        }
        return var4;
    }

    public int func_146198_h() {
        return this.field_146224_r;
    }

    public void func_146199_i(int p_146199_1_) {
        final int var2 = this.field_146216_j.length();
        if (p_146199_1_ > var2) {
            p_146199_1_ = var2;
        }
        if (p_146199_1_ < 0) {
            p_146199_1_ = 0;
        }
        this.field_146223_s = p_146199_1_;
        if (this.field_146211_a != null) {
            if (this.field_146225_q > var2) {
                this.field_146225_q = var2;
            }
            final int var3 = this.func_146200_o();
            final String var4 = this.field_146211_a.trimStringToWidth(this.field_146216_j.substring(this.field_146225_q), var3);
            final int var5 = var4.length() + this.field_146225_q;
            if (p_146199_1_ == this.field_146225_q) {
                this.field_146225_q -= this.field_146211_a.trimStringToWidth(this.field_146216_j, var3, true).length();
            }
            if (p_146199_1_ > var5) {
                this.field_146225_q += p_146199_1_ - var5;
            } else if (p_146199_1_ <= this.field_146225_q) {
                this.field_146225_q -= this.field_146225_q - p_146199_1_;
            }
            if (this.field_146225_q < 0) {
                this.field_146225_q = 0;
            }
            if (this.field_146225_q > var2) {
                this.field_146225_q = var2;
            }
        }
    }

    public int func_146200_o() {
        return this.func_146181_i() ? (this.field_146218_h - 8) : this.field_146218_h;
    }

    public void func_146202_e() {
        this.func_146190_e(this.field_146216_j.length());
    }

    public void func_146203_f(final int p_146203_1_) {
        this.field_146217_k = p_146203_1_;
        if (this.field_146216_j.length() > p_146203_1_) {
            this.field_146216_j = this.field_146216_j.substring(0, p_146203_1_);
        }
    }

    public void func_146204_h(final int p_146204_1_) {
        this.field_146221_u = p_146204_1_;
    }

    public void func_146205_d(final boolean p_146205_1_) {
        this.field_146212_n = p_146205_1_;
    }

    public String func_146207_c() {
        final int var1 = (this.field_146224_r < this.field_146223_s) ? this.field_146224_r : this.field_146223_s;
        final int var2 = (this.field_146224_r < this.field_146223_s) ? this.field_146223_s : this.field_146224_r;
        return this.field_146216_j.substring(var1, var2);
    }

    public int func_146208_g() {
        return this.field_146217_k;
    }

    public String getText() {
        return this.field_146216_j;
    }

    public boolean isFocused() {
        return this.field_146213_o;
    }

    public void mouseClicked(final int p_146192_1_, final int p_146192_2_, final int p_146192_3_) {
        final boolean var4 = p_146192_1_ >= this.field_146209_f && p_146192_1_ < this.field_146209_f + this.field_146218_h && p_146192_2_ >= this.field_146210_g && p_146192_2_ < this.field_146210_g + this.field_146219_i;
        if (this.field_146212_n) {
            this.setFocused(var4);
        }
        if (this.field_146213_o && p_146192_3_ == 0) {
            int var5 = p_146192_1_ - this.field_146209_f;
            if (this.field_146215_m) {
                var5 -= 4;
            }
            final String var6 = this.field_146211_a.trimStringToWidth(this.field_146216_j.substring(this.field_146225_q), this.func_146200_o());
            this.func_146190_e(this.field_146211_a.trimStringToWidth(var6, var5).length() + this.field_146225_q);
        }
    }

    public void setFocused(final boolean p_146195_1_) {
        if (p_146195_1_ && !this.field_146213_o) {
            this.field_146214_l = 0;
        }
        this.field_146213_o = p_146195_1_;
    }

    public void setText(final String p_146180_1_) {
        if (p_146180_1_.length() > this.field_146217_k) {
            this.field_146216_j = p_146180_1_.substring(0, this.field_146217_k);
        } else {
            this.field_146216_j = p_146180_1_;
        }
        this.func_146202_e();
    }

    public boolean textboxKeyTyped(final char p_146201_1_, final int p_146201_2_) {
        if (!this.field_146213_o) {
            return false;
        }
        switch (p_146201_1_) {
            case '\u0001':
                this.func_146202_e();
                this.func_146199_i(0);
                return true;
            case '\u0003':
                GuiScreen.setClipboardString(this.func_146207_c());
                return true;
            case '\u0016':
                if (this.field_146226_p) {
                    this.func_146191_b(GuiScreen.getClipboardString());
                }
                return true;
            case '\u0018':
                GuiScreen.setClipboardString(this.func_146207_c());
                if (this.field_146226_p) {
                    this.func_146191_b("");
                }
                return true;
            default:
                switch (p_146201_2_) {
                    case 14:
                        if (GuiScreen.isCtrlKeyDown()) {
                            if (this.field_146226_p) {
                                this.func_146177_a(-1);
                            }
                        } else if (this.field_146226_p) {
                            this.func_146175_b(-1);
                        }
                        return true;
                    case 199:
                        if (GuiScreen.isShiftKeyDown()) {
                            this.func_146199_i(0);
                        } else {
                            this.func_146196_d();
                        }
                        return true;
                    case 203:
                        if (GuiScreen.isShiftKeyDown()) {
                            if (GuiScreen.isCtrlKeyDown()) {
                                this.func_146199_i(this.func_146183_a(-1, this.func_146186_n()));
                            } else {
                                this.func_146199_i(this.func_146186_n() - 1);
                            }
                        } else if (GuiScreen.isCtrlKeyDown()) {
                            this.func_146190_e(this.func_146187_c(-1));
                        } else {
                            this.func_146182_d(-1);
                        }
                        return true;
                    case 205:
                        if (GuiScreen.isShiftKeyDown()) {
                            if (GuiScreen.isCtrlKeyDown()) {
                                this.func_146199_i(this.func_146183_a(1, this.func_146186_n()));
                            } else {
                                this.func_146199_i(this.func_146186_n() + 1);
                            }
                        } else if (GuiScreen.isCtrlKeyDown()) {
                            this.func_146190_e(this.func_146187_c(1));
                        } else {
                            this.func_146182_d(1);
                        }
                        return true;
                    case 207:
                        if (GuiScreen.isShiftKeyDown()) {
                            this.func_146199_i(this.field_146216_j.length());
                        } else {
                            this.func_146202_e();
                        }
                        return true;
                    case 211:
                        if (GuiScreen.isCtrlKeyDown()) {
                            if (this.field_146226_p) {
                                this.func_146177_a(1);
                            }
                        } else if (this.field_146226_p) {
                            this.func_146175_b(1);
                        }
                        return true;
                    default:
                        if (ChatAllowedCharacters.isAllowedCharacter(p_146201_1_)) {
                            if (this.field_146226_p) {
                                this.func_146191_b(Character.toString(p_146201_1_));
                            }
                            return true;
                        }
                        return false;
                }
        }
    }

    public void updateCursorCounter() {
        ++this.field_146214_l;
    }
}
