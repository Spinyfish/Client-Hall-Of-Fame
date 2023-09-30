package wtf.automn.gui.clickgui.neverlose;

import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;
import wtf.automn.events.EventHandler;
import wtf.automn.events.EventManager;
import wtf.automn.events.impl.visual.EventRender2D;
import wtf.automn.fontrenderer.ClientFont;
import wtf.automn.fontrenderer.GlyphPageFontRenderer;
import wtf.automn.gui.clickgui.neverlose.parts.NeverloseCategoryList;
import wtf.automn.gui.clickgui.neverlose.parts.NeverloseModuleList;
import wtf.automn.module.Category;
import wtf.automn.module.impl.visual.ModuleBlur;
import wtf.automn.utils.render.RenderUtils;

import java.awt.*;

public class NeverlooseScreen extends GuiScreen {

    public static GlyphPageFontRenderer moduleFont = ClientFont.font(20, "Arial", true);
    private final GlyphPageFontRenderer waterMarkFont = ClientFont.font(30, "Arial", true);
    public static final int LIST_BACKGROUND = Color.decode("#182430").getRGB();
    public static final int LIST_CATEGORY_COLOR = Color.decode("#304350").getRGB();
    public static final int LIST_TEXTURE_COLOR = Color.decode("#03A8F5").getRGB();
    public static final int LIST_SELECTED_COLOR = Color.decode("#0C3B54").getRGB();

    public static final int MODULES_BACKGROUND = Color.decode("#08080D").getRGB();
    public static final int MODULE_SETTINGS_BACKGROUND = Color.decode("#000B16").getRGB();
    public static double offset = 0;
    public Category selected = null;

    public NeverlooseScreen() {
    }

    @Override
    public void initGui() {
        EventManager.register(this);
        super.initGui();
        this.categoryList = new NeverloseCategoryList(150, this.height - 50, this);
        this.moduleList = new NeverloseModuleList(0, this.height - 50, this);
    }

    @Override
    public void onGuiClosed() {
        EventManager.unregister(this);
        super.onGuiClosed();
    }

    public NeverloseCategoryList categoryList;
    public NeverloseModuleList moduleList;

    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.categoryList.pos.height = this.height - 170;
        this.categoryList.render(70, 25, mouseX, mouseY);

        this.moduleList.pos.height = this.height - 170;
        this.moduleList.pos.width = this.width - 580;
        this.moduleList.render(178, 25, mouseX, mouseY);


        if(RenderUtils.isInside(mouseX,mouseY,this.moduleList.pos.x,this.moduleList.pos.y,this.moduleList.pos.width,this.moduleList.pos.height)){
            double mouseWheel = Mouse.getDWheel() / 5d;
            if(Mouse.getEventDWheel() != 0){
                offset = mouseWheel;
            }
        }
    }

    @EventHandler
    public void onRender(final EventRender2D e) {
        if (e.phase() == EventRender2D.Phase.POST) {
            ModuleBlur.drawBlurred(() -> {
                drawRect(0, 0, this.mc.displayWidth, this.mc.displayHeight + 1, Integer.MIN_VALUE);
            }, false);
            //drawRect(0, 0, this.width, this.height, Integer.MIN_VALUE);
            drawRect(this.width - 80, this.height - 145, this.width - 220, 100, MODULES_BACKGROUND);
            moduleFont.drawString("Esp Preview", this.width - 180, 110, -1);
        }
    }

}
