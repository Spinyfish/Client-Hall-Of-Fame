package Reality.Realii.guis.clickgui;

import org.lwjgl.input.Mouse;
import Reality.Realii.event.value.Mode;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.event.value.Option;
import Reality.Realii.event.value.Value;
import Reality.Realii.guis.clickgui.values.ModeRender;
import Reality.Realii.guis.clickgui.values.NumberRender;
import Reality.Realii.guis.clickgui.values.OptionRender;
import Reality.Realii.guis.clickgui.values.ValueRender;
import Reality.Realii.guis.font.FontLoaders;
import Reality.Realii.mods.Module;
import Reality.Realii.utils.cheats.world.TimerUtil;
import Reality.Realii.utils.render.RenderUtil;

import java.awt.*;
import java.util.ArrayList;

import static Reality.Realii.guis.clickgui.MainWindow.mainColor;

public class ModuleWindow {
    public float x, y, height, height2;
    public Color color;
    public Module mod;
    public TimerUtil timerUtil = new TimerUtil();
    public ArrayList<ValueRender> vrs = new ArrayList<>();
    public float circleR = 0;
    public float circleX, circleY;

    public ModuleWindow(Module mod, float x, float y, Color c) {
        this.x = x;
        this.y = y;
        this.color = c;
        this.mod = mod;

        for (Value v : mod.values) {
            if (v instanceof Option) {
                vrs.add(new OptionRender(v, x, y));
            } else if (v instanceof Numbers) {
                vrs.add(new NumberRender(v, x, y));

            } else if (v instanceof Mode) {
                vrs.add(new ModeRender(v, x, y));
            }

            if (!(v instanceof Mode)) {
                this.height2 += 20;
            } else {
                this.height2 += ((Mode<?>) v).getModes().length * 20;
            }
        }
        circleR = 100;
    }

    public void drawModule(int mouseX, int mouseY) {
        for (ValueRender v : vrs) {
            v.x = x;
            v.y = y;
        }
        if (height2 - height > 0.05) {
            height += (height2 - height) / 60f;
        }else {
            height = height2;
        }

        RenderUtil.drawRect(x, y + (mod.values.size() == 0 ? 25 : 20), x + 100, y + height + (mod.values.size() == 0 ? 25 : 25),  new Color(249, 250, 255).getRGB());

//        if (mod.isEnabled()) {
//            RenderUtil.drawRect(x, y, x + 100, y + (mod.values.size() == 0 ? 25 : 20), new Color(color.getRed(), color.getGreen(), color.getBlue()));
////            RenderUtil.drawCustomImage(x + 74, y + (mod.values.size() == 0 ? 6 : 5), 20, 10, new ResourceLocation("client/ui/clickgui/toggle_24x10.png"),-1);
//        } else {
//            RenderUtil.drawRect(x, y, x + 100, y + (mod.values.size() == 0 ? 25 : 20), new Color(255, 255, 255));
////            RenderUtil.drawCustomImage(x + 74, y + (mod.values.size() == 0 ? 6 : 5), 20, 10, new ResourceLocation("client/ui/clickgui/toggle2_24x10.png"));
//        }

        RenderUtil.drawRoundedRect(x, y, x + 100f, y + (mod.values.size() == 0 ? 25 : 20), 2, new Color(155, 155, 155).getRGB());
        if (mod.isEnabled()) {
            RenderUtil.drawRoundedRect(x, y, x + 100f, y + (mod.values.size() == 0 ? 25 : 20), 2, mainColor);
        }

        RenderUtil.drawGradientRect(x, y + (mod.values.size() == 0 ? 25 : 20), x + 100, y + (mod.values.size() == 0 ? 27 : 22), new Color(200, 200, 200, 100).getRGB(), new Color(249, 250, 255).getRGB());

        FontLoaders.arial18.drawString(mod.getName(), x + 10, y + (mod.values.size() == 0 ? 10 : 8), new Color(255, 255, 255).getRGB());

        if (isHovered(x, y, x + 100, y + (mod.values.size() == 0 ? 25 : 20), mouseX, mouseY) && Mouse.isButtonDown(0) && timerUtil.delay(200)) {
            mod.setEnabled(!mod.isEnabled());
            circleX = mouseX;
            circleY = mouseY;
            if (circleR == 0) {
                circleR = 1;
            }
            timerUtil.reset();
        }

        float valueY = y + (mod.values.size() == 0 ? 35 : 30);

        for (ValueRender v : vrs) {
            if (v.value instanceof Mode) {
                ModeRender render = (ModeRender) v;
                render.onRender(x, valueY);
                render.onMouseMove(mouseX, mouseY, Mouse.getButtonCount());
                for (String m : ((Mode<?>) render.value).getModes()) {
                    valueY += 20;
                }
            }
        }

        for (ValueRender v : vrs) {
            if (v.value instanceof Option) {
                OptionRender render = (OptionRender) v;
                render.onRender(x, valueY);
                render.onMouseMove(mouseX, mouseY, Mouse.getButtonCount());

                valueY += 20;
            }
        }

        for (ValueRender v : vrs) {
            if (v.value instanceof Numbers) {

                NumberRender render = (NumberRender) v;
                render.onRender(x, valueY);
                render.onMouseMove(mouseX, mouseY, Mouse.getButtonCount());


                valueY += 20;
            }
        }

    }

    public static boolean isHovered(float x, float y, float x1, float y1, float mouseX, float mouseY) {
        if (mouseX > x && mouseY > y && mouseX < x1 && mouseY < y1) {
            return true;
        }
        return false;
    }
}
