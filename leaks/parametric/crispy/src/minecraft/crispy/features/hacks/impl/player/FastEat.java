package crispy.features.hacks.impl.player;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.superblaubeere27.valuesystem.NumberValue;

@HackInfo(name = "FastEat", category = Category.PLAYER)
public class FastEat extends Hack {
    NumberValue<Integer> packets = new NumberValue<Integer>("Packets", 1, 1, 50);

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate)
            if (mc.thePlayer.inventory.getCurrentItem() != null && mc.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemFood && mc.thePlayer.isEating()) {
                mc.thePlayer.stopUsingItem();
                try {
                    for (int i = 0; i < packets.getObject(); i++) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
                    }
                } catch (Exception ignored) { }
            }


    }
}
