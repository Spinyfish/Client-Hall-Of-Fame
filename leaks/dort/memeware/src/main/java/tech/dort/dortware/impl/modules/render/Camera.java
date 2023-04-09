package tech.dort.dortware.impl.modules.render;

import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;

public class Camera extends Module {

    public final BooleanValue noScoreboard = new BooleanValue("No Scoreboard", this, true);
    public final BooleanValue clearChat = new BooleanValue("Clear Chat", this, false);
    public final BooleanValue smoothChat = new BooleanValue("Smooth Chat", this, false);
    public final BooleanValue noHurtCam = new BooleanValue("No Hurt Cam", this, true);

    public Camera(ModuleData moduleData) {
        super(moduleData);
        register(noScoreboard, clearChat, smoothChat, noHurtCam);
    }
}
