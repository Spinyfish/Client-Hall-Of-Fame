package alos.stella.ui.draggble;

import com.google.common.collect.Lists;


import java.util.List;

public class DragModern {

    private DragScreen screen;
    private final List<DragComp> components;

    public DragModern() {
        screen = new DragScreen();
        components = Lists.newArrayList();
//        components.add(new DragTargetHUD());
//        components.add(new DragWaterMark());
//        components.add(new DragCoords());
//        components.add(new DragPotion());
//        components.add(new DragSessionInfo());
//        components.add(new DragArmor());
//        components.add(new DragUserInfo());
//        components.add(new DragTimer());
//        components.add(new DragIndicator());
        components.add(new DragModuleList());
    }

    public DragScreen getScreen() {
        return screen;
    }

    public List<DragComp> getComponents() {
        return components;
    }

    public DragComp getDraggableComponentByClass(Class<? extends DragComp> classs) {
        for (DragComp draggableComponent : components) {
            if (draggableComponent.getClass() == classs) {
                return draggableComponent;
            }
        }

        return null;
    }

}