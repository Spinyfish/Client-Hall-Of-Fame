package wtf.automn.module.impl.visual;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import wtf.automn.Automn;
import wtf.automn.events.EventHandler;
import wtf.automn.events.impl.visual.EventRender2D;
import wtf.automn.fontrenderer.ClientFont;
import wtf.automn.fontrenderer.GlyphPageFontRenderer;
import wtf.automn.module.Category;
import wtf.automn.module.Module;
import wtf.automn.module.ModuleInfo;
import wtf.automn.module.settings.SettingBoolean;
import wtf.automn.module.settings.SettingNumber;
import wtf.automn.utils.minecraft.ArraylistUtils;
import wtf.automn.utils.render.RenderUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "hud", displayName = "HUD", category = Category.VISUAL)
public class ModuleHUD extends Module {
    private final GlyphPageFontRenderer font = ClientFont.font(38, "Arial", true);
    private final GlyphPageFontRenderer arraylist = ClientFont.font(20, "Calibri", true);

    public final SettingBoolean arrayList = new SettingBoolean("arraylist", true, "Arraylist", this, "Shows the Arraylist");
    public final SettingBoolean hotBar = new SettingBoolean("hotbar", true, "Hotbar", this, "Shows the CustomHotbar");
    public final SettingBoolean hotBarBlur = new SettingBoolean("hotbarblur", true, "HotbarBlur", this, "HotbarBlur");
    public final SettingBoolean hotBarglow = new SettingBoolean("hotbarglow", false, "HotbarGlow", this, "HotbarGlow");
    public final SettingBoolean hotBarshadow= new SettingBoolean("hotbarshadow", true, "HotbarShadow", this, "HotbarShadow");
    public final SettingBoolean shadow = new SettingBoolean("shadow", true, "Shadow", this, "Shadow");
    public final SettingBoolean glow = new SettingBoolean("glow", false, "Glow", this, "Glow");
    public final SettingBoolean blur = new SettingBoolean("blur", true, "Blur", this, "Glow");
    public final SettingBoolean chatFont = new SettingBoolean("chatfont", true, "ChatFont", this,"Shows the CustomHotbar");
    public final SettingNumber xValue = new SettingNumber("x_value", 10D, 0D, 50D, "X Position", this,"The X Position of the Arraylist");
    public final SettingNumber yValue = new SettingNumber("y_value", 10D, 0D, 50D, "Y Position", this,"The Y Position of the Arraylist");

    @Override
    protected void onDisable() {

    }

    @Override
    protected void onEnable() {

    }

    @EventHandler
    public void onRender(final EventRender2D event) {
        if (event.phase() == EventRender2D.Phase.PRE) return;
        if (hotBar.getBoolean()) this.drawHotbar();
        this.drawWatermark();
        if (arrayList.getBoolean()) this.drawArraylist();
    }

    public void drawWatermark() {
        final ScaledResolution sr = new ScaledResolution(this.MC);
        if(mm.glow.enabled()) {
            ModuleBloom.drawGlow(() -> this.font.drawScaledString("A", sr.getScaledWidth() - 80, 0, 1, Color.blue.getRGB()), false);
        }
       this.font.drawScaledString("A", sr.getScaledWidth() - 80, 0, 1, Color.blue.getRGB());
        if(mm.glow.enabled()) {
            ModuleBloom.drawGlow(() ->  this.font.drawScaledString("utomn", sr.getScaledWidth() - 67, 0, 1,  Color.blue.getRGB()), false);
        }
        this.font.drawScaledString("utomn", sr.getScaledWidth() - 67, 0, 1, -1);
        if(mm.glow.enabled()) {
            ModuleBloom.drawGlow(() ->   Gui.drawRect(sr.getScaledWidth() - 80, 19, sr.getScaledWidth() - 13, 18, Color.blue.getRGB()), false);


        }
        Gui.drawRect(sr.getScaledWidth() - 80, 19, sr.getScaledWidth() - 13, 18, -1);
    }

    private void drawHotbar() {
        final ScaledResolution scaledResolution = new ScaledResolution(this.MC);
        final int i = scaledResolution.getScaledWidth() / 2;


            Gui.drawRect((scaledResolution.getScaledWidth() / 2 - 91 + MC.thePlayer.inventory.currentItem * 20),
                    (scaledResolution.getScaledHeight() - 22),
                    (scaledResolution.getScaledWidth() / 2 + 91 - 20 * (8 - MC.thePlayer.inventory.currentItem)),
                    scaledResolution.getScaledHeight(), new Color(175, 171, 169, 140).getRGB());
            if(mm.shadow.enabled() && mm.hud.hotBarshadow.getBoolean()) {
                ModuleShadow.drawShadow(() -> RenderUtils.drawRoundedRect(i - 91, scaledResolution.getScaledHeight() - 22, 182, 20, 2, Color.black.getRGB()), false);
            }
        if(mm.glow.enabled() && mm.hud.hotBarglow.getBoolean()) {
            ModuleBloom.drawGlow(() -> RenderUtils.drawRoundedRect(i - 91, scaledResolution.getScaledHeight() - 22, 182, 20, 2, Color.blue.getRGB()), false);
        }
    if(mm.hud.hotBarBlur.getBoolean() && mm.blur.enabled()) {
        ModuleBlur.drawBlurred(() -> RenderUtils.drawRoundedRect(i - 91, scaledResolution.getScaledHeight() - 22, 182, 20, 2, Integer.MIN_VALUE), false);
    }
    }


    public void drawArraylist() {
        List<String> l = new ArrayList<>();
        for (final var mod : Automn.instance().moduleManager().getModules())
            if (mod.enabled()) l.add(mod.display());
        l = ArraylistUtils.sortList(this.arraylist, l);

        for (int i = 0; i < l.size(); i++) {
            final String display = l.get(i);
            final int color = Color.white.getRGB();

            final float baseX = (float) xValue.getValue();
            final float baseY = (float) yValue.getValue();

            final float y = baseY + (i * this.arraylist.getFontHeight());
            final float width = this.arraylist.getStringWidth(display + 2);
            final float height = this.arraylist.getFontHeight();


                if(mm.shadow.enabled() && shadow.getBoolean()){
                    ModuleShadow.drawShadow(() -> Gui.drawRect(baseX, y, baseX + width, y + height, Color.black.getRGB()), false);
                }
                if(mm.glow.enabled() && glow.getBoolean()){
                    ModuleBloom.drawGlow(() -> Gui.drawRect(baseX, y, baseX + width, y + height, Color.blue.getRGB()), false);
                }

                if(mm.blur.enabled() && mm.hud.blur.getBoolean()) {
                ModuleBlur.drawBlurred(() -> Gui.drawRect(baseX, y, baseX + width, y + height, Integer.MIN_VALUE), false);
                }else{
                Gui.drawRect(baseX, y, baseX + width, y + height, Integer.MIN_VALUE);
            }

                this.arraylist.drawString(display, baseX, y, color);


        }

    }
}