package club.pulsive.impl.module.impl.movement;

import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.property.implementations.EnumProperty;
import club.pulsive.impl.property.implementations.MultiSelectEnumProperty;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import net.minecraft.util.MovementInput;
import org.lwjgl.input.Keyboard;


@ModuleInfo(name = "GuiMove",  description = "It the gui move", category = Category.MOVEMENT)

public class GuiMove extends Module {
    public static MultiSelectEnumProperty<MODES> elements = new MultiSelectEnumProperty<MODES>("Elements", Lists.newArrayList(MODES.CLICKGUI), MODES.values());

    public static void updatePlayerMoveState() {
        MovementInput movementInput = mc.thePlayer.movementInput;

        movementInput.setMoveStrafe(0.0F);
        movementInput.setMoveForward(0.0F);

        if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
            movementInput.setMoveForward(movementInput.getMoveForward() + 1);
        }

        if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
            movementInput.setMoveForward(movementInput.getMoveForward() - 1);
        }

        if (Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode())) {
            movementInput.setMoveStrafe(movementInput.getMoveStrafe() + 1);
        }

        if (Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode())) {
            movementInput.setMoveStrafe(movementInput.getMoveStrafe() - 1);
        }

        movementInput.setJump(Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode()));
    }

    @AllArgsConstructor
    public enum MODES {
        CLICKGUI("ClickGui"),
        CHEST("Chest"),
        INVENTORY("Inventory");

        private final String modeName;

        @Override
        public String toString() {return modeName;}
    }
}
