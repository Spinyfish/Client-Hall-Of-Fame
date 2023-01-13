package me.uncodable.srt.impl.utils;

import com.google.common.collect.Ordering;
import java.util.List;
import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.BlockPos;

public class EntityUtils {
   private static final Minecraft MC = Minecraft.getMinecraft();

   public static void teleportToPos(BlockPos pos, boolean teleportBack) {
      for(double i = 0.0; i < MC.thePlayer.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()); ++i) {
         double tpX = MC.thePlayer.posX
            + i / MC.thePlayer.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()) * ((double)pos.getX() - MC.thePlayer.posX);
         double tpY = MC.thePlayer.posY
            + i / MC.thePlayer.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()) * ((double)pos.getY() - MC.thePlayer.posY);
         double tpZ = MC.thePlayer.posZ
            + i / MC.thePlayer.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()) * ((double)pos.getZ() - MC.thePlayer.posZ);
         BlockPos tpPos = new BlockPos(tpX, tpY, tpZ);
         MC.thePlayer
            .sendQueue
            .addToSendQueue(
               new C03PacketPlayer.C06PacketPlayerPosLook(
                  (double)tpPos.getX(), (double)tpPos.getY(), (double)tpPos.getZ(), MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch, true
               )
            );
      }

      MC.thePlayer
         .sendQueue
         .addToSendQueue(
            new C03PacketPlayer.C06PacketPlayerPosLook(
               (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch, true
            )
         );
      if (teleportBack) {
         teleportBackPos(pos);
      }
   }

   public static void teleportToEntity(Entity entity, boolean teleportBack) {
      for(double i = 0.0; i < (double)MC.thePlayer.getDistanceToEntity(entity); ++i) {
         double tpX = MC.thePlayer.posX + i / (double)MC.thePlayer.getDistanceToEntity(entity) * (entity.posX - MC.thePlayer.posX);
         double tpY = MC.thePlayer.posY + i / (double)MC.thePlayer.getDistanceToEntity(entity) * (entity.posY - MC.thePlayer.posY);
         double tpZ = MC.thePlayer.posZ + i / (double)MC.thePlayer.getDistanceToEntity(entity) * (entity.posZ - MC.thePlayer.posZ);
         BlockPos tpPos = new BlockPos(tpX, tpY, tpZ);
         MC.thePlayer
            .sendQueue
            .addToSendQueue(
               new C03PacketPlayer.C06PacketPlayerPosLook(
                  (double)tpPos.getX(), (double)tpPos.getY(), (double)tpPos.getZ(), MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch, true
               )
            );
      }

      MC.thePlayer
         .sendQueue
         .addToSendQueue(
            new C03PacketPlayer.C06PacketPlayerPosLook(entity.posX, entity.posY, entity.posZ, MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch, true)
         );
      if (teleportBack) {
         teleportBack(entity);
      }
   }

   public static void teleportToEntityGroundOnly(Entity entity, boolean teleportBack) {
      for(double i = 0.0; i < (double)MC.thePlayer.getDistanceToEntity(entity); ++i) {
         double tpX = MC.thePlayer.posX + i / (double)MC.thePlayer.getDistanceToEntity(entity) * (entity.posX - MC.thePlayer.posX);
         double tpZ = MC.thePlayer.posZ + i / (double)MC.thePlayer.getDistanceToEntity(entity) * (entity.posZ - MC.thePlayer.posZ);
         BlockPos tpPos = new BlockPos(tpX, (double)Math.round(MC.thePlayer.getEntityBoundingBox().minY), tpZ);
         MC.thePlayer
            .sendQueue
            .addToSendQueue(
               new C03PacketPlayer.C06PacketPlayerPosLook(
                  (double)tpPos.getX(), (double)tpPos.getY(), (double)tpPos.getZ(), MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch, true
               )
            );
      }

      MC.thePlayer
         .sendQueue
         .addToSendQueue(
            new C03PacketPlayer.C06PacketPlayerPosLook(entity.posX, entity.posY, entity.posZ, MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch, true)
         );
      if (teleportBack) {
         teleportBackGroundOnly(entity);
      }
   }

   public static void teleportBackPos(BlockPos pos) {
      for(double i = MC.thePlayer.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()); i > 0.0; --i) {
         double tpX = MC.thePlayer.posX
            + i / MC.thePlayer.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()) * ((double)pos.getX() - MC.thePlayer.posX);
         double tpY = MC.thePlayer.posY
            + i / MC.thePlayer.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()) * ((double)pos.getY() - MC.thePlayer.posY);
         double tpZ = MC.thePlayer.posZ
            + i / MC.thePlayer.getDistance((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()) * ((double)pos.getZ() - MC.thePlayer.posZ);
         BlockPos tpPos = new BlockPos(tpX, tpY, tpZ);
         MC.thePlayer
            .sendQueue
            .addToSendQueue(
               new C03PacketPlayer.C06PacketPlayerPosLook(
                  (double)tpPos.getX(), (double)tpPos.getY(), (double)tpPos.getZ(), MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch, true
               )
            );
      }

      MC.thePlayer
         .sendQueue
         .addToSendQueue(
            new C03PacketPlayer.C06PacketPlayerPosLook(
               MC.thePlayer.posX, MC.thePlayer.posY, MC.thePlayer.posZ, MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch, true
            )
         );
   }

   public static void teleportBack(Entity entity) {
      for(double i = (double)MC.thePlayer.getDistanceToEntity(entity); i > 0.0; --i) {
         double tpX = MC.thePlayer.posX + i / (double)MC.thePlayer.getDistanceToEntity(entity) * (entity.posX - MC.thePlayer.posX);
         double tpY = MC.thePlayer.posY + i / (double)MC.thePlayer.getDistanceToEntity(entity) * (entity.posY - MC.thePlayer.posY);
         double tpZ = MC.thePlayer.posZ + i / (double)MC.thePlayer.getDistanceToEntity(entity) * (entity.posZ - MC.thePlayer.posZ);
         BlockPos tpPos = new BlockPos(tpX, tpY, tpZ);
         MC.thePlayer
            .sendQueue
            .addToSendQueue(
               new C03PacketPlayer.C06PacketPlayerPosLook(
                  (double)tpPos.getX(), (double)tpPos.getY(), (double)tpPos.getZ(), MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch, true
               )
            );
      }

      MC.thePlayer
         .sendQueue
         .addToSendQueue(
            new C03PacketPlayer.C06PacketPlayerPosLook(
               MC.thePlayer.posX, MC.thePlayer.posY, MC.thePlayer.posZ, MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch, true
            )
         );
   }

   public static void teleportBackGroundOnly(Entity entity) {
      for(double i = (double)MC.thePlayer.getDistanceToEntity(entity); i > 0.0; --i) {
         double tpX = MC.thePlayer.posX + i / (double)MC.thePlayer.getDistanceToEntity(entity) * (entity.posX - MC.thePlayer.posX);
         double tpZ = MC.thePlayer.posZ + i / (double)MC.thePlayer.getDistanceToEntity(entity) * (entity.posZ - MC.thePlayer.posZ);
         BlockPos tpPos = new BlockPos(tpX, MC.thePlayer.posY, tpZ);
         MC.thePlayer
            .sendQueue
            .addToSendQueue(
               new C03PacketPlayer.C06PacketPlayerPosLook(
                  (double)tpPos.getX(), (double)tpPos.getY(), (double)tpPos.getZ(), MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch, true
               )
            );
      }

      MC.thePlayer
         .sendQueue
         .addToSendQueue(
            new C03PacketPlayer.C06PacketPlayerPosLook(
               MC.thePlayer.posX, MC.thePlayer.posY, MC.thePlayer.posZ, MC.thePlayer.rotationYaw, MC.thePlayer.rotationPitch, true
            )
         );
   }

   public static List<NetworkPlayerInfo> getPlayerList() {
      Ordering<NetworkPlayerInfo> field_175252_a = Ordering.from(new GuiPlayerTabOverlay.PlayerComparator());
      NetHandlerPlayClient nethandlerplayclient = Minecraft.getMinecraft().thePlayer.sendQueue;
      return field_175252_a.sortedCopy(nethandlerplayclient.getPlayerInfoMap());
   }

   public static String getPlayerName(NetworkPlayerInfo networkPlayerInfoIn) {
      return networkPlayerInfoIn.getDisplayName() != null
         ? networkPlayerInfoIn.getDisplayName().getFormattedText()
         : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
   }

   public static boolean isOpponentMoving(EntityLivingBase entity) {
      return entity.getDistance(entity.prevPosX, entity.getEntityBoundingBox().minY, entity.prevPosZ) > 0.0;
   }

   public static boolean skipEntity(EntityLivingBase entity, Module module) {
      Setting players = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_PLAYERS", Setting.Type.CHECKBOX);
      Setting animals = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_ANIMALS", Setting.Type.CHECKBOX);
      Setting mobs = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_MOBS", Setting.Type.CHECKBOX);
      Setting teams = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_TEAMS", Setting.Type.CHECKBOX);
      Setting weapons_only = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_WEAPONS_ONLY", Setting.Type.CHECKBOX);
      Setting invisibles = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_INVISIBLES", Setting.Type.CHECKBOX);
      Setting friends = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_FRIENDS", Setting.Type.CHECKBOX);
      Setting dead = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_DEAD", Setting.Type.CHECKBOX);
      Setting reach_value = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_REACH_VALUE", Setting.Type.SLIDER);
      Setting infinite_reach_value = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_INFINITE_REACH_VALUE", Setting.Type.SLIDER);
      Setting mode = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_GENERAL_COMBO_BOX", Setting.Type.COMBO_BOX);
      return entity != null
         && module != null
         && MC.thePlayer != entity
         && (
            reach_value == null
               || !((double)MC.thePlayer.getDistanceToEntity(entity) > reach_value.getCurrentValue())
               || (infinite_reach_value == null || !((double)MC.thePlayer.getDistanceToEntity(entity) > infinite_reach_value.getCurrentValue()))
                  && mode.getCurrentCombo().contains("Infinite Aura")
         )
         && (players == null || players.isTicked() || !(entity instanceof EntityPlayer))
         && (animals == null || animals.isTicked() || !(entity instanceof EntityAnimal))
         && (mobs == null || mobs.isTicked() || !(entity instanceof EntityMob))
         && (teams == null || !teams.isTicked() || !MC.thePlayer.isOnSameTeam(entity))
         && (
            weapons_only == null
               || !weapons_only.isTicked()
               || MC.thePlayer.getHeldItem() != null
                  && (MC.thePlayer.getHeldItem().getItem() instanceof ItemSword || MC.thePlayer.getHeldItem().getItem() instanceof ItemAxe)
         )
         && (invisibles == null || invisibles.isTicked() || entity.isInvisibleToPlayer(MC.thePlayer))
         && (dead == null || dead.isTicked() || !entity.isDead && entity.getHealth() != 0.0F)
         && (friends == null || friends.isTicked() || !Ries.INSTANCE.getFriendManager().isFriend(entity.getDisplayName().getUnformattedText()));
   }

   public static boolean skipEntity2(EntityLivingBase entity, Module module) {
      Setting players = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_PLAYERS", Setting.Type.CHECKBOX);
      Setting animals = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_ANIMALS", Setting.Type.CHECKBOX);
      Setting mobs = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_MOBS", Setting.Type.CHECKBOX);
      Setting teams = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_TEAMS", Setting.Type.CHECKBOX);
      Setting weapons_only = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_WEAPONS_ONLY", Setting.Type.CHECKBOX);
      Setting invisibles = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_INVISIBLES", Setting.Type.CHECKBOX);
      Setting friends = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_FRIENDS", Setting.Type.CHECKBOX);
      Setting dead = Ries.INSTANCE.getSettingManager().getSetting(module, "INTERNAL_DEAD", Setting.Type.CHECKBOX);
      return entity != null
         && module != null
         && MC.thePlayer != entity
         && (players == null || players.isTicked() || !(entity instanceof EntityPlayer))
         && (animals == null || animals.isTicked() || !(entity instanceof EntityAnimal))
         && (mobs == null || mobs.isTicked() || !(entity instanceof EntityMob))
         && (teams == null || !teams.isTicked() || !MC.thePlayer.isOnSameTeam(entity))
         && (
            weapons_only == null
               || !weapons_only.isTicked()
               || MC.thePlayer.getHeldItem() != null
                  && (MC.thePlayer.getHeldItem().getItem() instanceof ItemSword || MC.thePlayer.getHeldItem().getItem() instanceof ItemAxe)
         )
         && (invisibles == null || invisibles.isTicked() || entity.isInvisibleToPlayer(MC.thePlayer))
         && (dead == null || dead.isTicked() || !entity.isDead && entity.getHealth() != 0.0F)
         && (friends == null || friends.isTicked() || !Ries.INSTANCE.getFriendManager().isFriend(entity.getDisplayName().getUnformattedText()));
   }
}
