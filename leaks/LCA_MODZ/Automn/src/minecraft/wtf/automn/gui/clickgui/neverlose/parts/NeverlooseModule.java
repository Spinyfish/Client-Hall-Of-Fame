package wtf.automn.gui.clickgui.neverlose.parts;

import net.minecraft.client.gui.Gui;
import wtf.automn.fontrenderer.ClientFont;
import wtf.automn.fontrenderer.GlyphPageFontRenderer;
import wtf.automn.gui.Position;
import wtf.automn.gui.Renderable;
import wtf.automn.gui.clickgui.neverlose.NeverlooseScreen;
import wtf.automn.gui.clickgui.neverlose.parts.changers.GuiChanger;
import wtf.automn.gui.clickgui.neverlose.parts.changers.GuiChangerBoolean;
import wtf.automn.gui.clickgui.neverlose.parts.changers.GuiChangerNumber;
import wtf.automn.module.Module;
import wtf.automn.module.settings.Setting;
import wtf.automn.module.settings.SettingBoolean;
import wtf.automn.module.settings.SettingNumber;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NeverlooseModule extends Gui implements Renderable {
    
    public Position pos;

    public Module module;
    private List<GuiChanger> changers = new ArrayList<>();

    public int height = 0;

    public NeverlooseModule(float x, float y, float width, float height, Module mod) {
        this.module = mod;
        int cHeight = 30;
        for(Setting setting : mod.settings()) {
            if(setting instanceof SettingNumber){
                this.changers.add(new GuiChangerNumber(((SettingNumber) setting), x, y+cHeight, 180, 25));
                cHeight+=25;
            }
            if(setting instanceof SettingBoolean){
                this.changers.add(new GuiChangerBoolean(((SettingBoolean) setting), x, y+cHeight, 180, 25));
                cHeight+=25;
            }
        }
       this.height = cHeight;
        pos = new Position(x,y,width,cHeight);
    }

    private GlyphPageFontRenderer moduleFont = ClientFont.font(28, "Arial", true);
    @Override
    public void render(float x, float y, int mouseX, int mouseY) {
        drawRect(pos.x, pos.y, pos.x+pos.width + 5, pos.y+pos.height, NeverlooseScreen.MODULE_SETTINGS_BACKGROUND);
        moduleFont.drawString(module.display(), pos.x+2, pos.y+5, !module.enabled() ? NeverlooseScreen.LIST_SELECTED_COLOR : Color.white.getRGB());
        for(GuiChanger changer : this.changers){
            changer.pos.y += NeverlooseScreen.offset;
            changer.draw(mouseX, mouseY);
        }
    }
}
