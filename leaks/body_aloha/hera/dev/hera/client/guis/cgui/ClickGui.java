package dev.hera.client.guis.cgui;

import dev.hera.client.mods.Category;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGui extends GuiScreen {

    public List<Frame> frames = new ArrayList<>();
    public Frame dragging = null;
    public int dragX, dragY;

    public ClickGui(){
        int x = 10;
        for(Category cat : Category.values()){
            frames.add(new Frame(cat, x, 10));
            x += 121;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for(Frame frame : frames){
            frame.render();
        }
        if(dragging == null)
            return;
        dragging.x = mouseX - dragX;
        dragging.y = mouseY - dragY;

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for(Frame frame : frames){
            if(mouseX < frame.x || mouseX > frame.x + 120)
                continue;
            if(mouseY <= frame.y)
                continue;
            if(mouseY <= frame.y + 20){
                if(mouseButton == 0){
                    dragging = frame;
                    dragX = mouseX - frame.x;
                    dragY = mouseY - frame.y;

                }else if(mouseButton == 1){
                    frame.opened = !frame.opened;
                }
            }else{
                frame.onClick(mouseX, mouseY, mouseButton);
            }

        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        dragging = null;
    }


}