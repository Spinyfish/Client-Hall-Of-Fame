package net.minecraft.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.*;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import viamcp.ViaMCP;
import wtf.automn.Automn;
import wtf.automn.events.EventManager;
import wtf.automn.events.impl.player.EventMotion;
import wtf.automn.events.impl.player.EventPlayerUpdate;
import wtf.automn.events.impl.player.EventSilentMove;
import wtf.automn.utils.math.RotationUtil;

public class EntityPlayerSP extends AbstractClientPlayer {
    public final NetHandlerPlayClient sendQueue;
    private final StatFileWriter statWriter;

    public boolean sprintReset = false;

    /**
     * The last X position which was transmitted to the server, used to determine when the X position changes and needs
     * to be re-trasmitted
     */
    private double lastReportedPosX;

    /**
     * The last Y position which was transmitted to the server, used to determine when the Y position changes and needs
     * to be re-transmitted
     */
    private double lastReportedPosY;

    /**
     * The last Z position which was transmitted to the server, used to determine when the Z position changes and needs
     * to be re-transmitted
     */
    private double lastReportedPosZ;

    /**
     * The last yaw value which was transmitted to the server, used to determine when the yaw changes and needs to be
     * re-transmitted
     */
    private float lastReportedYaw;

    /**
     * The last pitch value which was transmitted to the server, used to determine when the pitch changes and needs to
     * be re-transmitted
     */
    private float lastReportedPitch;

    /**
     * the last sneaking state sent to the server
     */
    private boolean serverSneakState;

    /**
     * the last sprinting state sent to the server
     */
    private boolean serverSprintState;

    /**
     * Reset to 0 every time position is sent to the server, used to send periodic updates every 20 ticks even when the
     * player is not moving.
     */
    private int positionUpdateTicks;
    private boolean hasValidHealth;
    private String clientBrand;
    public MovementInput movementInput;
    protected Minecraft mc;

    /**
     * Used to tell if the player pressed forward twice. If this is at 0 and it's pressed (And they are allowed to
     * sprint, aka enough food on the ground etc) it sets this to 7. If it's pressed and it's greater than 0 enable
     * sprinting.
     */
    protected int sprintToggleTimer;

    /**
     * Ticks left before sprinting is disabled.
     */
    public int sprintingTicksLeft;
    public float renderArmYaw;
    public float renderArmPitch;
    public float prevRenderArmYaw;
    public float prevRenderArmPitch;
    private int horseJumpPowerCounter;
    private float horseJumpPower;

    /**
     * The amount of time an entity has been in a Portal
     */
    public float timeInPortal;

    /**
     * The amount of time an entity has been in a Portal the previous tick
     */
    public float prevTimeInPortal;

    public EntityPlayerSP(final Minecraft mcIn, final World worldIn, final NetHandlerPlayClient netHandler, final StatFileWriter statFile) {
        super(worldIn, netHandler.getGameProfile());
        this.sendQueue = netHandler;
        this.statWriter = statFile;
        this.mc = mcIn;
        this.dimension = 0;
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(final DamageSource source, final float amount) {
        return false;
    }

    /**
     * Heal living entity (param: amount of half-hearts)
     */
    @Override
    public void heal(final float healAmount) {
    }

    /**
     * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
     */
    @Override
    public void mountEntity(final Entity entityIn) {
        super.mountEntity(entityIn);

        if (entityIn instanceof EntityMinecart)
            this.mc.getSoundHandler().playSound(new MovingSoundMinecartRiding(this, (EntityMinecart) entityIn));
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {
        EventManager.call(new EventPlayerUpdate());
        if (this.worldObj.isBlockLoaded(new BlockPos(this.posX, 0.0D, this.posZ))) {
            super.onUpdate();

            if (this.isRiding()) {
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
                this.sendQueue.addToSendQueue(new C0CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
            } else this.onUpdateWalkingPlayer();
        }
    }

    /**
     * called every tick when the player is on foot. Performs all the things that normally happen during movement.
     */
    public void onUpdateWalkingPlayer() {
        final boolean flag = this.isSprinting();

        if (flag != this.serverSprintState) {
            if (flag)
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SPRINTING));
            else
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SPRINTING));

            this.sprintReset = false;
            this.serverSprintState = flag;
        }

        final boolean flag1 = this.isSneaking();

        if (flag1 != this.serverSneakState) {
            if (flag1)
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SNEAKING));
            else
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SNEAKING));

            this.serverSneakState = flag1;
        }

        if (this.isCurrentViewEntity()) {
            final var motionEvent = new EventMotion(this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround);
            EventManager.call(motionEvent);
            final double d0 = motionEvent.x() - this.lastReportedPosX;
            final double d1 = motionEvent.y() - this.lastReportedPosY;
            final double d2 = motionEvent.z() - this.lastReportedPosZ;
            final double d3 = motionEvent.yaw() - this.lastReportedYaw;
            final double d4 = motionEvent.pitch() - this.lastReportedPitch;
            boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || this.positionUpdateTicks >= 20;
            final boolean flag3 = d3 != 0.0D || d4 != 0.0D;

            if (this.ridingEntity == null) if (flag2 && flag3)
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(motionEvent.x(), motionEvent.y(), motionEvent.z(), motionEvent.yaw(), motionEvent.pitch(), motionEvent.onGround()));
            else if (flag2)
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(motionEvent.x(), motionEvent.y(), motionEvent.z(), motionEvent.onGround()));
            else if (flag3)
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(motionEvent.yaw(), motionEvent.pitch(), motionEvent.onGround()));
            else
                this.sendQueue.addToSendQueue(new C03PacketPlayer(motionEvent.onGround()));
            else {
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0D, this.motionZ, motionEvent.yaw(), motionEvent.pitch(), motionEvent.onGround()));
                flag2 = false;
            }

            ++this.positionUpdateTicks;

            if (flag2) {
                this.lastReportedPosX = motionEvent.x();
                this.lastReportedPosY = motionEvent.y();
                this.lastReportedPosZ = motionEvent.z();
                this.positionUpdateTicks = 0;
            }

            if (flag3) {
                this.lastReportedYaw = motionEvent.yaw();
                this.lastReportedPitch = motionEvent.pitch();
            }
        }
    }

    /**
     * Called when player presses the drop item key
     */
    @Override
    public EntityItem dropOneItem(final boolean dropAll) {
        final C07PacketPlayerDigging.Action c07packetplayerdigging$action = dropAll ? C07PacketPlayerDigging.Action.DROP_ALL_ITEMS : C07PacketPlayerDigging.Action.DROP_ITEM;
        this.sendQueue.addToSendQueue(new C07PacketPlayerDigging(c07packetplayerdigging$action, BlockPos.ORIGIN, EnumFacing.DOWN));
        return null;
    }

    /**
     * Joins the passed in entity item with the world. Args: entityItem
     */
    @Override
    protected void joinEntityItemWithWorld(final EntityItem itemIn) {
    }

    /**
     * Sends a chat message from the player. Args: chatMessage
     */
    public void sendChatMessage(final String message) {
        if (Automn.instance().commandManager().execute(message)
                || message.startsWith(Automn.instance().commandManager().CHAT_PREFIX))
            return;
        this.sendQueue.addToSendQueue(new C01PacketChatMessage(message));
    }

    /**
     * Swings the item the player is holding.
     */
    @Override
    public void swingItem() {
        super.swingItem();
        if (ViaMCP.getInstance().getVersion() == 47) {
            this.sendQueue.addToSendQueue(new C0APacketAnimation());
        } else {
            this.sendQueue.addToSendQueue(new CAnimateHandPacket(Hand.MAIN_HAND));
        }
    }

    @Override
    public void respawnPlayer() {
        this.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
    }

    /**
     * Deals damage to the entity. If its a EntityPlayer then will take damage from the armor first and then health
     * second with the reduced value. Args: damageAmount
     */
    @Override
    protected void damageEntity(final DamageSource damageSrc, final float damageAmount) {
        if (!this.isEntityInvulnerable(damageSrc)) this.setHealth(this.getHealth() - damageAmount);
    }

    /**
     * set current crafting inventory back to the 2x2 square
     */
    @Override
    public void closeScreen() {
        this.sendQueue.addToSendQueue(new C0DPacketCloseWindow(this.openContainer.windowId));
        this.closeScreenAndDropStack();
    }

    public void closeScreenAndDropStack() {
        this.inventory.setItemStack(null);
        super.closeScreen();
        this.mc.displayGuiScreen(null);
    }

    /**
     * Updates health locally.
     */
    public void setPlayerSPHealth(final float health) {
        if (this.hasValidHealth) {
            final float f = this.getHealth() - health;

            if (f <= 0.0F) {
                this.setHealth(health);

                if (f < 0.0F) this.hurtResistantTime = this.maxHurtResistantTime / 2;
            } else {
                this.lastDamage = f;
                this.setHealth(this.getHealth());
                this.hurtResistantTime = this.maxHurtResistantTime;
                this.damageEntity(DamageSource.generic, f);
                this.hurtTime = this.maxHurtTime = 10;
            }
        } else {
            this.setHealth(health);
            this.hasValidHealth = true;
        }
    }

    /**
     * Adds a value to a statistic field.
     */
    @Override
    public void addStat(final StatBase stat, final int amount) {
        if (stat != null) if (stat.isIndependent) super.addStat(stat, amount);
    }

    /**
     * Sends the player's abilities to the server (if there is one).
     */
    @Override
    public void sendPlayerAbilities() {
        this.sendQueue.addToSendQueue(new C13PacketPlayerAbilities(this.capabilities));
    }

    /**
     * returns true if this is an EntityPlayerSP, or the logged in player.
     */
    @Override
    public boolean isUser() {
        return true;
    }

    protected void sendHorseJump() {
        this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.RIDING_JUMP, (int) (this.getHorseJumpPower() * 100.0F)));
    }

    public void sendHorseInventory() {
        this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.OPEN_INVENTORY));
    }

    public void setClientBrand(final String brand) {
        this.clientBrand = brand;
    }

    public String getClientBrand() {
        return this.clientBrand;
    }

    public StatFileWriter getStatFileWriter() {
        return this.statWriter;
    }

    @Override
    public void addChatComponentMessage(final IChatComponent chatComponent) {
        this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponent);
    }

    @Override
    protected boolean pushOutOfBlocks(final double x, final double y, final double z) {
        if (this.noClip) return false;
        else {
            final BlockPos blockpos = new BlockPos(x, y, z);
            final double d0 = x - (double) blockpos.getX();
            final double d1 = z - (double) blockpos.getZ();

            if (!this.isOpenBlockSpace(blockpos)) {
                int i = -1;
                double d2 = 9999.0D;

                if (this.isOpenBlockSpace(blockpos.west()) && d0 < d2) {
                    d2 = d0;
                    i = 0;
                }

                if (this.isOpenBlockSpace(blockpos.east()) && 1.0D - d0 < d2) {
                    d2 = 1.0D - d0;
                    i = 1;
                }

                if (this.isOpenBlockSpace(blockpos.north()) && d1 < d2) {
                    d2 = d1;
                    i = 4;
                }

                if (this.isOpenBlockSpace(blockpos.south()) && 1.0D - d1 < d2) {
                    d2 = 1.0D - d1;
                    i = 5;
                }

                final float f = 0.1F;

                if (i == 0) this.motionX = -f;

                if (i == 1) this.motionX = f;

                if (i == 4) this.motionZ = -f;

                if (i == 5) this.motionZ = f;
            }

            return false;
        }
    }

    /**
     * Returns true if the block at the given BlockPos and the block above it are NOT full cubes.
     */
    private boolean isOpenBlockSpace(final BlockPos pos) {
        return !this.worldObj.getBlockState(pos).getBlock().isNormalCube() && !this.worldObj.getBlockState(pos.up()).getBlock().isNormalCube();
    }

    /**
     * Set sprinting switch for Entity.
     */
    @Override
    public void setSprinting(final boolean sprinting) {
        super.setSprinting(sprinting);
        this.sprintingTicksLeft = sprinting ? 600 : 0;
    }

    /**
     * Sets the current XP, total XP, and level number.
     */
    public void setXPStats(final float currentXP, final int maxXP, final int level) {
        this.experience = currentXP;
        this.experienceTotal = maxXP;
        this.experienceLevel = level;
    }

    /**
     * Send a chat message to the CommandSender
     */
    @Override
    public void addChatMessage(final IChatComponent component) {
        this.mc.ingameGUI.getChatGUI().printChatMessage(component);
    }

    /**
     * Returns {@code true} if the CommandSender is allowed to execute the command, {@code false} if not
     */
    @Override
    public boolean canCommandSenderUseCommand(final int permLevel, final String commandName) {
        return permLevel <= 0;
    }

    /**
     * Get the position in the world. <b>{@code null} is not allowed!</b> If you are not an entity in the world, return
     * the coordinates 0, 0, 0
     */
    @Override
    public BlockPos getPosition() {
        return new BlockPos(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D);
    }

    @Override
    public void playSound(final String name, final float volume, final float pitch) {
        this.worldObj.playSound(this.posX, this.posY, this.posZ, name, volume, pitch, false);
    }

    /**
     * Returns whether the entity is in a server world
     */
    @Override
    public boolean isServerWorld() {
        return true;
    }

    public boolean isRidingHorse() {
        return this.ridingEntity != null && this.ridingEntity instanceof EntityHorse && ((EntityHorse) this.ridingEntity).isHorseSaddled();
    }

    public float getHorseJumpPower() {
        return this.horseJumpPower;
    }

    @Override
    public void openEditSign(final TileEntitySign signTile) {
        this.mc.displayGuiScreen(new GuiEditSign(signTile));
    }

    @Override
    public void openEditCommandBlock(final CommandBlockLogic cmdBlockLogic) {
        this.mc.displayGuiScreen(new GuiCommandBlock(cmdBlockLogic));
    }

    /**
     * Displays the GUI for interacting with a book.
     */
    @Override
    public void displayGUIBook(final ItemStack bookStack) {
        final Item item = bookStack.getItem();

        if (item == Items.writable_book) this.mc.displayGuiScreen(new GuiScreenBook(this, bookStack, true));
    }

    /**
     * Displays the GUI for interacting with a chest inventory. Args: chestInventory
     */
    @Override
    public void displayGUIChest(final IInventory chestInventory) {
        final String s = chestInventory instanceof IInteractionObject ? ((IInteractionObject) chestInventory).getGuiID() : "minecraft:container";

        if ("minecraft:chest".equals(s)) this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
        else if ("minecraft:hopper".equals(s))
            this.mc.displayGuiScreen(new GuiHopper(this.inventory, chestInventory));
        else if ("minecraft:furnace".equals(s))
            this.mc.displayGuiScreen(new GuiFurnace(this.inventory, chestInventory));
        else if ("minecraft:brewing_stand".equals(s))
            this.mc.displayGuiScreen(new GuiBrewingStand(this.inventory, chestInventory));
        else if ("minecraft:beacon".equals(s))
            this.mc.displayGuiScreen(new GuiBeacon(this.inventory, chestInventory));
        else if (!"minecraft:dispenser".equals(s) && !"minecraft:dropper".equals(s))
            this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
        else
            this.mc.displayGuiScreen(new GuiDispenser(this.inventory, chestInventory));
    }

    @Override
    public void displayGUIHorse(final EntityHorse horse, final IInventory horseInventory) {
        this.mc.displayGuiScreen(new GuiScreenHorseInventory(this.inventory, horseInventory, horse));
    }

    @Override
    public void displayGui(final IInteractionObject guiOwner) {
        final String s = guiOwner.getGuiID();

        if ("minecraft:crafting_table".equals(s))
            this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj));
        else if ("minecraft:enchanting_table".equals(s))
            this.mc.displayGuiScreen(new GuiEnchantment(this.inventory, this.worldObj, guiOwner));
        else if ("minecraft:anvil".equals(s)) this.mc.displayGuiScreen(new GuiRepair(this.inventory, this.worldObj));
    }

    @Override
    public void displayVillagerTradeGui(final IMerchant villager) {
        this.mc.displayGuiScreen(new GuiMerchant(this.inventory, villager, this.worldObj));
    }

    /**
     * Called when the player performs a critical hit on the Entity. Args: entity that was hit critically
     */
    @Override
    public void onCriticalHit(final Entity entityHit) {
        this.mc.effectRenderer.emitParticleAtEntity(entityHit, EnumParticleTypes.CRIT);
    }

    @Override
    public void onEnchantmentCritical(final Entity entityHit) {
        this.mc.effectRenderer.emitParticleAtEntity(entityHit, EnumParticleTypes.CRIT_MAGIC);
    }

    /**
     * Returns if this entity is sneaking.
     */
    @Override
    public boolean isSneaking() {
        final boolean flag = this.movementInput != null && this.movementInput.sneak;
        return flag && !this.sleeping;
    }

    @Override
    public void updateEntityActionState() {
        super.updateEntityActionState();

        if (this.isCurrentViewEntity()) {
            /**
             * fixing Hand
             */
            this.moveStrafing = this.movementInput.moveStrafe;
            this.moveForward = this.movementInput.moveForward;
            this.isJumping = this.movementInput.jump;
            this.prevRenderArmYaw = this.renderArmYaw;
            this.prevRenderArmPitch = this.renderArmPitch;
            this.renderArmPitch = (float) ((double) this.renderArmPitch + (double) (Automn.instance().yawPitchHelper().realPitch - this.renderArmPitch) * 0.5D);
            this.renderArmYaw = (float) ((double) this.renderArmYaw + (double) (Automn.instance().yawPitchHelper().realYaw - this.renderArmYaw) * 0.5D);


            /*
            this.moveStrafing = this.movementInput.moveStrafe;
            this.moveForward = this.movementInput.moveForward;
            this.isJumping = this.movementInput.jump;
            this.prevRenderArmYaw = this.renderArmYaw;
            this.prevRenderArmPitch = this.renderArmPitch;
            this.renderArmPitch = (float) ((double) this.renderArmPitch + (double) (this.rotationPitch - this.renderArmPitch) * 0.5D);
            this.renderArmYaw = (float) ((double) this.renderArmYaw + (double) (this.rotationYaw - this.renderArmYaw) * 0.5D);

             */
        }
    }

    protected boolean isCurrentViewEntity() {
        return this.mc.getRenderViewEntity() == this;
    }

    /**
     * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
     * use this to react to sunlight and start to burn.
     */
    @Override
    public void onLivingUpdate() {
        if (this.sprintingTicksLeft > 0) {
            --this.sprintingTicksLeft;

            if (this.sprintingTicksLeft == 0) this.setSprinting(false);
        }

        if (this.sprintToggleTimer > 0) --this.sprintToggleTimer;

        this.prevTimeInPortal = this.timeInPortal;

        if (this.inPortal) {
            if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame())
                this.mc.displayGuiScreen(null);

            if (this.timeInPortal == 0.0F)
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), this.rand.nextFloat() * 0.4F + 0.8F));

            this.timeInPortal += 0.0125F;

            if (this.timeInPortal >= 1.0F) this.timeInPortal = 1.0F;

            this.inPortal = false;
        } else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60) {
            this.timeInPortal += 0.006666667F;

            if (this.timeInPortal > 1.0F) this.timeInPortal = 1.0F;
        } else {
            if (this.timeInPortal > 0.0F) this.timeInPortal -= 0.05F;

            if (this.timeInPortal < 0.0F) this.timeInPortal = 0.0F;
        }

        if (this.timeUntilPortal > 0) --this.timeUntilPortal;

        final boolean flag = this.movementInput.jump;
        final boolean flag1 = this.movementInput.sneak;
        final float f = 0.8F;
        final boolean flag2 = this.movementInput.moveForward >= f;
        float forward = movementInput.moveForward;
        float strafe = movementInput.moveStrafe;

        this.movementInput.updatePlayerMoveState();

        EventSilentMove eventSilentMove = new EventSilentMove(Automn.instance().yawPitchHelper().realYaw);
        EventManager.call(eventSilentMove);
        if (eventSilentMove.isSilent()) {
            float[] floats = eventSilentMove.moveSilent(this.movementInput.moveStrafe, this.movementInput.moveForward);
            float diffForward = forward - floats[1];
            float diffStrafe = strafe - floats[0];
            if (this.movementInput.sneak){
                this.movementInput.moveStrafe = MathHelper.clamp_float(floats[0], -0.3f, 0.3f);
                this.movementInput.moveForward = MathHelper.clamp_float(floats[1], -0.3f, 0.3f);
            } else {
                if (diffForward >= 2) {
                    floats[1] = 0;
                }
                if (diffForward <= -2) {
                    floats[1] = 0;
                }
                if (diffStrafe >= 2) {
                    floats[0] = 0;
                }
                if (diffStrafe <= -2) {
                    floats[0] = 0;
                }
                this.movementInput.moveStrafe = MathHelper.clamp_float(floats[0], -1f, 1f);
                this.movementInput.moveForward = MathHelper.clamp_float(floats[1], -1f, 1f);
            }
        }


        if (this.isUsingItem() && !this.isRiding())
        /**
         * adding Noslow
         */
            if (Automn.instance().moduleManager().noSlowDown.enabled()) {
                this.movementInput.moveStrafe *= 1F;
                this.movementInput.moveForward *= 1F;
                this.sprintToggleTimer = 0;
            } else {
                this.movementInput.moveStrafe *= 0.2F;
                this.movementInput.moveForward *= 0.2F;
                this.sprintToggleTimer = 0;
            }

        this.pushOutOfBlocks(this.posX - (double) this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ + (double) this.width * 0.35D);
        this.pushOutOfBlocks(this.posX - (double) this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ - (double) this.width * 0.35D);
        this.pushOutOfBlocks(this.posX + (double) this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ - (double) this.width * 0.35D);
        this.pushOutOfBlocks(this.posX + (double) this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ + (double) this.width * 0.35D);
        final boolean flag3 = (float) this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.allowFlying;

        float movef = this.movementInput.moveForward;
        if (sprintReset) {
            this.movementInput.moveForward = 0;
        }

        if (this.onGround && !flag1 && !flag2 && this.movementInput.moveForward >= f && !this.isSprinting() && flag3 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness))
            if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown())
                this.sprintToggleTimer = 7;
            else this.setSprinting(true);

        if (!this.isSprinting() && this.movementInput.moveForward >= f && flag3 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness) && this.mc.gameSettings.keyBindSprint.isKeyDown())
            this.setSprinting(true);

        if (this.isSprinting() && (this.movementInput.moveForward < f || this.isCollidedHorizontally || !flag3))
            this.setSprinting(false);

        if (sprintReset) {
            this.movementInput.moveForward = movef;
            sprintReset = false;
        }
        if (this.capabilities.allowFlying) if (this.mc.playerController.isSpectatorMode()) {
            if (!this.capabilities.isFlying) {
                this.capabilities.isFlying = true;
                this.sendPlayerAbilities();
            }
        } else if (!flag && this.movementInput.jump) if (this.flyToggleTimer == 0) this.flyToggleTimer = 7;
        else {
            this.capabilities.isFlying = !this.capabilities.isFlying;
            this.sendPlayerAbilities();
            this.flyToggleTimer = 0;
        }

        if (this.capabilities.isFlying && this.isCurrentViewEntity()) {
            if (this.movementInput.sneak)
                this.motionY -= this.capabilities.getFlySpeed() * 3.0F;

            if (this.movementInput.jump)
                this.motionY += this.capabilities.getFlySpeed() * 3.0F;
        }

        if (this.isRidingHorse()) {
            if (this.horseJumpPowerCounter < 0) {
                ++this.horseJumpPowerCounter;

                if (this.horseJumpPowerCounter == 0) this.horseJumpPower = 0.0F;
            }

            if (flag && !this.movementInput.jump) {
                this.horseJumpPowerCounter = -10;
                this.sendHorseJump();
            } else if (!flag && this.movementInput.jump) {
                this.horseJumpPowerCounter = 0;
                this.horseJumpPower = 0.0F;
            } else if (flag) {
                ++this.horseJumpPowerCounter;

                if (this.horseJumpPowerCounter < 10) this.horseJumpPower = (float) this.horseJumpPowerCounter * 0.1F;
                else
                    this.horseJumpPower = 0.8F + 2.0F / (float) (this.horseJumpPowerCounter - 9) * 0.1F;
            }
        } else this.horseJumpPower = 0.0F;

        super.onLivingUpdate();

        if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
            this.capabilities.isFlying = false;
            this.sendPlayerAbilities();
        }
    }

    public float getBestDistanceToEntity(Entity entityIn)
    {
        Vec3 vec3 = RotationUtil.getBestHitVec(entityIn);
        Vec3 posEyes = new Vec3(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
        return (float) posEyes.distanceTo(vec3);
    }
}
