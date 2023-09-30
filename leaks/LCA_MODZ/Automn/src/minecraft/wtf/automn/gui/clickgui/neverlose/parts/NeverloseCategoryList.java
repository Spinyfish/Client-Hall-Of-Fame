package wtf.automn.gui.clickgui.neverlose.parts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;
import wtf.automn.fontrenderer.ClientFont;
import wtf.automn.fontrenderer.GlyphPageFontRenderer;
import wtf.automn.gui.Position;
import wtf.automn.gui.Renderable;
import wtf.automn.gui.clickgui.neverlose.NeverlooseScreen;
import wtf.automn.module.Category;
import wtf.automn.utils.render.RenderUtils;

import java.awt.*;

public class NeverloseCategoryList extends Gui implements Renderable {

    public Position pos;
    private  NeverlooseScreen screen;

    public NeverloseCategoryList(int width, int height, NeverlooseScreen screen) {
        this.pos = new Position(0,0,width,height);
        this.screen = screen;
    }

    private Minecraft mc = Minecraft.getMinecraft();

    private GlyphPageFontRenderer tabFont = ClientFont.font(20, "Arial", true);
    private GlyphPageFontRenderer categoryFont = ClientFont.font(28, "Arial", true);
    private GlyphPageFontRenderer watermarkFont = ClientFont.font(45, "Arial", true);

    private boolean next = true;

    @Override
    public void render(float x, float y, int mouseX, int mouseY) {
        pos.x = x;
        pos.y = y;
        RenderUtils.drawRoundedRect(pos.x, pos.y, pos.width-30, pos.height, 3, NeverlooseScreen.LIST_BACKGROUND);

        float listX = pos.x+10;
        float listY = pos.y+50;

        float listWidth = pos.width-20;
        float listHeight = pos.height-100;
        watermarkFont.drawStringWithShadow("Automn", listX + 2, listY - 30, new Color(0, 141, 255).getRGB());
        tabFont.drawString("Categories", listX+2, listY, NeverlooseScreen.LIST_CATEGORY_COLOR, false);
        int ind = 0;
        for(Category category : Category.values()) {
            int catHeight = 30;
            Position hov = new Position(listX+25,listY+15+(ind*catHeight+10), listWidth-20, catHeight-10);

            if(hov.isHovered(mouseX, mouseY) && Mouse.isButtonDown(0) && next) {
                this.screen.selected = category;
                this.screen.moduleList.initModules();
                next = false;
            }

            if(this.screen.selected == category) {
                drawRect(listX, hov.y, listX+listWidth - 40, hov.y+hov.height, NeverlooseScreen.LIST_SELECTED_COLOR);
            }
            categoryFont.drawString(category.toString().substring(0, 1)+category.toString().substring(1).toLowerCase(), listX+10, listY+15+(ind*catHeight+10)+2, Color.white.getRGB());
            ind++;
        }
        if(!Mouse.isButtonDown(0)){
            next = true;
        }
    }

}
