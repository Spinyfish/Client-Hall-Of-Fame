package me.uncodable.srt.impl.modules.impl.miscellaneous;

import java.util.ArrayList;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.entity.EventUpdate;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import net.minecraft.entity.item.EntityBoat;

@ModuleInfo(
   internalName = "MinemenBoats",
   name = "Minemen Boats",
   desc = "Allows you to spawn a shit ton of boats.\nThis is the most retarded module I've ever made.\n\nWARNING: This module can ban you on servers (from jumping on the boats). Beware...",
   category = Module.Category.MISCELLANEOUS,
   legit = true
)
public class MinemenBoats extends Module {
   private float yawIncrement;
   private double x;
   private double y;
   private double z;
   private final ArrayList<EntityBoat> boats = new ArrayList<>();

   public MinemenBoats(int key, boolean enabled) {
      super(key, enabled);
   }

   @Override
   public void onEnable() {
      if (MC.thePlayer != null) {
         this.x = MC.thePlayer.posX;
         this.y = MC.thePlayer.posY;
         this.z = MC.thePlayer.posZ;
      } else {
         this.toggle();
      }
   }

   @Override
   public void onDisable() {
      this.boats.forEach(boat -> MC.theWorld.removeEntity(boat));
      this.boats.clear();
      this.yawIncrement = 0.0F;
   }

   @EventTarget(
      target = EventUpdate.class
   )
   public void onUpdate(EventUpdate e) {
      if (this.yawIncrement < 360.0F) {
         EntityBoat boat = new EntityBoat(MC.theWorld, this.x, this.y, this.z);
         this.boats.add(boat);
         boat.rotationYaw = ++this.yawIncrement;
         MC.theWorld.spawnEntityInWorld(boat);
      }
   }
}
