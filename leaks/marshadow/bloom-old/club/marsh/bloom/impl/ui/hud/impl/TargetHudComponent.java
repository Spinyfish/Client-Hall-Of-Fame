package club.marsh.bloom.impl.ui.hud.impl;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.impl.mods.combat.KillAura;
import club.marsh.bloom.impl.ui.hud.Component;
import club.marsh.bloom.impl.ui.hud.HudDesignerUI;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;

import static club.marsh.bloom.impl.mods.render.Hud.rgb;

public class TargetHudComponent extends Component {
    public TargetHudComponent() {
        super("Target Hud",ScaledResolution.getScaledWidth()/2, ScaledResolution.getScaledHeight()/2, true);
    }
    @Override
    public void render() {
        //System.out.println("LOL");
        int calculatedWidth = 0;
        String text = "";
        EntityLivingBase target = KillAura.toggled ? KillAura.target : (getMc().currentScreen instanceof HudDesignerUI ? getMc().thePlayer : null);
        if (target != null) {
            text = target instanceof EntityPlayer ? ((EntityPlayer) (target)).getGameProfile().getName() : target.getName();
            calculatedWidth = renderText(text,getX(),getY(),1);
            int percent = (int) ((target.getHealth()/target.getMaxHealth())*calculatedWidth);
            Gui.drawRect(getX(),getY()+getHeight(),getX()+percent,getY(),Color.getHSBColor(Math.max(0.0F, Math.min(((EntityLivingBase) target).getHealth(), ((EntityLivingBase) target).getMaxHealth()) / ((EntityLivingBase) target).getMaxHealth()) / 3.0F, 1.0F, 0.75F).getRGB());
            renderText(text,getX(),getY(),1);
        }
        setWidth(calculatedWidth);
        setHeight((int) Bloom.INSTANCE.fontManager.defaultFont.getHeight());
    }

    public int renderText(String text, double x,double y,int thing) {
        int ind = 0;
        char[] t = text.toCharArray();
        for (int i = 0; i <= text.length()-1; ++i) {
            getMc().fontRendererObj.drawString(String.valueOf(t[i]), (int) (x+ind),(int) (y), rgb(thing).getRGB());
            ind += getMc().fontRendererObj.getCharWidth(t[i]);
            thing += 1;
        }
        return ind;
    }

    @Override
    protected boolean isHolding(int mouseX, int mouseY) {
        return mouseX >= getX()-5 && mouseY >= getY()-5 && mouseX < getX()+getWidth()+5 && mouseY < getY()+getHeight()+5;
    }
}
