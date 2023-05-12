package markgg.ui.comp;

import markgg.modules.Module;
import markgg.settings.Setting;
import markgg.ui.GUIMethod;
import markgg.ui.GUIMethodLight;

public class Comp {

	public double x, y, x2, y2;
    public GUIMethod parent; public GUIMethodLight parent1;
    public Module module;
    public Setting setting;

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    public void mouseReleased(int mouseX, int mouseY, int state) {

    }

    public void drawScreen(int mouseX, int mouseY) {

    }

    public boolean isInside(int mouseX, int mouseY, double x, double y, double x2, double y2) {
        return (mouseX > x && mouseX < x2) && (mouseY > y && mouseY < y2);
    }

    public void keyTyped(char typedChar, int keyCode) {

    }
	
}
