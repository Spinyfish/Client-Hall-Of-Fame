package alos.stella.ui.draggble;

import alos.stella.Stella;
import alos.stella.module.modules.visual.HUD;

public class DragModuleList extends DragComp {

    public DragModuleList() {
        super("ModuleList", 700, 0, 1, 1);
    }

    @Override
    public boolean allowDraw() {
        return Stella.moduleManager.getModule(HUD.class).getState();
    }
}