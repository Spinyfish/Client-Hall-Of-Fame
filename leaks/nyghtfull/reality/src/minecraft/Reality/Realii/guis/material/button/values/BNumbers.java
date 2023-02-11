package Reality.Realii.guis.material.button.values;

import org.lwjgl.input.Mouse;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.event.value.Value;
import Reality.Realii.guis.font.FontLoaders;
import Reality.Realii.guis.material.Main;
import Reality.Realii.guis.material.Tab;
import Reality.Realii.guis.material.button.Button;
import Reality.Realii.utils.render.ColorUtils;
import Reality.Realii.utils.render.RenderUtil;

import java.awt.*;

public class BNumbers extends Button {

    public BNumbers(float x, float y, Value v, Tab moduleTab) {
        super(x, y, v, moduleTab);
    }

    @Override
    public void drawButton(float mouseX, float mouseY) {
        FontLoaders.arial16.drawString(v.getName() + ":" + v.getValue(), x, y - 4, new Color(255, 255, 255).getRGB());

        FontLoaders.arial16B.drawString("", x, y + 3, new Color(255, 255, 255).getRGB());
        FontLoaders.arial16B.drawString("", x + 65, y + 3, new Color(255, 255, 255).getRGB());

        RenderUtil.drawRect(x + 5, y + 5, x + 65, y + 6, ColorUtils.reAlpha(Main.clientColor.getRGB(), 0.6f));
        animation = animationUtils.animate(60 * (((Number) v.getValue()).floatValue() / ((Numbers) v).getMaximum().floatValue()), animation, 0.05f);
        RenderUtil.drawRect(x + 5, y + 5, x + 5 + animation, y + 6, Main.clientColor.getRGB());
        RenderUtil.drawCircle(x + 5 + animation, y + 5.5f, 3, Main.clientColor.getRGB());

        if (Main.isHovered(x + 5, y + 4, x + 65, y + 7, mouseX, mouseY) && Mouse.isButtonDown(0)) {
            drag = true;
        } else if (!Mouse.isButtonDown(0)) {
            drag = false;
        }

        if (drag ) {
            double reach = mouseX - (x + 5);
            double percent = reach / 60f;
            double val = (((Numbers<?>) v).getMaximum().doubleValue() - ((Numbers<?>) v).getMinimum().doubleValue()) * percent;
            if (val > ((Numbers<?>) v).getMinimum().doubleValue() && val < ((Numbers<?>) v).getMaximum().doubleValue()) {
                v.setValue((int)(val*10)/10F);
            }

            if(val < ((Numbers<?>) v).getMinimum().doubleValue()){
                v.setValue(((Numbers<?>) v).getMinimum());
            }else if(val > ((Numbers<?>) v).getMaximum().doubleValue()){
                v.setValue(((Numbers<?>) v).getMaximum());
            }
        }

    }

    @Override
    public void mouseClicked(float mouseX, float mouseY) {
        super.mouseClicked(mouseX, mouseY);
    }
}
