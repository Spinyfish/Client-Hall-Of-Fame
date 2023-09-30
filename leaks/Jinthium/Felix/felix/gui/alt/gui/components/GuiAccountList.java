package felix.gui.alt.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.VanillaFontRenderer;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;

import com.mojang.realmsclient.gui.ChatFormatting;

import felix.Client;
import felix.gui.alt.gui.GuiAltManager;
import felix.gui.alt.system.Account;
import felix.util.font.FontRenderer;
import felix.util.visual.Colors;
import felix.util.visual.RenderUtil;

public class GuiAccountList extends GuiSlot {

    public int selected = -1;
    private GuiAltManager parent;

    public GuiAccountList(GuiAltManager parent){
        super(Minecraft.getMinecraft(), parent.width, parent.height, 36, parent.height - 56, 40);
        this.parent = parent;
        
    }

    @Override
    public int getSize(){
        return Client.INSTANCE.getAccountManager().getAccounts().size();
    }

    @Override
    public void elementClicked(int i, boolean b, int i1, int i2){
        selected = i;

        if(b){
            parent.login(getAccount(i));
        }
    }

    @Override
    protected boolean isSelected(int i){
        return i == selected;
    }

    @Override
    protected void drawBackground(){
    	
    }

    @Override
    protected void drawSlot(int i, int i1, int i2, int i3, int i4, int i5) {
    	
        Account account = getAccount(i);
        Minecraft minecraft = Minecraft.getMinecraft();
        ScaledResolution scaledResolution = new ScaledResolution(minecraft);
        VanillaFontRenderer fontRenderer = minecraft.fontRendererObj;

        int x = i1 + 2;
        int y = i2;

        if (y >= scaledResolution.getScaledHeight() || y < 0)
            return;

        glTranslated(x, y, 0);

        RenderUtil.drawImage(account.getHead(), 0, 6, 24, 24);
        RenderUtil.drawRect(0, 6, 24, 24, Color.TRANSLUCENT);
        final FontRenderer fr = Client.INSTANCE.getFontManager().getFont("Display 20", true);
        fr.drawStringWithShadow(account.getName(), 30, 6, 0xFFFFFFFF);
        fr.drawStringWithShadow(ChatFormatting.GRAY + account.getEmail(), 30, 6 + fontRenderer.FONT_HEIGHT + 2, 0xFFFFFFFF);

        glTranslated(-x, -y, 0);
        
    }

    public Account getAccount(int i){
        return  Client.INSTANCE.getAccountManager().getAccounts().get(i);
    }

    public void removeSelected(){
        if(selected == -1)
            return;

        Client.INSTANCE.getAccountManager().getAccounts().remove(getAccount(selected));
        Client.INSTANCE.getAccountManager().save();
    }

    public Account getSelectedAccount(){
        return getAccount(selected);
    }
}
