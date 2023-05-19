package club.marsh.bloom.impl.ui.hud.impl;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.impl.events.ClickMouseEvent;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.mods.combat.KillAura;
import club.marsh.bloom.impl.ui.hud.Component;
import club.marsh.bloom.impl.ui.hud.HudDesignerUI;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

import static club.marsh.bloom.impl.mods.render.Hud.rgb;


//https://github.com/PaceCodes/Badlion/blob/master/src/main/java/net/badlion/client/mods/render/Keystrokes.java
public class KeystrokesComponent extends Component {
    public KeystrokesComponent() {
        super("Key Strokes",ScaledResolution.getScaledWidth()/2, 0, true);
        addValue("Mouse", true);

    }

    private transient List<Long> delaysLMB = new ArrayList();
    private transient List<Long> delaysRMB = new ArrayList();

    @Subscribe
    public void onUpdate(UpdateEvent e) {
        final List<Long> newDelaysLMB = new ArrayList<Long>();
        for (final long l : this.delaysLMB) {
            if (System.currentTimeMillis() - l < 1000L) {
                newDelaysLMB.add(l);
            }
        }
        this.delaysLMB = newDelaysLMB;
        final List<Long> newDelaysRMB = new ArrayList<Long>();
        for (final long i : this.delaysRMB) {
            if (System.currentTimeMillis() - i < 1000L) {
                newDelaysRMB.add(i);
            }
        }
        this.delaysRMB = newDelaysRMB;
    }

    @Subscribe
    public void onClick(ClickMouseEvent e) {
        if (e.getClickType() == 0)
        {
            this.delaysLMB.add(Long.valueOf(System.currentTimeMillis()));
        } else if (e.getClickType() == 1)
        {
            this.delaysRMB.add(Long.valueOf(System.currentTimeMillis()));
        }
    }

    public void renderMouse(int xStart, int yStart)
    {
        boolean flag = getMc().gameSettings.keyBindAttack.isKeyDown();
        boolean flag1 = getMc().gameSettings.keyBindUseItem.isKeyDown();
        Gui.drawRect(xStart, yStart, xStart + 23, yStart + 15, flag ?  1728053247 : -1289213133);
        String s = this.getKeyName(getMc().gameSettings.keyBindAttack.getKeyCode());
        boolean flag2 = this.delaysLMB.size() > 0;
        boolean flag3 = this.delaysRMB.size() > 0;
        double d0 = flag2 ? 0.65D : 1.0D;
        double d1 = flag3 ? 0.65D : 1.0D;

        if (flag2)
        {
            s = this.delaysLMB.size() + " CPS";
        }

        GL11.glScaled(d0, d0, d0);

        int i = getMc().fontRendererObj.getStringWidth(s);
        int j = xStart + (23 - i) / 2 + 1;
        int k = yStart + 4;
        j = (int)((double)j / d0);
        k = (int)((double)k / d0);
        if (flag2)
        {
            j += 6;
            k += 2;
        }
        getMc().fontRendererObj.drawString(s, j, k, flag ? -12084809 : -1);

        GL11.glScaled(1.0D / d0, 1.0D / d0, 1.0D / d0);
        xStart = xStart + 24;
        Gui.drawRect(xStart, yStart, xStart + 23, yStart + 15, flag1 ? 1728053247 : -1289213133);
        s = this.getKeyName(getMc().gameSettings.keyBindUseItem.getKeyCode());

        if (flag3)
        {
            s = this.delaysRMB.size() + " CPS";
        }

        GL11.glScaled(d1, d1, d1);
        
        int i1 = getMc().fontRendererObj.getStringWidth(s);
        int l1 = xStart + (23 - i1) / 2 + 1;
        int k2 = yStart + 4;
        l1 = (int)((double)l1 / d1);
        k2 = (int)((double)k2 / d1);
        if (flag3)
        {
            l1 += 6;
            k2 += 2;
        }
        getMc().fontRendererObj.drawString(s, l1, k2, flag1 ? -12084809 : -1);
        GL11.glScaled(1.0D / d1, 1.0D / d1, 1.0D / d1);
    }
    @Override
    public void render() {
        //System.out.println("LOL");
        int height = 0;
        renderWasd(getX(),getY());
        height += 32;
        renderMouse(getX(),getY()+height);
        height += 16;
        setWidth(47);
        setHeight(height);
    }

    private String getKeyName(int keyCode)
    {
        return keyCode < 0 ? (keyCode == -100 ? "LMB" : (keyCode == -99 ? "RMB" : (keyCode == -98 ? "MMB" : "MB" + String.valueOf(keyCode - -99)))) : Keyboard.getKeyName(keyCode);
    }

    public void renderWasd(int xStart, int yStart)
    {
        boolean flag = getMc().gameSettings.keyBindForward.isKeyDown();
        boolean flag1 =getMc().gameSettings.keyBindLeft.isKeyDown();
        boolean flag2 =getMc().gameSettings.keyBindBack.isKeyDown();
        boolean flag3 =getMc().gameSettings.keyBindRight.isKeyDown();
        xStart = xStart + 16;
        this.renderChar(xStart, yStart, this.getKeyName(getMc().gameSettings.keyBindForward.getKeyCode()), flag);
        xStart = xStart - 16;
        yStart = yStart + 16;
        this.renderChar(xStart, yStart, this.getKeyName(getMc().gameSettings.keyBindLeft.getKeyCode()), flag1);
        xStart = xStart + 16;
        this.renderChar(xStart, yStart, this.getKeyName(getMc().gameSettings.keyBindBack.getKeyCode()), flag2);
        xStart = xStart + 16;
        this.renderChar(xStart, yStart, this.getKeyName(getMc().gameSettings.keyBindRight.getKeyCode()), flag3);
    }

    public void renderChar(int x, int y, String keyName, boolean keyDown)
    {
        Gui.drawRect(x, y, x + 15, y + 15, keyDown ? 1728053247 : -1289213133);
            int i = getMc().fontRendererObj.getCharWidth(keyName.charAt(0));
            getMc().fontRendererObj.drawString(keyName, x + (15 - i) / 2 + 1, y + 4, keyDown ? -12084809 : -1);
    }

    @Override
    protected boolean isHolding(int mouseX, int mouseY) {
        return mouseX >= getX()-5 && mouseY >= getY()-5 && mouseX < getX()+getWidth()+5 && mouseY < getY()+getHeight()+5;
    }
}
