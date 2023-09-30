package dev.tenacity.module.impl.misc;

import dev.tenacity.event.impl.game.world.TickEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import dev.tenacity.utils.player.ChatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import java.util.ArrayList;

public class MurderDetector extends Module {
    public static int[] itemIds = {288, 396, 412, 398, 75, 50};
    public static Item[] itemTypes = new Item[]{
            Items.fishing_rod,
            Items.diamond_hoe,
            Items.golden_hoe,
            Items.iron_hoe,
            Items.stone_hoe,
            Items.wooden_hoe,
            Items.stone_sword,
            Items.diamond_sword,
            Items.golden_sword,
            ItemBlock.getItemFromBlock(Blocks.sponge),
            Items.iron_sword,
            Items.wooden_sword,
            Items.diamond_axe,
            Items.golden_axe,
            Items.iron_axe,
            Items.stone_axe,
            Items.diamond_pickaxe,
            Items.wooden_axe,
            Items.golden_pickaxe,
            Items.iron_pickaxe,
            Items.stone_pickaxe,
            Items.wooden_pickaxe,
            Items.stone_shovel,
            Items.diamond_shovel,
            Items.golden_shovel,
            Items.iron_shovel,
            Items.wooden_shovel
    };
    public static ArrayList<EntityPlayer> killerData = new ArrayList<>();

    public MurderDetector() {
        super("MurderDetector", Category.MISC, "Detect the murders");
    }

    @Override
    public void onTickEvent(TickEvent event) {
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                if (entityLivingBase instanceof EntityPlayer && entity != mc.thePlayer) {
                    EntityPlayer player = (EntityPlayer) entityLivingBase;
                    if (player.inventory.getCurrentItem() != null) {
                        if (!killerData.contains(player)) {
                            if (isWeapon(player.inventory.getCurrentItem().getItem())) {
                                ChatUtil.print(true, "[MurderDetector] " + player.getName() + " is Killer!");
                                NotificationManager.post(NotificationType.WARNING, "MurderDetector", player.getName() + " is Killer!");
                                killerData.add(player);
                            }
                        } else {
                            if (!isWeapon(player.inventory.getCurrentItem().getItem())) {
                                killerData.remove(player);
                            }
                        }

                    }
                }
            }
        }
    }

    public boolean isWeapon(Item item) {
        for (int id : itemIds) {
            Item itemId = Item.getItemById(id);
            if (item == itemId) {
                return true;
            }
        }
        for (Item id : itemTypes) {
            if (item == id) {
                return true;
            }
        }
        return false;
    }
}