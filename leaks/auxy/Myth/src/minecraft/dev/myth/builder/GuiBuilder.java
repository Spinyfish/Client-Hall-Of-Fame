package dev.myth.builder;

import dev.myth.api.utils.mouse.MouseUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.builder.items.Item;
import dev.myth.builder.items.ItemList;
import dev.myth.builder.items.impl.ItemRect;
import dev.myth.main.ClientMain;
import dev.myth.managers.ShaderManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class GuiBuilder extends GuiScreen  {


    /** Current Tab*/
    private BuilderTab tab = BuilderTab.ESP;

    /** Gui Values */
    private final double width = 600, height = 400;
    private int x = 300, y = 200;
    private boolean closing, addEx;


    /** Items */
    private ArrayList<Item> items = new ArrayList<>();
    private Item selectedItem;
    private boolean itemDrag;

    @Override
    public void initGui() {
        super.initGui();
        closing = false;
        selectedItem = null;



    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {


        ClientMain.INSTANCE.manager.getManager(ShaderManager.class).ROUNDED_RECT_SHADER.drawRound(x, y, (float) width, (float) height, 8, new Color(20, 20, 20));

        /** Loop all tabs */
        AtomicInteger icoX = new AtomicInteger(0);
        for (BuilderTab bTab : BuilderTab.values()) {

            /** Dont need the mouse event here since im gay */
            if (MouseUtil.isHovered(mouseX, mouseY, x + 15 + icoX.get(), y + 20, 20, 20) && Mouse.isButtonDown(0)) {
                tab = bTab;
            }

            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).ROUNDED_RECT_SHADER.drawRound(x + 15 + icoX.get(), y + 20, 20, 20, 5, bTab == tab ? new Color(35, 35, 35) : new Color(30, 30, 30));
            RenderUtil.drawImage(x + 18 + icoX.get(), y + 24, 12, 12, new ResourceLocation(String.format("myth/builder/%s.png", bTab.toString().toUpperCase())));
            icoX.set(icoX.get() + 30);


        }

        AtomicInteger itemY = new AtomicInteger(0);
        for (ItemList list : ItemList.values()) {
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).ROUNDED_RECT_SHADER.drawRound((float) (x + width - 30), y + 20 + itemY.get(), 20, 20, 5, new Color(30, 30, 30));
            RenderUtil.drawImage((float) (x + width - 26), y + 24 + itemY.get(), 12, 12, new ResourceLocation(String.format("myth/builder/%s.png", list.toString().toLowerCase())));

            if (MouseUtil.isHovered(mouseX, mouseY, (float) (x + width - 30), y + 20 + itemY.get(), 20, 20) && Mouse.isButtonDown(0)) {
                final String name = list.toString().toLowerCase();

                /** add things */
                switch (name) {
                    case "rect":
                        items.add(new ItemRect((int) (x + width / 2), (int) (y + height / 2), 50, 50, tab));
                        break;
                }
            }
            itemY.set(itemY.get() + 25);
        }

        /** Loop all added items and do cool things */
        for (Item item : items) {
            if (item.type == tab) {


                /** Actions if a item has been selected */
                if (selectedItem != null ) {

                    /** Outline */
                    RenderUtil.drawRect(selectedItem.x, selectedItem.y, 1, selectedItem.height, -1);
                    RenderUtil.drawRect(selectedItem.x, selectedItem.y, selectedItem.height, 1, -1);
                    RenderUtil.drawRect(selectedItem.x + selectedItem.width - 1, selectedItem.y, 1, selectedItem.height, -1);
                    RenderUtil.drawRect(selectedItem.x, selectedItem.y + selectedItem.height - 1, selectedItem.height, 1, -1);

                    /** Remove button at the bottom */
                    //ClientMain.INSTANCE.manager.getManager(ShaderManager.class).ROUNDED_RECT_SHADER.drawRound((float) (x + 10), (float) (y + height - 30), 20, 20, 5, MouseUtil.isHovered(mouseX, mouseY, (float) (x + 10), (float) (y + height - 30), 20, 20) ? new Color(37, 37, 37) : new Color(30, 30, 30));
                    //RenderUtil.drawImage((float) (x + 14), (float) (y + height - 26), 12, 12, new ResourceLocation("myth/builder/remove.png"));
                    //if (MouseUtil.isHovered(mouseX, mouseY, (float) (x + 10), (float) (y + height - 30), 20, 20) && Mouse.isButtonDown(0)) {
                        //items.remove(selectedItem);
                    //}



                }

                if (MouseUtil.isHovered(mouseX, mouseY, item.x, item.y, item.width, item.height)) {

                    if (Mouse.isButtonDown(1))
                        selectedItem = item;

                    /** Dragging */
                    if (Mouse.isButtonDown(0)) {
                        selectedItem = null;
                        RenderUtil.drawRect(item.x, item.y, 1, item.height, -1);
                        RenderUtil.drawRect(item.x, item.y, item.height, 1, -1);
                        RenderUtil.drawRect(item.x + item.width - 1, item.y, 1, item.height, -1);
                        RenderUtil.drawRect(item.x, item.y + item.height - 1, item.height, 1, -1);

                        if (item.dragX == 0 && item.dragY == 0) {
                            item.dragX = mouseX - item.x;
                            item.dragY = mouseY - item.y;

                        } else {
                            item.x = (int) (mouseX - item.dragX);
                            item.y = (int) (mouseY - item.dragY);
                        }
                    }

                }else {
                    if (Mouse.isButtonDown(1))
                        selectedItem = null;
                }

                item.draw();
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        if (MouseUtil.isHovered(mouseX, mouseY, (float) (x + width - 30), y + 20, 20, 20) && mouseButton == 0) {
            addEx = !addEx;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == 1 && !closing) {
            closing = true;
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

}


