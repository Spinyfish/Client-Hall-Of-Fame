/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.api.util.misc.StringUtil;
import wtf.monsoon.api.util.misc.Timer;
import wtf.monsoon.impl.event.EventPreMotion;
import wtf.monsoon.impl.event.EventUpdate;

public class LongJump
extends Module {
    public Setting<Mode> mode = new Setting<Mode>("Mode", Mode.WATCHDOG).describedBy("Mode of the module.");
    public Setting<HypixelMode> hypixelMode = new Setting<HypixelMode>("Watchdog Mode", HypixelMode.BOW).describedBy("How to bypass on Hypixel").visibleWhen(() -> this.mode.getValue().equals((Object)Mode.WATCHDOG));
    public Timer timer = new Timer();
    public Timer mineplexTimer = new Timer();
    public static int lastSlot = -1;
    protected boolean boosted = false;
    protected boolean doneBow = false;
    protected boolean hasStartedGlide = false;
    protected double motionVa = 2.8;
    private int stage;
    double distanceX = 0.0;
    double distanceZ = 0.0;
    double oldPosY = 0.0;
    double yPos = 0.0;
    boolean hasBoosted;
    boolean hasJumped;
    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = e -> {
        switch (this.mode.getValue()) {
            case WATCHDOG: {
                if (this.hypixelMode.getValue() != HypixelMode.BOW) break;
                if (this.doneBow) {
                    if (this.mc.thePlayer.hurtTime > 0) {
                        this.hasBoosted = true;
                    }
                    if (this.hasBoosted) {
                        this.mc.gameSettings.keyBindForward.pressed = true;
                    }
                } else if (this.doneBow) {
                    this.mc.gameSettings.keyBindBack.pressed = false;
                    this.mc.gameSettings.keyBindForward.pressed = false;
                    this.mc.gameSettings.keyBindRight.pressed = false;
                    this.mc.gameSettings.keyBindLeft.pressed = false;
                }
                if (!this.timer.hasTimeElapsed(1800L, false) || !this.mc.thePlayer.onGround || !this.doneBow) break;
                this.toggle();
                break;
            }
            case NCP: {
                if (this.mc.thePlayer.onGround) {
                    if (!this.hasJumped) {
                        this.mc.thePlayer.jump();
                        this.hasJumped = true;
                    }
                } else {
                    float dir = this.mc.thePlayer.rotationYaw + (float)(this.mc.thePlayer.moveForward < 0.0f ? 180 : 0) + (this.mc.thePlayer.moveStrafing > 0.0f ? -90.0f * (this.mc.thePlayer.moveForward < 0.0f ? -0.5f : (this.mc.thePlayer.moveForward > 0.0f ? 0.4f : 1.0f)) : 0.0f);
                    float xDir = (float)Math.cos((double)(dir + 90.0f) * Math.PI / 180.0);
                    float zDir = (float)Math.sin((double)(dir + 90.0f) * Math.PI / 180.0);
                    if (this.mc.thePlayer.motionY == 0.33319999363422365 && (this.mc.gameSettings.keyBindForward.isKeyDown() || this.mc.gameSettings.keyBindLeft.isKeyDown() || this.mc.gameSettings.keyBindRight.isKeyDown() || this.mc.gameSettings.keyBindBack.isKeyDown())) {
                        this.mc.thePlayer.motionX = (double)xDir * 1.0381;
                        this.mc.thePlayer.motionZ = (double)zDir * 1.0381;
                    }
                }
                if (!this.timer.hasTimeElapsed(300L, false) || !this.mc.thePlayer.onGround) break;
                this.toggle();
                break;
            }
            case FUNCRAFT: {
                if (this.mc.thePlayer.onGround) {
                    if (!this.hasJumped) {
                        this.mc.thePlayer.jump();
                        this.hasJumped = true;
                        this.timer.reset();
                    }
                } else {
                    float dir = this.mc.thePlayer.rotationYaw + (float)(this.mc.thePlayer.moveForward < 0.0f ? 180 : 0) + (this.mc.thePlayer.moveStrafing > 0.0f ? -90.0f * (this.mc.thePlayer.moveForward < 0.0f ? -0.5f : (this.mc.thePlayer.moveForward > 0.0f ? 0.4f : 1.0f)) : 0.0f);
                    float xDir = (float)Math.cos((double)(dir + 90.0f) * Math.PI / 180.0);
                    float zDir = (float)Math.sin((double)(dir + 90.0f) * Math.PI / 180.0);
                    if (this.mc.thePlayer.motionY == 0.33319999363422365 && (this.mc.gameSettings.keyBindForward.isKeyDown() || this.mc.gameSettings.keyBindLeft.isKeyDown() || this.mc.gameSettings.keyBindRight.isKeyDown() || this.mc.gameSettings.keyBindBack.isKeyDown())) {
                        this.mc.thePlayer.motionX = (double)xDir * 1.6561;
                        if (this.stage != 2) {
                            this.mc.thePlayer.motionY += (double)0.05f;
                        }
                        this.mc.thePlayer.motionZ = (double)zDir * 1.6561;
                    }
                    if (this.mc.thePlayer.motionY < 0.0) {
                        this.stage = 2;
                    }
                }
                if (!this.timer.hasTimeElapsed(700L, false) || !this.mc.thePlayer.onGround) break;
                this.toggle();
                break;
            }
            case VULCAN_BOAT: {
                if (this.mc.thePlayer.onGround) {
                    if (!this.hasJumped) {
                        this.mc.getTimer().timerSpeed = 0.7f;
                        this.mc.thePlayer.jump();
                        this.mc.thePlayer.motionY = 1.0;
                        this.hasJumped = true;
                        this.timer.reset();
                        this.stage = 0;
                    }
                } else {
                    float dir = this.mc.thePlayer.rotationYaw + (float)(this.mc.thePlayer.moveForward < 0.0f ? 180 : 0) + (this.mc.thePlayer.moveStrafing > 0.0f ? -90.0f * (this.mc.thePlayer.moveForward < 0.0f ? -0.5f : (this.mc.thePlayer.moveForward > 0.0f ? 0.4f : 1.0f)) : 0.0f);
                    float xDir = (float)Math.cos((double)(dir + 90.0f) * Math.PI / 180.0);
                    float zDir = (float)Math.sin((double)(dir + 90.0f) * Math.PI / 180.0);
                    if (this.hasJumped && (this.mc.gameSettings.keyBindForward.isKeyDown() || this.mc.gameSettings.keyBindLeft.isKeyDown() || this.mc.gameSettings.keyBindRight.isKeyDown() || this.mc.gameSettings.keyBindBack.isKeyDown())) {
                        this.mc.getTimer().timerSpeed = 1.0f;
                        this.mc.thePlayer.motionX = xDir * 6.0f;
                        this.mc.thePlayer.motionZ = zDir * 6.0f;
                        this.mc.thePlayer.motionY = 0.0;
                    }
                }
                if (this.hasJumped) {
                    ++this.stage;
                }
                PlayerUtil.sendClientMessage(this.stage + " " + this.hasJumped);
                if (this.stage < 19 || !this.hasJumped) break;
                this.player.setSpeed(0.0);
                this.mc.thePlayer.setVelocity(0.0, 0.0, 0.0);
                this.toggle();
                break;
            }
            case VERUS: {
                this.mc.thePlayer.cameraYaw = 0.099999994f;
                if (this.mc.thePlayer.onGround) {
                    if (!this.boosted) {
                        PlayerUtil.damageVerus();
                        this.mc.thePlayer.motionY = 0.8;
                        this.player.setSpeed(3.0);
                    }
                    this.boosted = true;
                }
                if (!this.timer.hasTimeElapsed(1000L, false) || !this.mc.thePlayer.onGround) break;
                this.player.setSpeed(0.0);
                this.mc.thePlayer.motionY = 0.0;
                this.toggle();
            }
        }
    };
    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        if (this.mode.getValue() == Mode.WATCHDOG) {
            switch (this.hypixelMode.getValue()) {
                case BOW: {
                    if (this.hasBoosted) {
                        this.mc.thePlayer.cameraYaw = 0.099999994f;
                        if (this.mc.thePlayer.onGround) {
                            if (this.hasJumped) break;
                            this.mc.thePlayer.jump();
                            this.mc.thePlayer.motionY = 0.7f;
                            this.hasJumped = true;
                            break;
                        }
                        if (!(this.mc.thePlayer.motionY > 0.0)) break;
                        PlayerUtil.sendClientMessage("sex");
                        this.player.setSpeed(this.player.getBaseMoveSpeed() * 1.2);
                        break;
                    }
                    this.mc.gameSettings.keyBindBack.pressed = false;
                    this.mc.gameSettings.keyBindForward.pressed = false;
                    this.mc.gameSettings.keyBindRight.pressed = false;
                    this.mc.gameSettings.keyBindLeft.pressed = false;
                    break;
                }
                case NO_DMG: {
                    if (this.mc.thePlayer.onGround && !this.timer.hasTimeElapsed(200L, false)) {
                        this.mc.getTimer().timerSpeed = 1.6f;
                        this.player.setSpeed(this.player.getBaseMoveSpeed() * 1.5);
                        this.mc.thePlayer.jump();
                        this.timer.reset();
                        break;
                    }
                    if (!this.timer.hasTimeElapsed(800L, false)) {
                        if (this.mc.thePlayer.motionY < 0.0) {
                            this.mc.getTimer().timerSpeed = 1.15f;
                            if (this.mc.thePlayer.onGround) break;
                            this.player.setSpeed(this.player.getBaseMoveSpeed() * 1.25);
                            break;
                        }
                        this.mc.getTimer().timerSpeed = 1.3f;
                        if (this.mc.thePlayer.onGround) break;
                        this.player.setSpeed(this.player.getBaseMoveSpeed() * 1.3);
                        break;
                    }
                    this.toggle();
                }
            }
        }
    };

    public LongJump() {
        super("Long Jump", "Jump longer", Category.MOVEMENT);
        this.setMetadata(() -> {
            if (this.mode.getValue() == Mode.WATCHDOG) {
                return "Watchdog (" + StringUtil.formatEnum(this.hypixelMode.getValue()) + ")";
            }
            return StringUtil.formatEnum(this.mode.getValue());
        });
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.stage = 0;
        this.timer.reset();
        this.hasBoosted = false;
        this.hasJumped = false;
        this.hasStartedGlide = false;
        this.doneBow = false;
        if (this.mode.getValue() == Mode.WATCHDOG && this.hypixelMode.getValue() == HypixelMode.BOW && this.hasBow()) {
            this.selfBow();
        }
        lastSlot = -1;
        this.oldPosY = this.mc.thePlayer.posY;
        this.yPos = this.mc.thePlayer.posY;
        this.distanceX = this.mc.thePlayer.posX;
        this.distanceZ = this.mc.thePlayer.posZ;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.mc.getTimer().timerSpeed = 1.0f;
        lastSlot = -1;
        this.mc.thePlayer.speedInAir = 0.02f;
        this.boosted = false;
        this.motionVa = 2.8;
        if (this.mode.getValue().equals((Object)Mode.WATCHDOG) && this.hypixelMode.getValue().equals((Object)HypixelMode.BOW)) {
            this.mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(this.mc.thePlayer.inventory.currentItem));
        }
        this.mc.thePlayer.speedInAir = 0.02f;
    }

    public void selfBow() {
        Thread thread = new Thread(){

            @Override
            public void run() {
                LongJump.this.mc.gameSettings.keyBindBack.pressed = false;
                LongJump.this.mc.gameSettings.keyBindForward.pressed = false;
                LongJump.this.mc.gameSettings.keyBindRight.pressed = false;
                LongJump.this.mc.gameSettings.keyBindLeft.pressed = false;
                int oldSlot = LongJump.this.mc.thePlayer.inventory.currentItem;
                ItemStack block = LongJump.this.mc.thePlayer.getCurrentEquippedItem();
                if (block != null) {
                    block = null;
                }
                int slot = LongJump.this.mc.thePlayer.inventory.currentItem;
                for (int g = 0; g < 9; g = (int)((short)(g + 1))) {
                    if (!LongJump.this.mc.thePlayer.inventoryContainer.getSlot(g + 36).getHasStack() || !(LongJump.this.mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem() instanceof ItemBow) || LongJump.this.mc.thePlayer.inventoryContainer.getSlot((int)(g + 36)).getStack().stackSize == 0 || block != null && !(block.getItem() instanceof ItemBow)) continue;
                    slot = g;
                    block = LongJump.this.mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack();
                }
                LongJump.this.mc.gameSettings.keyBindBack.pressed = false;
                LongJump.this.mc.gameSettings.keyBindForward.pressed = false;
                LongJump.this.mc.gameSettings.keyBindRight.pressed = false;
                LongJump.this.mc.gameSettings.keyBindLeft.pressed = false;
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(slot));
                try {
                    Thread.sleep(20L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(LongJump.this.mc.thePlayer.rotationYaw, -90.0f, LongJump.this.mc.thePlayer.onGround));
                PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, null, 0.0f, 0.0f, 0.0f));
                try {
                    Thread.sleep(200L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(LongJump.this.mc.thePlayer.rotationYaw, -90.0f, true));
                if (block.getItem() != null && !(block.getItem() instanceof ItemFishingRod)) {
                    PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, 3, -1), EnumFacing.UP));
                }
                try {
                    Thread.sleep(200L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(oldSlot));
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(LongJump.this.mc.thePlayer.rotationYaw, LongJump.this.mc.thePlayer.rotationPitch, true));
                LongJump.this.doneBow = true;
                LongJump.this.mc.gameSettings.keyBindBack.pressed = false;
                LongJump.this.mc.gameSettings.keyBindForward.pressed = false;
                LongJump.this.mc.gameSettings.keyBindRight.pressed = false;
                LongJump.this.mc.gameSettings.keyBindLeft.pressed = false;
            }
        };
        thread.start();
    }

    public boolean hasBow() {
        for (int g = 0; g < 9; ++g) {
            if (!this.mc.thePlayer.inventoryContainer.getSlot(g + 36).getHasStack() || !(this.mc.thePlayer.inventoryContainer.getSlot(g + 36).getStack().getItem() instanceof ItemBow) || this.mc.thePlayer.inventoryContainer.getSlot((int)(g + 36)).getStack().stackSize == 0) continue;
            return true;
        }
        return false;
    }

    static enum HypixelMode {
        BOW,
        NO_DMG;

    }

    static enum Mode {
        WATCHDOG,
        NCP,
        FUNCRAFT,
        VULCAN_BOAT,
        VERUS;

    }
}

