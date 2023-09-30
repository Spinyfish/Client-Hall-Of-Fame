package Reality.Realii.guis.material.button.values;

import org.lwjgl.input.Mouse;
import Reality.Realii.event.value.Mode;
import Reality.Realii.event.value.Value;
import Reality.Realii.guis.font.FontLoaders;
import Reality.Realii.guis.material.Main;
import Reality.Realii.guis.material.Tab;
import Reality.Realii.guis.material.button.Button;
import Reality.Realii.mods.modules.ClientSettings;
import Reality.Realii.utils.render.RenderUtil;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class BMode extends Button {

    public BMode(float x, float y, Value v, Tab moduleTab) {
        super(x, y, v, moduleTab);
    }

    @Override
    public void draw(float mouseX, float mouseY) {
        FontLoaders.arial16.drawString(v.getName(), x + 2 - animation / (((Mode) this.v).getModes().length * 20) * 5, y - animation / (((Mode) this.v).getModes().length * 20) * 5, new Color(150, 150, 150).getRGB());
        FontLoaders.arial18.drawString(((Mode) v).getModeAsString(), x + 5, y + 5, new Color(120, 120, 120).getRGB());
        Color c = new Color(ClientSettings.r.getValue().intValue(), ClientSettings.g.getValue().intValue(), ClientSettings.b.getValue().intValue());
        RenderUtil.drawBorderedRect(x, y - 5, x + 65, y - 5 + animation, 0.5f, c.getRGB(), new Color(0, 0, 0, 0).getRGB());
        FontLoaders.arial16.drawString("", x + 55, y + 4, new Color(200, 200, 200).getRGB());

        float modY = y + 25;
        for (String e : ((Mode<?>) v).getModes()) {
            if (e.equals(v.getValue()))
                continue;
            if (modY <= y - 5 + animation) {
                FontLoaders.arial18.drawString(e, x + 5, modY, new Color(120, 120, 120).getRGB());
//                if (Main.isHovered(x, modY - 5, x + 65, modY + 25, mouseX, mouseY) && Mouse.isButtonDown(0)) {
//                    drag = false;
//                    v.setValue(e);
//                }
            }
            modY += 20;
        }

        if (drag) {
            animation = animationUtils.animate(((Mode) this.v).getModes().length * 20, animation, 0.1f);
        } else {
            animation = animationUtils.animate(20, animation, 0.1f);
        }

        super.draw(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY) {
        super.mouseClicked(mouseX, mouseY);
        if (Main.isHovered(x, y - 5, x + 65, y + 15, mouseX, mouseY)) {
            drag = !drag;
        }

        float modY = y + 25;
        for (String e : ((Mode<?>) v).getModes()) {
            if (e.equals(v.getValue()))
                continue;
            if (modY <= y - 5 + animation) {
                if (Main.isHovered(x, modY, x + 65, modY + 20, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                    drag = false;
                    v.setValue(e);
                }
            }
            modY += 20;
        }
    }


}
