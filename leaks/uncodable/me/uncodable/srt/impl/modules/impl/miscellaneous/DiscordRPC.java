package me.uncodable.srt.impl.modules.impl.miscellaneous;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.miscellaneous.EventMinecraftLoop;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;

@ModuleInfo(
   internalName = "DiscordRPC",
   name = "Discord RPC",
   desc = "Allows you to show off SRT to your Discord friends (and pedophiles).",
   category = Module.Category.MISCELLANEOUS,
   legit = true
)
public class DiscordRPC extends Module {
   private boolean executing;
   private long created;
   private int counter;
   private int switchCounter;
   private Thread discordRPCThread;
   private String message;

   public DiscordRPC(int key, boolean enabled) {
      super(key, enabled);
   }

   @Override
   public void onEnable() {
      this.executing = true;
      this.created = System.currentTimeMillis();
      DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(discordUser -> this.update("Initializing...")).build();
      net.arikia.dev.drpc.DiscordRPC.discordInitialize("901588407766122587", handlers, true);
      this.discordRPCThread = new Thread("Discord RPC Thread") {
         @Override
         public void run() {
            while(DiscordRPC.this.executing) {
               net.arikia.dev.drpc.DiscordRPC.discordRunCallbacks();
            }
         }
      };
      this.discordRPCThread.start();
   }

   @Override
   public void onDisable() {
      this.executing = false;
      net.arikia.dev.drpc.DiscordRPC.discordShutdown();
   }

   @EventTarget(
      target = EventMinecraftLoop.class
   )
   public void onLoop(EventMinecraftLoop e) {
      ++this.counter;
      if (this.counter > Minecraft.debugFPS * 2) {
         ++this.switchCounter;
         if (this.switchCounter > 5) {
            this.switchCounter = 0;
         }

         this.counter = 0;
      }

      switch(this.switchCounter) {
         case 0:
            int i = 0;

            for(Module module : Ries.INSTANCE.getModuleManager().getModules()) {
               if (module.isEnabled()) {
                  ++i;
               }
            }

            this.message = String.format("%d Modules Enabled", i);
            break;
         case 1:
            this.message = String.format("Build: %s", Ries.INSTANCE.getBuild());
            break;
         case 2:
            if (MC.theWorld == null) {
               this.message = "In Main Menu";
            } else {
               this.message = String.format("Playing On: %s", MC.getCurrentServerData() == null ? "Single Player" : MC.getCurrentServerData().serverIP);
            }
            break;
         case 3:
            this.message = String.format("Playing Legit: %s", Ries.INSTANCE.getModuleManager().getModuleByName("Safeguard").isEnabled() ? "Yes" : "No");
            break;
         case 4:
            this.message = "Developed by uncodable with <3";
            break;
         case 5:
            this.message = "The Most Hated Client Ever Conceived.";
      }

      this.update("Drag-strip Ready", this.message);
   }

   public void update(String firstLine, String secondLine) {
      DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder(secondLine);
      builder.setBigImage("main", "Supercharged 6.2L HEMI® V8");
      builder.setDetails(firstLine);
      builder.setStartTimestamps(this.created);
      net.arikia.dev.drpc.DiscordRPC.discordUpdatePresence(builder.build());
   }

   public void update(String firstLine) {
      this.update(firstLine, "");
   }
}
