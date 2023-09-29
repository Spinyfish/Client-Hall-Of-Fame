package wtf.automn.gui.clickgui.neverlose.parts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.Render;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import wtf.automn.Automn;
import wtf.automn.fontrenderer.ClientFont;
import wtf.automn.fontrenderer.GlyphPageFontRenderer;
import wtf.automn.gui.Position;
import wtf.automn.gui.Renderable;
import wtf.automn.gui.clickgui.neverlose.NeverlooseScreen;
import wtf.automn.module.Module;
import wtf.automn.utils.render.RenderUtils;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class NeverloseModuleList extends Gui implements Renderable {

    public Position pos;
    private NeverlooseScreen screen;

    public NeverloseModuleList(int width, int height, NeverlooseScreen screen) {
        this.pos = new Position(0,0,width,height);
        this.screen = screen;
    }

    private Minecraft mc = Minecraft.getMinecraft();

    private boolean toggleNext = true;

    private List<NeverlooseModule> modules = new ArrayList<>();

    public void initModules(){
        this.modules.clear();
        int count = 0;

        int leftAdd = 10;
        int rightAdd = 10;

        for(Module module : Automn.instance().moduleManager().getModules()){
            if(module.category() == this.screen.selected) {
                float posWidth = pos.width/2-30;
                float posHeight = 30; //TODO use guichanger sizes + settings shown (extra class)

                float posX = pos.x+((count%2 == 0) ? 10 : posWidth+20);
                float posY = pos.y+((count%2 == 0) ? leftAdd : rightAdd);


                Position pos = new Position(posX, posY, posWidth, 30);
                NeverlooseModule nvlM = new NeverlooseModule(pos.x, pos.y, pos.width, pos.height, module);
                pos.height = nvlM.height;
                if(count%2 == 0){
                    leftAdd+=pos.height+10;
                }else{
                    rightAdd+=pos.height+10;
                }
                modules.add(nvlM);
                count++;
            }
        }
    }

    @Override
    public void render(float x, float y, int mouseX, int mouseY) {
        pos.x = x;
        pos.y = y;
        drawRect(pos.x, pos.y, pos.x+ pos.width, pos.y+ pos.height, NeverlooseScreen.MODULES_BACKGROUND);
        GL11.glPushMatrix();
        RenderUtils.scissor((int) pos.x, (int) pos.y, (int) (pos.width), (int) (pos.height));

        for(NeverlooseModule mod : modules){
            mod.pos.y += NeverlooseScreen.offset;
            mod.render(mod.pos.x,  (mod.pos.y), mouseX, mouseY);
            if(mod.pos.isHovered(mouseX, mouseY) && toggleNext && Mouse.isButtonDown(1)) {
                mod.module.toggle();
                toggleNext = false;
            }
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
        if(!Mouse.isButtonDown(1)) toggleNext = true;
    }
}
