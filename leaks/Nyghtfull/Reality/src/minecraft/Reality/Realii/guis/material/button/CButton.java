package Reality.Realii.guis.material.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import Reality.Realii.guis.font.FontLoaders;
import Reality.Realii.utils.math.AnimationUtils;
import Reality.Realii.utils.render.RenderUtil;

import java.awt.*;

public class CButton extends Gui {
    public boolean realized;
    private float circleR = 0;
    public String name;
    public String image;
    private float x, y;
    private boolean n;

    private float xp, yp, width, height;
    private AnimationUtils animationUtils = new AnimationUtils();
    public CButton(String name, String res, float xp, float yp, float w, float h) {
        this.name = name;
        this.image = res;
        this.xp = xp;
        this.yp = yp;
        this.width = w;
        this.height = h;
    }


    public void onMouseClicked(float mouseX, float mouseY) {
        if (isHovered(x + 1, y + 1, x + 17, y + 17, mouseX, mouseY) && Mouse.isButtonDown(0)) {
            realized = !realized;
            n = !n;
        }
    }

    public void onRender(float x, float y, float mouseX, float mouseY) {
        this.x = x;
        this.y = y;
        onUpdate(mouseX, mouseY);
        if (isHovered(x + 1, y + 1, x + 17, y + 17, mouseX, mouseY)) {
            RenderUtil.drawCircle(x + 8, y + 8, 10, new Color(30, 30, 30, 30).getRGB());
        }
        if (n) {
            RenderUtil.drawCircle(x + 8, y + 8, circleR, new Color(245, 245, 245, (int) (100 * circleR / 10)).getRGB());
        } else {
            RenderUtil.drawCircle(x + 8, y + 8, 10, new Color(245, 245, 245, (int) (100 * circleR / 10)).getRGB());
        }

        RenderUtil.drawCustomImage(x + xp, y + yp, width, height, new

                ResourceLocation(image));

    }

    public void onUpdate(float mouseX, float mouseY) {
        if (circleR == 10)
            n = false;
        if (n) {
            circleR = animationUtils.animate(10, circleR, 0.1F);
        } else {
            circleR = animationUtils.animate(0, circleR, 0.1F);
        }
    }

    public static boolean isHovered(float x, float y, float x2, float y2, float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }
}
