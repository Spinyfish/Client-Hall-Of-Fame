package v4n1ty;

import de.Hero.clickgui.ClickGUI;
import de.Hero.settings.SettingsManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;
import v4n1ty.module.ModuleManager;
import v4n1ty.module.movement.Sprint;
import v4n1ty.utils.misc.SessionChanger;
import v4n1ty.utils.render.ColorUtils;

import javax.vecmath.Vector2d;
import java.awt.Color;

public class V4n1ty {

    public static V4n1ty instance = new V4n1ty();
    public static final Color firstColor = new Color(207, 0, 255);
    public static final Color secondColor = new Color(123, 0, 255);
    public static SettingsManager settingsManager;
    public static ModuleManager moduleManager;
    public static ClickGUI clickGUI;
    public static Sprint sprint;
    public static Color getHudColor(float y){
        return ColorUtils.blend(firstColor, secondColor, new Vector2d(0, y));
    }

    public void init(){
        settingsManager = new SettingsManager();
        moduleManager = new ModuleManager();
        clickGUI = new ClickGUI();

        Display.setTitle("V4n1ty Client - (b1.0.1)");
    }
}