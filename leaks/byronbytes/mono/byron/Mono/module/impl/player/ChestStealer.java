package byron.Mono.module.impl.player;

import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import com.google.common.eventbus.Subscribe;
import net.minecraft.inventory.ContainerChest;

@ModuleInterface(name = "ChestStealer", description = "Steals chests.", category = Category.Player)
public class ChestStealer extends Module {
    public static boolean silent = true;
    long a;
    public static boolean enabled;

    @Subscribe
    public void onUpdate(EventUpdate e) {

        if (mc.thePlayer == null) {
        }

        if (mc.thePlayer.openContainer != null && mc.thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest var2 = (ContainerChest) mc.thePlayer.openContainer;

            for (int var3 = 0; var3 < var2.getLowerChestInventory().getSizeInventory(); ++var3) {
                if (var2.getLowerChestInventory().getStackInSlot(var3) != null) {
                    mc.playerController.windowClick(var2.windowId, var3, 0, 1, mc.thePlayer);
                }
            }

            var2.getInventory().isEmpty();
        }

        if (mc.thePlayer.openContainer != null && mc.thePlayer.openContainer instanceof ContainerChest) {
            mc.inGameHasFocus = true;
            mc.mouseHelper.grabMouseCursor();
        }

    }

    @Override
    public void onEnable()
    {
        super.onEnable();
        enabled = true;
        this.a = 0L;
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        enabled = false;
    }
    }

