//package de.fanta.module.impl.combat;
//
//import net.minecraft.block.Block;
//import net.minecraft.block.material.Material;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.util.BlockPos;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import de.fanta.events.Event;
//import de.fanta.events.listeners.EventTick;
//import de.fanta.module.Module;
//import de.fanta.setting.Setting;
//import de.fanta.setting.settings.CheckBox;
//import de.fanta.utils.PlayerUtil;
//import de.fanta.utils.TimeUtil;
//import javafx.scene.paint.Color;
//
//public class AntiBot extends Module {
//
//    public static AntiBot INSTANCE = new AntiBot();
//    private final TimeUtil time = new TimeUtil();
//    public static List<EntityPlayer> bots = new ArrayList<>();
//    public List<EntityPlayer> swing = new ArrayList<>();
//    public List<EntityPlayer> nearby = new ArrayList<>();
//
//    public AntiBot() {
//     //   super("AntiBot", 0, Type.Combat, );
//
//        this.settings.add(new Setting("Invisible", new CheckBox(false)));
//        this.settings.add(new Setting("Tablist", new CheckBox(false)));
//        this.settings.add(new Setting("Movement", new CheckBox(false)));
//        this.settings.add(new Setting("Hypixel", new CheckBox(false)));
//        this.settings.add(new Setting("Swing", new CheckBox(false)));
//        this.settings.add(new Setting("OnGround", new CheckBox(false)));
//        this.settings.add(new Setting("Ticks", new CheckBox(false)));
//        this.settings.add(new Setting("Checks", new CheckBox(false)));
//    }
//
//    public void onDisable(){
//        bots.clear();
//    }
//
//
//    @Override
//    public void onEvent(Event e) {
//        if (e instanceof EventTick) {
//            for (Entity entity : mc.theWorld.loadedEntityList) {
//                if (entity != mc.thePlayer && entity instanceof EntityPlayer) {
//                    if (((CheckBox) this.getSetting("Invisible").getSetting()).isState()) {
//                        if (entity.isInvisible())
//                            bots.add((EntityPlayer) entity);
//                    }
//                    if (((CheckBox) this.getSetting("Tablist").getSetting()).isState()) {
//                        if (!PlayerUtil.isInTablist(entity) && !bots.contains(entity))
//                            bots.add((EntityPlayer) entity);
//                    }
//                    if (((CheckBox) this.getSetting("OnGround").getSetting()).isState()) {
//                        BlockPos pos = new BlockPos(entity.posX, entity.posY + 0.2D, entity.posZ);
//                        Block block = mc.theWorld.getBlockState(pos).getBlock();
//
//                        if (block.getMaterial() == Material.air && entity.onGround || entity.fallDistance != 0 && block.getMaterial() != Material.air  && !bots.contains(entity)) {
//                            bots.add((EntityPlayer) entity);
//                        }
//                    }
//                    if (((CheckBox) this.getSetting("Ticks").getSetting()).isState()) {
//
//                        if (entity.ticksExisted < 100 && !bots.contains(entity)) {
//                            bots.add((EntityPlayer) entity);
//                        }
//                    }
//
//                    if (((CheckBox) this.getSetting("Checks").getSetting()).isState()) {
//                        if ((entity.getUniqueID() == null || entity.getName() == null || entity.getEntityBoundingBox() == null || entity.fallDistance != 0 && entity.onGround && !bots.contains(entity))) {
//                            bots.add((EntityPlayer) entity);
//                        }
//
//                    }
//
//                    if (((CheckBox) this.getSetting("Movement").getSetting()).isState()) {
//                        BlockPos pos = new BlockPos(entity.posX, entity.posY - 0.1D, entity.posZ);
//                        Block block = mc.theWorld.getBlockState(pos).getBlock();
//
//                        if (!entity.isInsideOfMaterial(Material.air) && entity.fallDistance == 0 && !entity.isInsideOfMaterial(Material.water) && !entity.isInsideOfMaterial(Material.lava) && !entity.isInsideOfMaterial(Material.grass)
//                                && !entity.isInsideOfMaterial(Material.fire) && entity.onGround && !((EntityPlayer) entity).isOnLadder() && entity.lastTickPosZ != entity.posZ &&
//                                entity.lastTickPosX != entity.posX && !entity.isInsideOfMaterial(Material.plants) && !entity.isInsideOfMaterial(Material.grass) && !entity.isInsideOfMaterial(Material.leaves) && !entity.canBeCollidedWith() && !bots.contains(entity)) {
//                            bots.add((EntityPlayer) entity);
//                        }
//                    }
//                    if (((CheckBox) this.getSetting("Swing").getSetting()).isState()) {
//                        if (((EntityPlayer) entity).swingProgress > 0.0 && !swing.contains(entity)) {
//                            swing.add((EntityPlayer) entity);
//                        }
//                    } else swing.remove(entity);
//
//                    if (((CheckBox) this.getSetting("Hypixel").getSetting()).isState()) {
//                        if (isHypixelBot((EntityPlayer) entity)) {
//                            bots.add((EntityPlayer) entity);
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    public boolean isBot(EntityPlayer entity) {
//        return bots.contains(entity) && (!swing.contains(entity) && ((CheckBox) this.getSetting("Swing").getSetting()).isState());
//    }
//
//    private boolean isHypixelBot(EntityPlayer entity) {
//        if (entity.isInvisible()) return true;
//        if (!PlayerUtil.isInTablist(entity)) return true;
//        if ((entity).getDisplayName().getFormattedText().contains("NPC")) return false;
//        return false;
//    }
//
//}
