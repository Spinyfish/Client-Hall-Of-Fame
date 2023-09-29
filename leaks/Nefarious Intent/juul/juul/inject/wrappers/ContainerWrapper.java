package juul.inject.wrappers;

import java.awt.image.BufferedImage;

import juul.Juul;
import juul.inject.LUT.Version;
import juul.inject.Wrapper;

public class ContainerWrapper extends Wrapper {

    public ContainerWrapper(Object instance) {
        super("net.minecraft.inventory.Container", instance);
    }

    public int windowId() { return (int) getField("windowId"); }

    public Object getSlot(int id) { return getMethodPrimitive("getSlot", id); }

}
