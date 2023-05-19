package club.marsh.bloom.impl.ui.hud.impl;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.impl.ui.hud.Component;
import club.marsh.bloom.impl.utils.other.UserData;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

import static club.marsh.bloom.impl.mods.render.Hud.rgb;

public class WatermarkComponent extends Component {
    public WatermarkComponent() {
        super("Watermark",0,0, true);
        addValue("blooom",false);
    }
    @Override
    public void render() {
        setWidth(renderText("bloom | " + UserData.INSTANCE.discordName, getX(), getY()-2, 1));
        setHeight((int) Bloom.INSTANCE.fontManager.defaultFont.getHeight()-2);
        //System.out.println("LOL");
        if (getValue("blooom")) {
            Bloom.INSTANCE.bloomHandler.bloom(getX(),getY(),getWidth(),getHeight(),15,new Color(0,0,0,135));
            GlStateManager.resetColor();
        }
    }

    public int renderText(String text, double x,double y,int thing) {
        int ind = 0;
        boolean hasChangedColor = false;
        char[] t = text.toCharArray();
        for (int i = 0; i <= text.length()-1; ++i) {
            if (String.valueOf(t[i]).contains("\247"))
                hasChangedColor = true;
            if (hasChangedColor)
                break;
            //getMc().fontRendererObj.drawString(String.valueOf(t[i]), (int) (x+ind),(int) (y), rgb(thing).getRGB());
            Bloom.INSTANCE.fontManager.defaultFont.drawStringWithShadow(String.valueOf(t[i]), (int) (x+ind),(int) (y), rgb(thing));
            ind += Bloom.INSTANCE.fontManager.defaultFont.getWidth(String.valueOf(t[i]))-2;
            thing += 1;
        }
        if (hasChangedColor) {
            //getMc().fontRendererObj.drawString( String.valueOf(text.split("\247")[1]), (int) (x+ind),(int) (y), rgb(thing).getRGB());
            Bloom.INSTANCE.fontManager.defaultFont.drawStringWithShadow(String.valueOf(text.split("\247")[1]), (int) (x+ind),(int) (y), rgb(thing));
            ind += Bloom.INSTANCE.fontManager.defaultFont.getWidth(text.split("\247")[1]);
        }
        return ind;
    }

    @Override
    protected boolean isHolding(int mouseX, int mouseY) {
        return mouseX >= getX()-5 && mouseY >= getY()-5 && mouseX < getX()+getWidth()+5 && mouseY < getY()+getHeight()+5;
    }
}
