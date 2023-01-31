package intent.AquaDev.aqua.command.impl.commands;

import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.command.Command;
import intent.AquaDev.aqua.utils.ChatUtil;
import net.minecraft.client.gui.GuiScreen;

public class Ign extends Command {
   public Ign() {
      super("ign");
   }

   @Override
   public void execute(String[] args) {
      ChatUtil.sendChatMessageWithPrefix("§bYour Username: " + Aqua.INSTANCE.ircClient.getIngameName());
      ChatUtil.sendChatMessageWithPrefix("§bCopyt to Clipoard");
      GuiScreen.setClipboardString(Aqua.INSTANCE.ircClient.getIngameName());
   }
}
