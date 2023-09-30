package dev.echo.module.impl.render;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.render.Render2DEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.ColorSetting;
import dev.echo.module.settings.impl.ModeSetting;
import dev.echo.utils.render.ColorUtil;
import dev.echo.utils.time.TimerUtil;
import dev.echo.utils.tuples.Pair;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class Snake extends Module {
    private double x = 100;
    private double y = 100;

    private boolean left,right,up,down;

    private final TimerUtil timer = new TimerUtil();

    private double apple;

    public Snake() {
        super("Snake", Category.RENDER, "A Remake of the popular game snake in minecraft");
    }

    @Link
    public Listener<Render2DEvent> onRender2DEvent = event -> {
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            left = true;
            right = false;
            up = false;
            down = false;
            //x -= 1;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            left = false;
            right = true;
            up = false;
            down = false;
           // x += 1;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            left = false;
            right = false;
            up = true;
            down = false;
           // y -= 1;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            left = false;
            right = false;
            up = false;
            down = true;
            //y += 1;
        }
        if(timer.hasTimeElapsed(100)) {
            if(down) {
                y += 3;
            } else if(up) {
                y -= 3;
            } else if(right) {
                x += 3;
            } else if(left) {
                x -= 3;
            }
            timer.reset();
        }
        //if(y >= 400) {
          //  y = 0;
       // }
        //apple = MathHelper
        Gui.drawRect2(270, 50, 400, 400,Color.DARK_GRAY.getRGB());
        Gui.drawRect2(x, y, 20, 20,new Color(0,100,1).getRGB());
        Gui.drawRect2(270, 50, 20, 20,new Color(255,0,1).getRGB());
    };

    public void handleInput(int keyCode) {
        if (keyCode == 37) {
            x -= 20;
        } else if (keyCode == 39) {
            x += 20;
        } else if (keyCode == 38) {
            y -= 20;
        } else if (keyCode == 40) {
            y += 20;
        }
    }


}
