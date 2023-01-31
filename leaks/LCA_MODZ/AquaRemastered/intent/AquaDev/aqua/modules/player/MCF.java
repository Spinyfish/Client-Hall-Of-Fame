package intent.AquaDev.aqua.modules.player;

import events.Event;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.ChatUtil;
import intent.AquaDev.aqua.utils.FriendSystem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;

public class MCF extends Module {
   private long milliSeconds = System.currentTimeMillis();

   public MCF() {
      super("MCF", Module.Type.Player, "MCF", 0, Category.Player);
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @Override
   public void onEvent(Event event) {
      if (Mouse.isButtonDown(2)) {
         Entity entity = mc.objectMouseOver.entityHit;
         if (entity != null && entity instanceof EntityPlayer && System.currentTimeMillis() - this.milliSeconds > 300L) {
            if (FriendSystem.getFriends().contains(entity.getName())) {
               FriendSystem.removeFriend(entity.getName());
               ChatUtil.sendChatMessageWithPrefix("Removed " + entity.getName() + " from your friend list!");
               this.milliSeconds = System.currentTimeMillis();
            } else {
               FriendSystem.addFriend(entity.getName());
               ChatUtil.sendChatMessageWithPrefix("Added " + entity.getName() + " to your friend list!");
               this.milliSeconds = System.currentTimeMillis();
            }
         }
      }
   }
}
