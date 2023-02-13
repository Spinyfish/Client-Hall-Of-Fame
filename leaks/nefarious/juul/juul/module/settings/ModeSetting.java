package juul.module.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ModeSetting extends Setting{

    public final List<String> modes;
    private String defaultMode, currentMode;
    private int modeIndex;

    public ModeSetting(String... modes)
    {
        this.modes = Arrays.asList(modes);
        this.modeIndex = this.modes.indexOf(modes);
        this.defaultMode = modes[0];

        if (currentMode == null) currentMode = defaultMode;
    }

    public String getMode() {
        return currentMode;
    }

    public boolean is(String mode) {
        return currentMode.equals(mode);
    }

    public void cycleForwards() {
        modeIndex++;
        if (modeIndex > modes.size() - 1) modeIndex = 0;
        currentMode = modes.get(modeIndex);
    }

    public void cycleBackwards() {
        modeIndex--;
        if (modeIndex < 0) modeIndex = modes.size() - 1;
        currentMode = modes.get(modeIndex);
    }

    public void setCurrentMode(String currentMode) {
        this.currentMode = currentMode;
    }

}
