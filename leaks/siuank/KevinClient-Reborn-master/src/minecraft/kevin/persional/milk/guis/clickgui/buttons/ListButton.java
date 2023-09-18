package kevin.persional.milk.guis.clickgui.buttons;

import kevin.persional.milk.guis.font.FontLoaders;
import kevin.persional.milk.utils.key.ClickUtils;
import kevin.module.ListValue;

import java.awt.*;

public class ListButton extends Button {
    public ListValue value;
    int bWidth = 80;
    public ListButton(ListValue value){
        this.value = value;
    }

    @Override
    public void drawButton(int x, int y, int mx, int my, float pticks, float alpha) {
        int intalpha = (int)(alpha * 255);
        String nameText = value.getName() + ":";
        FontLoaders.novo20.drawString(nameText, x, y, new Color(255, 255, 255, intalpha).getRGB());
        int bx = x + (bWidth = Math.max(80, FontLoaders.novo20.getStringWidth(nameText) + 8)), by = y;
        int add = 0;
        for(String mode : value.getValues()){
            String end = mode.equals(value.getValues()[value.getValues().length - 1]) ? "" : ", ";
            String realname = mode + end;
            if(bx + FontLoaders.novo20.getStringWidth(realname) > x + 300){
                by += 10;
                add += 10;
                bx = x + bWidth;
            }
            int color = mode.equals(value.get()) ? new Color(47, 243, 125, intalpha).getRGB() : new Color(100, 100, 100, intalpha).getRGB();
            FontLoaders.novo20.drawString(mode, bx, by, color);
            FontLoaders.novo20.drawString(end, bx + FontLoaders.novo20.getStringWidth(mode), by, new Color(100, 100, 100, intalpha).getRGB());
            bx += FontLoaders.novo20.getStringWidth(realname);
        }
        this.add = add;
        super.drawButton(x, y, mx, my, pticks, alpha);
    }

    @Override
    public void clickButton(int x, int y, int mx, int my) {
        int bx = x + bWidth, by = y;
        for(String mode : value.getValues()){
            String realname = mode + (mode.equals(value.getValues()[value.getValues().length - 1]) ? "" : ", ");
            if(bx + FontLoaders.novo20.getStringWidth(realname) > x + 300){
                by += 10;
                bx = x + bWidth;
            }
            if(ClickUtils.isClickable(bx, by, bx + FontLoaders.novo20.getStringWidth(realname), by + 8, mx, my)){
                value.set(mode);
            }
            bx += FontLoaders.novo20.getStringWidth(realname);
        }
        super.clickButton(x, y, mx, my);
    }
}
