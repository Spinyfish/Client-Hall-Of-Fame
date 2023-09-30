package club.pulsive.client.ui.clickgui.clickgui.panel.implementations;


import club.pulsive.api.main.Pulsive;
import club.pulsive.client.ui.clickgui.clickgui.panel.Panel;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.util.client.TimerUtil;
import club.pulsive.impl.util.render.RenderUtil;
import club.pulsive.impl.util.render.animations.Animation;
import club.pulsive.impl.util.render.animations.Direction;
import club.pulsive.impl.util.render.animations.impl.EaseInOutRect;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModulePanel extends Panel {

    private Animation animation = new EaseInOutRect(600, 1, Direction.BACKWARDS);
    private final TimerUtil timer = new TimerUtil();
    private Module module;

    public ModulePanel(Module module, float x, float y, float width, float height) {
        super(x, y, width, height, false);
        this.module = module;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public boolean isExtended() {
        return extended;
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(this.getModule().isToggled()) {
            animation.setDirection(Direction.FORWARDS);
        } else {
            animation.setDirection(Direction.BACKWARDS);
        }
        origHeight = RenderUtil.animate(totalHeight(), origHeight, 0.3f);
        if (origHeight < 0) origHeight = 0;
        theme.drawModule(this, x, y, width, height);
        super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(mouseX, mouseY)) {
            if (mouseButton == 0) {
                //animation.setDuration(300);
                module.toggle();
                timer.reset();
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
    }

}
