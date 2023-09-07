package kevin.persional.milk.guis.clickgui;

import blur.*;
import kevin.persional.milk.guis.font.FontLoaders;
import kevin.persional.milk.utils.StencilUtil;
import kevin.persional.milk.utils.key.ClickUtils;
import kevin.persional.milk.utils.render.anims.AnimationUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import kevin.hud.ClickGui;
import kevin.main.KevinClient;
import kevin.module.ModuleCategory;
import kevin.module.Module;
import kevin.utils.ColorUtils;
import kevin.utils.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;

public class MilkClickGui extends ClickGui {
    // Author: IamFrozenMilk
    @Expose
    @SerializedName("X")
    public
    int x = 100;
    public
    int y = 30;
    int lastClickX;
    int lastClickY;
    int startX;
    int startY;
    int actX;
    @Expose
    @SerializedName("Y")
    public int actY;
    boolean dragging = false;
    ModuleCategory selectCategory = null;
    ModulePanel selectPanel = null;
    float animForCategory2 = 0,animForCategory = 0, animForModulePanel = 0, animJoin = 0, animScroll = 0, scroll = 0, animPanelScroll = 0, scroll2 = 0, animForModules = 0;
    AnimationUtils animationUtils = new AnimationUtils(), animationUtilsPanel = new AnimationUtils(), animationUtils3 = new AnimationUtils(), animationUtils4 = new AnimationUtils(), scrollAnim = new AnimationUtils(), anim5 = new AnimationUtils();
    public MilkClickGui(){
    }

    @Override
    public void handleMouseInput() throws IOException {
        if(Mouse.getEventDWheel() != 0){
            if(selectPanel != null)
                animPanelScroll += Mouse.getEventDWheel() > 0 ? 5 : -5;
            else
                animScroll += Mouse.getEventDWheel() > 0 ? 5 : -5;
        }
        super.handleMouseInput();
    }

    @Override
    public void initGui() {
        animJoin = -1;
        y = -500;
        super.initGui();
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        GaussianBlur.renderBlur(2);
        if(dragging) {
            x = lastClickX + mouseX - startX;
            y = lastClickY + mouseY - startY;
            actX = x;
            actY = y;
        }
        animScroll = (float) scrollAnim.animateHigh(0, animScroll, 0.2);
        scroll += animScroll;
        if (selectPanel != null) {
            animPanelScroll = (float) selectPanel.animScroll.animateHigh(0, animPanelScroll, 0.2);
            scroll2 += animPanelScroll;
            if (scroll2 > 0) scroll2 = 0;
        }
        if(scroll > 0) scroll = 0;
        animJoin = (float) animationUtils3.animateHigh(1.0, animJoin, 0.3);
        if(animJoin < 0.95f) // for Anim
        {
            y = (int) ((actY) * animJoin);
        }else{
            y = actY;
        }
        float nextY = y;

        int animAlpha = Math.max((int)(animJoin * 240),0);
        int animAlpha2 = Math.max((int)(animJoin * 200),0);
        RenderUtils.drawRect(x - 50, y, x + 58, y + 300, new Color(23,26,33, animAlpha2).getRGB());
        RenderUtils.drawRect(x + 50, y, x + 400, y + 300, new Color(23,26,33, animAlpha).getRGB());
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        Gui.drawRect(x + 50, y, x + 392, y + 300, new Color(23,26,33, animAlpha).getRGB());
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        Color white = new Color(230, 230, 230, animAlpha);
        Color white2 = new Color(60, 60, 60, animAlpha);
        // title
        KevinClient.fontManager.fontMisans32.drawString("Kevin", x - 34, y + 12, white.getRGB());
        FontLoaders.novo16.drawString(KevinClient.INSTANCE.getVersion(), x - 8 + 3 - 1, y + 9, white.getRGB());
//        FontUtil.sfuiFont18.drawString("b" + Rice.CLIENT_VERSION, x - 5, y + 15, white);

        if(selectPanel != null) {
            StencilUtil.initStencilToWrite();
            Gui.drawRect(x + 50, y, x + 400, y + 300, -1);
            StencilUtil.readStencilBuffer(1);
            animForModulePanel = (float) animationUtilsPanel.animateHigh(1, animForModulePanel, 0.3);
            selectPanel.drawPanel((int) (x + 5 + 400 - 400 * animForModulePanel), y, mouseX, mouseY, partialTicks, animForModulePanel, scroll2);
            StencilUtil.uninitStencilBuffer();
        }else{
            animForModulePanel = 0;
        }
        int categoryY = 30 + y;
        for(ModuleCategory category : ModuleCategory.values()) {
            float moduleY = y + 10 + scroll;
            if(category == selectCategory) {
                animForModules = (float) anim5.animateHigh(1.0, animForModules, 0.3);
                int categoryX = (int) (-400 + 400 * animForModules);
                // select anim
                if(animForCategory == 0){
                    animForCategory = categoryY + 2;
                }else{
                    animForCategory = (float) animationUtils.animate(categoryY + 2 - y, animForCategory, 0.2);
                }
                animForCategory2 = (float) animationUtils4.animate(KevinClient.fontManager.font35.getStringWidth(category.name() + 10), animForCategory2, 0.2);
//                Color rainbow = ColorPicker.getColor(0, 14);
//                RenderUtils.drawRect(x - 34, y + animForCategory, x - 40 + animForCategory2, y + animForCategory + 13, rainbow.getRGB());
                // category
                FontLoaders.novo18.drawString(category.name(), x - 33, categoryY + 5, new Color(255, 255, 255).getRGB());
                // for all module
                if(selectPanel == null) {
                    StencilUtil.initStencilToWrite();
                    Gui.drawRect(x + 50, y, x + 400, y + 300, -1);
                    StencilUtil.readStencilBuffer(1);
                    for (Module m : KevinClient.moduleManager.getModules()) {
                        if (selectCategory == null || m.getCategory() != category) continue;
                        int cc = ClickUtils.isClickable(x + 60 + categoryX, moduleY, x + 370 + categoryX, moduleY + 35, mouseX, mouseY) ? new Color(16,19,24,animAlpha).getRGB() : new Color(18,21,28,animAlpha).getRGB();
                        RenderUtils.drawRect(x + 60 + categoryX, moduleY, x + 370 + categoryX, moduleY + 35,cc);
//                        if(m.getState()){
//                            m.anim = m.animationUtils.animateHigh(1.0, m.anim, 0.1);
//                        }else{
//                            m.anim = m.animationUtils.animateHigh(0, m.anim, 0.1);
//                        }
//                        Color nameColor = ColorUtils.blend(rainbow, new Color(224, 224, 224), m.anim);
                        FontLoaders.novo22.drawString(m.getName(), x + 66 + categoryX, moduleY + 8, ColorUtils.reAlpha((m.getState() ? Color.GREEN.brighter() : Color.RED.brighter()), animAlpha).getRGB());
                        FontLoaders.novo18.drawString(m.getDescription(), x + 66 + categoryX, moduleY + 23, new Color(95,93,101, animAlpha).getRGB());
                        moduleY += 45;
                    }
                    StencilUtil.uninitStencilBuffer();
                }
            }else {
                FontLoaders.novo18.drawString(category.name(), x - 33, categoryY + 5, -1);
            }
            categoryY += 20;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mouseButton == 0 && ClickUtils.isClickable(x, y, x + 400, y + 12, mouseX, mouseY)){
            startX = mouseX;
            startY = mouseY;
            lastClickX = this.x;
            lastClickY = this.y;
            dragging = true;
        }
        if(selectPanel != null) {
            if(ClickUtils.isClickable(x + 52, y + 3, x + 60, y + 7, mouseX, mouseY)){
                selectPanel = null;
                animForModules = 0;
                scroll = 0;
                return;
            }
            selectPanel.clickPanel(x + 5, y, mouseX, mouseY, scroll2);
        }
        int categoryY = 30 + y;
        for(ModuleCategory category : ModuleCategory.values()) {
            float moduleY = y + 10 + scroll;
            if(category == selectCategory) {
                int categoryX = (int) (-400 + 400 * animForModules);
                // for all module
                if(selectPanel == null) {
                    for (Module m : KevinClient.moduleManager.getModules()) {
                        if (selectCategory == null || m.getCategory() != category) continue;
                        if(mouseY >= y && mouseY <= y + 300 && ClickUtils.isClickable(x + 60 + categoryX, moduleY, x + 370 + categoryX, moduleY + 35, mouseX, mouseY)){
                            if(mouseButton == 1) {
                                scroll2 = 0;
                                selectPanel = new ModulePanel(m);
                                animForModulePanel = 0;
                                scroll = 0;
                            }else{
                                m.toggle();
                            }
                        }
                        moduleY += 45;
                    }
                }
            }else {
                if(ClickUtils.isClickable(x - 43, categoryY, x + 55, categoryY + 20, mouseX, mouseY)){
                    selectCategory = category;
                    selectPanel = null;
                    animForModules = 0;
                    scroll = 0;
                }
            }
            categoryY += 20;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(selectPanel != null) selectPanel.keyType(keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void onGuiClosed() {
//        KevinClient.fileManager.saveAllConfigs();
        super.onGuiClosed();
    }
}
