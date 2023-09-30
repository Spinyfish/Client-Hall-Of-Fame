package frapppyz.cutefurry.pics.modules.impl.player;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.Render;
import frapppyz.cutefurry.pics.event.impl.Update;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Boolean;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;
import net.minecraft.inventory.ContainerChest;

public class ChestStealer extends Mod {
    private Mode delay = new Mode("Ticks", "1", "0", "1", "2", "3", "4", "5");
    private Boolean closeChest = new Boolean("Close Chest", true);
    public ChestStealer() {
        super("Stealer", "Steals your nans thigh highs from chests.", 0, Category.PLAYER);
        addSettings(closeChest, delay);
    }

    private int ticks;
    private int emptyslots;

    public void onEvent(Event e){
        if(e instanceof Render) {
            this.setSuffix(delay.getMode());
        }
        if(e instanceof Update) {
            emptyslots = 0;
            ticks++;

            if (mc.thePlayer.openContainer instanceof ContainerChest && ticks >= Integer.parseInt(delay.getMode())) {

                ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;

                for(int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
                    if((chest.getLowerChestInventory().getStackInSlot(i) != null) && ticks >= 1) {
                        mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                        if(!delay.is("0"))
                            ticks = 0;
                    }else{
                        emptyslots++;
                    }

                }

                if(emptyslots == chest.getLowerChestInventory().getSizeInventory() && closeChest.isToggled()){
                    mc.thePlayer.closeScreen();
                }
            }
        }
    }
}
