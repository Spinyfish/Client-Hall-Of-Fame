package de.fanta.clickgui.defaultgui.components;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import de.fanta.Client;
import de.fanta.setting.Setting;
import de.fanta.utils.Colors;
import de.fanta.utils.RenderUtil;
import de.fanta.utils.TimeUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

public class TextField {

    public float x;
    public float y;
    private CategoryPanel panel;
    private float dragX;
    private Setting setting;
    private boolean focused;
    private char key = 0;
    private List<String> list = new ArrayList<>();
    TimeUtil time = new TimeUtil();



    public TextField(Setting setting, CategoryPanel panel, float x, float y) {
        this.setting = setting;
        this.panel = panel;
        this.x = x;
        this.y = y;
    }

    public void drawTextField(float mouseX, float mouseY){
        float xOff = panel.cateButton.panel.dragX;
        float yOff = panel.cateButton.panel.dragY;

        boolean hovering = mouseX >= x + xOff && mouseY >= y + yOff && mouseX <= x + xOff + 9 && mouseY <= y + yOff + 9;

        int color = hovering ? Colors.getColor(150, 150, 150, 255) : Colors.getColor(110, 110, 110, 255);
        int fcolor = hovering ? Colors.getColor(150, 150, 150, 255) : Colors.getColor(20, 30, 20, 255);


        Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(setting.getName(), (int) (x + xOff + 12), (int) (y + yOff - 2), -1);


        if(hovering && Mouse.isButtonDown(1)){
            focused = true;
        }else{
            if(!hovering && Mouse.isButtonDown(1)){
                focused = false;
            }
        }

        RenderUtil.rectangle(x + xOff, y + yOff, x + xOff + 9, y + yOff + 9, focused ? fcolor : color);
        if(focused) {
            //keyListener();
        	 Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow("", (int) (x + xOff + 12), (int) (y + yOff + 9), -1);
        }

    }

    public void textFieldClicked(float mouseX, float mouseY, int mouseButton) {
        float xOff = panel.cateButton.panel.dragX;
        float yOff = panel.cateButton.panel.dragY;

        boolean hovering = mouseX >= x + xOff && mouseY >= y + yOff && mouseX <= x + xOff + 9 && mouseY <= y + yOff + 9;

        if(hovering)
            ((de.fanta.setting.settings.TextField) setting.getSetting()).focused = !((de.fanta.setting.settings.TextField) setting.getSetting()).focused;
    }

/*
    private String keyListener() {
        String keyType = "";
        String allkeys = "";
        String s[] = new String[]{keyType};


        try {
            if (focused) {
                key = Keyboard.getEventCharacter();
                //time.hasReached(100)
                if (key != 0) {

                    for (int i = 0; i < key; i++) {
                        if(time.hasReached(100)) {
                            keyType += String.valueOf(key);
                        }
                        s[0] = keyType;
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s[0];
    }

 */


    }
