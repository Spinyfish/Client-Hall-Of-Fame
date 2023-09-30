package felix.module.impl.combat;

import felix.Client;
import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.module.impl.movement.Flight;
import felix.module.impl.world.Scaffold;
import felix.util.other.MathUtils;
import felix.value.impl.BooleanValue;
import felix.value.impl.NumberValue;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class AutoPot extends Module {
    private final BooleanValue speed;
    private final BooleanValue regen;
    private final NumberValue<Float> health;
    public boolean potted;
    private long lastMs;

    public AutoPot() {
        super("AutoPot", 0, ModuleCategory.COMBAT);
        speed = new BooleanValue("Speed Pots", true);
        regen = new BooleanValue("Regen Pots", true);
        health = new NumberValue("Health", 7.0f, 2.0f, 9.5f, 0.5f);
        addValues(speed, regen, health);
    }

    @Handler
    public void onMotionUpdate(final EventMotionUpdate event) {
    	setSuffix("" + MathUtils.preciseRound(health.getValue(), 1));
        if (System.currentTimeMillis() - lastMs >= 200L && potted) {
            potted = false;
        }
        final int potIndex = getPotIndex();
        final int[] array = {6, -1, -1};
        if (regen.isEnabled()) {
            array[1] =10;
        }
        if (speed.isEnabled()) {
            array[2] = 1;
        }
        for (final int n : array) {
            if (n != -1) {
                if (n == 6 || n == 10) {
                    if (System.currentTimeMillis() - lastMs > 900L && !mc.thePlayer.isPotionActive(n) && mc.thePlayer.getHealth() < health.getValue() * 2.0f) {
                        splashPot(potIndex, n);
                    }
                } else if (System.currentTimeMillis() - lastMs > 1000L && !mc.thePlayer.isPotionActive(n)) {
                    splashPot(potIndex, n);
                }
            }
        }
    }

    public void clickWindow(final int n, final int n2) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, n, n2, 2, mc.thePlayer);
    }

    float[] rotations() {
        return new float[]{mc.thePlayer.rotationYaw, 90.0f};
    }

    int getPotIndex() {
        int n = 5;
        for (int i = 36; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                n = i - 36;
                break;
            }
            if (mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemPotion) {
                n = i - 36;
                break;
            }
        }
        return n;
    }

    void splashPot(final int n, final int n2) {
        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() && (mc.currentScreen == null)) {
                final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (stack.getItem() instanceof ItemPotion && mc.thePlayer.onGround && !Client.INSTANCE.getModuleManager().getModule(Scaffold.class).isEnabled() && !Client.INSTANCE.getModuleManager().getModule(Flight.class).isEnabled()) {
                    final ItemPotion itemPotion = (ItemPotion) stack.getItem();
                    if (itemPotion.getEffects(stack).isEmpty()) {
                        return;
                    }
                    if (itemPotion.getEffects(stack).get(0).getPotionID() == n2 && ItemPotion.isSplash(stack.getItemDamage()) && isBetter(itemPotion, stack)) {
                        if (36 + n != i) {
                            clickWindow(i, n);
                        }
                        lastMs = System.currentTimeMillis();
                        final int currentItem = mc.thePlayer.inventory.currentItem;
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(n));
                        //event.setPitch(90f);
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(rotations()[0], rotations()[1], mc.thePlayer.onGround));
                        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(currentItem));
                        potted = true;
                        break;
                    }
                }
            }
        }
    }

    boolean isBetter(final ItemPotion itemPotion, final ItemStack itemStack) {
        if (itemPotion.getEffects(itemStack) == null || itemPotion.getEffects(itemStack).size() != 1) {
            return false;
        }
        final PotionEffect potionEffect = itemPotion.getEffects(itemStack).get(0);
        final int potionID = potionEffect.getPotionID();
        final int amplifier = potionEffect.getAmplifier();
        final int duration = potionEffect.getDuration();
        for (int i = 9; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (stack.getItem() instanceof ItemPotion) {
                    final ItemPotion itemPotion2 = (ItemPotion) stack.getItem();
                    if (itemPotion2.getEffects(stack) != null) {
                        for (final PotionEffect potionEffect2 : itemPotion2.getEffects(stack)) {
                            final int potionID2 = potionEffect2.getPotionID();
                            final int amplifier2 = potionEffect2.getAmplifier();
                            final int duration2 = potionEffect2.getDuration();
                            if (potionID2 == potionID && ItemPotion.isSplash(stack.getItemDamage())) {
                                if (amplifier2 > amplifier) {
                                    return false;
                                }
                                if (amplifier2 == amplifier && duration2 > duration) {
                                    return false;
                                }
                                continue;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
