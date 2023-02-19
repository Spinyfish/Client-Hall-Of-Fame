package alos.stella.utils;

import alos.stella.event.EventState;
import alos.stella.event.EventTarget;
import alos.stella.event.events.MotionEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.EntityList;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public final class GUIDetectionComponent {

    protected Minecraft mc = Minecraft.getMinecraft();

    private static boolean userInterface;

    @EventTarget
    public void onMotion(MotionEvent event){
        if (event.getEventState() == EventState.PRE) {
            userInterface = false;

            if (mc.currentScreen instanceof GuiChest) {
                final ContainerChest container = (ContainerChest) mc.thePlayer.openContainer;

                int confidence = 0, totalSlots = 0, amount = 0;

                for (final Slot slot : container.inventorySlots) {
                    if (slot.getHasStack() && amount++ <= 26 /* Amount of slots in a chest */) {
                        final ItemStack itemStack = slot.getStack();

                        if (itemStack == null) {
                            continue;
                        }

                        final String name = itemStack.getDisplayName();
                        final String expectedName = expectedName(itemStack);
                        final String strippedName = name.toLowerCase().replace(" ", "");
                        final String strippedExpectedName = expectedName.toLowerCase().replace(" ", "");

                        if (strippedName.contains(strippedExpectedName)) {
                            confidence -= 0.1;
                        } else {
                            confidence++;
                        }

                        totalSlots++;
                    }
                }

                userInterface = (float) confidence / (float) totalSlots > 0.5f;
            }
        }
    };

    public static boolean inGUI() {
        return userInterface;
    }

    private String expectedName(final ItemStack stack) {
        String s = ("" + StatCollector.translateToLocal(stack.getUnlocalizedName() + ".name")).trim();
        final String s1 = EntityList.getStringFromID(stack.getMetadata());

        if (s1 != null) {
            s = s + " " + StatCollector.translateToLocal("entity." + s1 + ".name");
        }

        return s;
    }
}
