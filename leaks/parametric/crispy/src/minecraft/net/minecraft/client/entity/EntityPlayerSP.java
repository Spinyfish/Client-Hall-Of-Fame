package net.minecraft.client.entity;

import crispy.Crispy;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.hacks.impl.combat.Aura;
import crispy.features.hacks.impl.movement.NoSlowDown;
import crispy.features.hacks.impl.movement.Scaffold;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
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
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.*;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public class EntityPlayerSP extends AbstractClientPlayer {
	public final NetHandlerPlayClient sendQueue;
	private final StatFileWriter statWriter;

	public static float PostPitch;
	public static float PostYaw;
	public static float PreYaw;
	public static float PrePitch;
	/**
	 * The last X position which was transmitted to the server, used to
	 * determine when the X position changes and needs to be re-trasmitted
	 */
	private double lastReportedPosX;

	/**
	 * The last Y position which was transmitted to the server, used to
	 * determine when the Y position changes and needs to be re-transmitted
	 */
	private double lastReportedPosY;

	/**
	 * The last Z position which was transmitted to the server, used to
	 * determine when the Z position changes and needs to be re-transmitted
	 */
	private double lastReportedPosZ;

	/**
	 * The last yaw value which was transmitted to the server, used to determine
	 * when the yaw changes and needs to be re-transmitted
	 */
	private float lastReportedYaw;

	/**
	 * The last pitch value which was transmitted to the server, used to
	 * determine when the pitch changes and needs to be re-transmitted
	 */
	private float lastReportedPitch;

	/** the last sneaking state sent to the server */
	private boolean serverSneakState;

	/** the last sprinting state sent to the server */
	private boolean serverSprintState;

	/**
	 * Reset to 0 every time position is sent to the server, used to send
	 * periodic updates every 20 ticks even when the player is not moving.
	 */
	private int positionUpdateTicks;
	private boolean hasValidHealth;
	private String clientBrand;
	public MovementInput movementInput;
	protected Minecraft mc;

	/**
	 * Used to tell if the player pressed forward twice. If this is at 0 and
	 * it's pressed (And they are allowed to sprint, aka enough food on the
	 * ground etc) it sets this to 7. If it's pressed and it's greater than 0
	 * enable sprinting.
	 */
	protected int sprintToggleTimer;

	/** Ticks left before sprinting is disabled. */
	public int sprintingTicksLeft;
	public float renderArmYaw;
	public float renderArmPitch;
	public float prevRenderArmYaw;
	public float prevRenderArmPitch;
	private int horseJumpPowerCounter;
	private float horseJumpPower;

	/** The amount of time an entity has been in a Portal */
	public float timeInPortal;

	/** The amount of time an entity has been in a Portal the previous tick */
	public float prevTimeInPortal;



	public EntityPlayerSP(Minecraft mcIn, World worldIn, NetHandlerPlayClient netHandler, StatFileWriter statFile) {
		super(worldIn, netHandler.getGameProfile());
		this.sendQueue = netHandler;
		this.statWriter = statFile;
		this.mc = mcIn;
		this.dimension = 0;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return false;
	}

	/**
	 * Heal living entity (param: amount of half-hearts)
	 */
	public void heal(float healAmount) {
	}

	/**
	 * Called when a player mounts an entity. e.g. mounts a pig, mounts a boat.
	 */
	public void mountEntity(Entity entityIn) {
		super.mountEntity(entityIn);

		if (entityIn instanceof EntityMinecart) {
			this.mc.getSoundHandler().playSound(new MovingSoundMinecartRiding(this, (EntityMinecart) entityIn));
		}
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate()
    {
        if (this.worldObj.isBlockLoaded(new BlockPos(this.posX, 0.0D, this.posZ)))
        {
            super.onUpdate();

            if (this.isRiding())
            {
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
                this.sendQueue.addToSendQueue(new C0CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
            }
            else
            {
                this.onUpdateWalkingPlayer();
				EventUpdate eventPreMotionUpdate = new EventUpdate(this.posX,  mc.thePlayer.posY, this.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, this.onGround, false, this.movementInput.sneak);
				PostPitch = eventPreMotionUpdate.getPitch();
				PostYaw = eventPreMotionUpdate.getYaw();
				if(Aura.aura) {
					eventPreMotionUpdate.setYaw(Aura.targetRotation.getYaw());
					eventPreMotionUpdate.setPitch(Aura.targetRotation.getPitch());
				}
				if(Scaffold.scaffoldRot.getYaw() != 999 && Scaffold.scaffoldRot.getPitch() != 999) {
					eventPreMotionUpdate.setYaw(Scaffold.scaffoldRot.getYaw());
					eventPreMotionUpdate.setPitch(Scaffold.scaffoldRot.getPitch());
				}
				Crispy.INSTANCE.onEvent(eventPreMotionUpdate);
            }
        }
    }

	/**
	 * called every tick when the player is on foot. Performs all the things that normally happen during movement.
	 */
	public void onUpdateWalkingPlayer()
	{
		EventUpdate eventPreMotionUpdate = new EventUpdate(this.posX, mc.thePlayer.posY, this.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch,this.onGround, true, this.movementInput.sneak);
		PrePitch = PostPitch;
		PreYaw = PostYaw;
		if(Aura.aura) {
			eventPreMotionUpdate.setYaw(Aura.targetRotation.getYaw());
			eventPreMotionUpdate.setPitch(Aura.targetRotation.getPitch());

		}
		if(Scaffold.scaffoldRot.getYaw() != 999 && Scaffold.scaffoldRot.getPitch() != 999) {
			eventPreMotionUpdate.setYaw(Scaffold.scaffoldRot.getYaw());
			eventPreMotionUpdate.setPitch(Scaffold.scaffoldRot.getPitch());
		}

		Crispy.INSTANCE.onEvent(eventPreMotionUpdate);
		boolean flag = this.isSprinting();


		if (flag != this.serverSprintState)
		{
			if (flag)
			{
				this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SPRINTING));
			}
			else
			{
				this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SPRINTING));
			}

			this.serverSprintState = flag;
		}

		boolean flag1 = eventPreMotionUpdate.sneak;

		if (flag1 != this.serverSneakState)
		{
			if (flag1)
			{
				this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SNEAKING));
			}
			else
			{
				this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SNEAKING));
			}

			this.serverSneakState = flag1;
		}

		if (this.isCurrentViewEntity())
		{
			double d0 = this.posX - this.lastReportedPosX;
			double d1 =  eventPreMotionUpdate.getY() - this.lastReportedPosY;
			double d2 = this.posZ - this.lastReportedPosZ;
			double d3 = (double)(eventPreMotionUpdate.getYaw() - this.lastReportedYaw);
			double d4 = (double)(eventPreMotionUpdate.getPitch() - this.lastReportedPitch);
			boolean flag2 = d0 * d0 + d1 * d1 + d2 * d2 > 9.0E-4D || this.positionUpdateTicks >= 20;
			boolean flag3 = d3 != 0.0D || d4 != 0.0D;

			if (this.ridingEntity == null)
			{
				if (flag2 && flag3)
				{
					this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(this.posX, this.getEntityBoundingBox().minY, this.posZ, eventPreMotionUpdate.getYaw(), eventPreMotionUpdate.getPitch(), eventPreMotionUpdate.ground()));
				}
				else if (flag2)
				{
					this.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.posX, this.getEntityBoundingBox().minY, this.posZ, eventPreMotionUpdate.ground()));

				}
				else if (flag3)
				{
					this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(eventPreMotionUpdate.getYaw(), eventPreMotionUpdate.getPitch(), eventPreMotionUpdate.ground()));

				}
				else
				{
					this.sendQueue.addToSendQueue(new C03PacketPlayer(eventPreMotionUpdate.ground));
				}
			}
			else
			{
				this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0D, this.motionZ, this.rotationYaw, this.rotationPitch, eventPreMotionUpdate.ground()));
				flag2 = false;
			}

			++this.positionUpdateTicks;

			if (flag2)
			{
				this.lastReportedPosX = this.posX;
				this.lastReportedPosY =  this.getEntityBoundingBox().minY;
				this.lastReportedPosZ = this.posZ;
				this.positionUpdateTicks = 0;
			}

			if (flag3)
			{
				this.lastReportedYaw = eventPreMotionUpdate.getYaw();
				this.lastReportedPitch = eventPreMotionUpdate.getPitch();
			}
		}

	}

	/**
	 * Called when player presses the drop item key
	 */
	public EntityItem dropOneItem(boolean dropAll) {
		C07PacketPlayerDigging.Action var2 = dropAll ? C07PacketPlayerDigging.Action.DROP_ALL_ITEMS
				: C07PacketPlayerDigging.Action.DROP_ITEM;
		this.sendQueue.addToSendQueue(new C07PacketPlayerDigging(var2, BlockPos.ORIGIN, EnumFacing.DOWN));
		return null;
	}

	/**
	 * Joins the passed in entity item with the world. Args: entityItem
	 */
	protected void joinEntityItemWithWorld(EntityItem itemIn) {
	}

	/**
	 * Sends a chat message from the player. Args: chatMessage
	 *
	 * @param message
	 *            used on EntityPlayerSP.sendChatMessage - as inbound message
	 */
	public void sendChatMessage(String message)
    {
    	if(Crispy.onSendChatMessage(message)) {
			this.sendQueue.addToSendNoEvent(new C01PacketChatMessage(message));
		}
    }


	/**
	 * Swings the item the player is holding.
	 */
	public void swingItem() {
		super.swingItem();
		this.sendQueue.addToSendQueue(new C0APacketAnimation());
	}

	public void respawnPlayer() {
		this.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
	}

	/**
	 * Deals damage to the entity. If its a EntityPlayer then will take damage
	 * from the armor first and then health second with the reduced value. Args:
	 * damageAmount
	 */
	protected void damageEntity(DamageSource damageSrc, float damageAmount) {
		if (!this.isEntityInvulnerable(damageSrc)) {
			this.setHealth(this.getHealth() - damageAmount);
		}
	}

	/**
	 * set current crafting inventory back to the 2x2 square
	 */
	public void closeScreen() {
		this.sendQueue.addToSendQueue(new C0DPacketCloseWindow(this.openContainer.windowId));
		this.closeScreenAndDropStack();
	}

	public void closeScreenAndDropStack() {
		this.inventory.setItemStack((ItemStack) null);
		super.closeScreen();
		this.mc.displayGuiScreen((GuiScreen) null);
	}

	/**
	 * Updates health locally.
	 */
	public void setPlayerSPHealth(float health) {
		if (this.hasValidHealth) {
			float var2 = this.getHealth() - health;

			if (var2 <= 0.0F) {
				this.setHealth(health);

				if (var2 < 0.0F) {
					this.hurtResistantTime = this.maxHurtResistantTime / 2;
				}
			} else {
				this.lastDamage = var2;
				this.setHealth(this.getHealth());
				this.hurtResistantTime = this.maxHurtResistantTime;
				this.damageEntity(DamageSource.generic, var2);
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
	public void addStat(StatBase stat, int amount) {
		if (stat != null) {
			if (stat.isIndependent) {
				super.addStat(stat, amount);
			}
		}
	}

	/**
	 * Sends the player's abilities to the server (if there is one).
	 */
	public void sendPlayerAbilities() {
		this.sendQueue.addToSendQueue(new C13PacketPlayerAbilities(this.capabilities));
	}

	/**
	 * returns true if this is an EntityPlayerSP, or the logged in player.
	 */
	public boolean isUser() {
		return true;
	}

	protected void sendHorseJump() {
		this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.RIDING_JUMP,
				(int) (this.getHorseJumpPower() * 100.0F)));
	}

	public void sendHorseInventory() {
		this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.OPEN_INVENTORY));
	}

	public void setClientBrand(String brand) {
		this.clientBrand = brand;
	}

	public String getClientBrand() {
		return this.clientBrand;
	}

	public StatFileWriter getStatFileWriter() {
		return this.statWriter;
	}

	public void addChatComponentMessage(IChatComponent chatComponent) {
		this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponent);
	}

	protected boolean pushOutOfBlocks(double x, double y, double z) {
		if (this.noClip) {
			return false;
		} else {
			BlockPos var7 = new BlockPos(x, y, z);
			double var8 = x - (double) var7.getX();
			double var10 = z - (double) var7.getZ();

			if (!this.isOpenBlockSpace(var7)) {
				byte var12 = -1;
				double var13 = 9999.0D;

				if (this.isOpenBlockSpace(var7.west()) && var8 < var13) {
					var13 = var8;
					var12 = 0;
				}

				if (this.isOpenBlockSpace(var7.east()) && 1.0D - var8 < var13) {
					var13 = 1.0D - var8;
					var12 = 1;
				}

				if (this.isOpenBlockSpace(var7.north()) && var10 < var13) {
					var13 = var10;
					var12 = 4;
				}

				if (this.isOpenBlockSpace(var7.south()) && 1.0D - var10 < var13) {
					var13 = 1.0D - var10;
					var12 = 5;
				}

				float var15 = 0.1F;

				if (var12 == 0) {
					this.motionX = (double) (-var15);
				}

				if (var12 == 1) {
					this.motionX = (double) var15;
				}

				if (var12 == 4) {
					this.motionZ = (double) (-var15);
				}

				if (var12 == 5) {
					this.motionZ = (double) var15;
				}
			}

			return false;
		}
	}

	/**
	 * Returns true if the block at the given BlockPos and the block above it
	 * are NOT full cubes.
	 */
	private boolean isOpenBlockSpace(BlockPos pos) {
		return !this.worldObj.getBlockState(pos).getBlock().isNormalCube()
				&& !this.worldObj.getBlockState(pos.up()).getBlock().isNormalCube();
	}

	/**
	 * Set sprinting switch for Entity.
	 */
	public void setSprinting(boolean sprinting)
    {
        super.setSprinting(sprinting);
        this.sprintingTicksLeft = sprinting ? 600 : 0;
    }

	/**
	 * Sets the current XP, total XP, and level number.
	 */
	public void setXPStats(float currentXP, int maxXP, int level) {
		this.experience = currentXP;
		this.experienceTotal = maxXP;
		this.experienceLevel = level;
	}

	/**
	 * Send a chat message to the CommandSender
	 *
	 * @param component
	 *            The ChatComponent to send
	 */
	public void addChatMessage(IChatComponent component) {
		this.mc.ingameGUI.getChatGUI().printChatMessage(component);
	}

	/**
	 * Returns {@code true} if the CommandSender is allowed to execute the
	 * command, {@code false} if not
	 *
	 * @param permLevel
	 *            The permission level required to execute the command
	 * @param commandName
	 *            The name of the command
	 */
	public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
		return permLevel <= 0;
	}

	/**
	 * Get the position in the world. <b>{@code null} is not allowed!</b> If you
	 * are not an entity in the world, return the coordinates 0, 0, 0
	 */
	public BlockPos getPosition() {
		return new BlockPos(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D);
	}

	public Vec3 getVec() {
		return new Vec3(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D);
	}
	public void playSound(String name, float volume, float pitch) {
		this.worldObj.playSound(this.posX, this.posY, this.posZ, name, volume, pitch, false);
	}

	/**
	 * Returns whether the entity is in a server world
	 */
	public boolean isServerWorld() {
		return true;
	}

	public boolean isRidingHorse() {
		return this.ridingEntity != null && this.ridingEntity instanceof EntityHorse
				&& ((EntityHorse) this.ridingEntity).isHorseSaddled();
	}

	public float getHorseJumpPower() {
		return this.horseJumpPower;
	}

	public void openEditSign(TileEntitySign signTile) {
		this.mc.displayGuiScreen(new GuiEditSign(signTile));
	}

	public void openEditCommandBlock(CommandBlockLogic cmdBlockLogic) {
		this.mc.displayGuiScreen(new GuiCommandBlock(cmdBlockLogic));
	}

	/**
	 * Displays the GUI for interacting with a book.
	 */
	public void displayGUIBook(ItemStack bookStack) {
		Item var2 = bookStack.getItem();

		if (var2 == Items.writable_book) {
			this.mc.displayGuiScreen(new GuiScreenBook(this, bookStack, true));
		}
	}

	/**
	 * Displays the GUI for interacting with a chest inventory. Args:
	 * chestInventory
	 */
	public void displayGUIChest(IInventory chestInventory) {
		String var2 = chestInventory instanceof IInteractionObject ? ((IInteractionObject) chestInventory).getGuiID()
				: "minecraft:container";

		if ("minecraft:chest".equals(var2)) {
			this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
		} else if ("minecraft:hopper".equals(var2)) {
			this.mc.displayGuiScreen(new GuiHopper(this.inventory, chestInventory));
		} else if ("minecraft:furnace".equals(var2)) {
			this.mc.displayGuiScreen(new GuiFurnace(this.inventory, chestInventory));
		} else if ("minecraft:brewing_stand".equals(var2)) {
			this.mc.displayGuiScreen(new GuiBrewingStand(this.inventory, chestInventory));
		} else if ("minecraft:beacon".equals(var2)) {
			this.mc.displayGuiScreen(new GuiBeacon(this.inventory, chestInventory));
		} else if (!"minecraft:dispenser".equals(var2) && !"minecraft:dropper".equals(var2)) {
			this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
		} else {
			this.mc.displayGuiScreen(new GuiDispenser(this.inventory, chestInventory));
		}
	}

	public void displayGUIHorse(EntityHorse horse, IInventory horseInventory) {
		this.mc.displayGuiScreen(new GuiScreenHorseInventory(this.inventory, horseInventory, horse));
	}

	public void displayGui(IInteractionObject guiOwner) {
		String var2 = guiOwner.getGuiID();

		if ("minecraft:crafting_table".equals(var2)) {
			this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj));
		} else if ("minecraft:enchanting_table".equals(var2)) {
			this.mc.displayGuiScreen(new GuiEnchantment(this.inventory, this.worldObj, guiOwner));
		} else if ("minecraft:anvil".equals(var2)) {
			this.mc.displayGuiScreen(new GuiRepair(this.inventory, this.worldObj));
		}
	}

	public void displayVillagerTradeGui(IMerchant villager) {
		this.mc.displayGuiScreen(new GuiMerchant(this.inventory, villager, this.worldObj));
	}

	/**
	 * Called when the player performs a critical hit on the Entity. Args:
	 * entity that was hit critically
	 */
	public void onCriticalHit(Entity entityHit) {
		this.mc.effectRenderer.emitParticleAtEntity(entityHit, EnumParticleTypes.CRIT);
	}

	public void onEnchantmentCritical(Entity entityHit) {
		this.mc.effectRenderer.emitParticleAtEntity(entityHit, EnumParticleTypes.CRIT_MAGIC);
	}

	/**
	 * Returns if this entity is sneaking.
	 */
	public boolean isSneaking() {
		boolean var1 = this.movementInput != null ? this.movementInput.sneak : false;
		return var1 && !this.sleeping;
	}

	public void updateEntityActionState() {
		super.updateEntityActionState();

		if (this.isCurrentViewEntity()) {
			this.moveStrafing = this.movementInput.moveStrafe;
			this.moveForward = this.movementInput.moveForward;
			this.isJumping = this.movementInput.jump;
			this.prevRenderArmYaw = this.renderArmYaw;
			this.prevRenderArmPitch = this.renderArmPitch;
			this.renderArmPitch = (float) ((double) this.renderArmPitch
					+ (double) (this.rotationPitch - this.renderArmPitch) * 0.5D);
			this.renderArmYaw = (float) ((double) this.renderArmYaw
					+ (double) (this.rotationYaw - this.renderArmYaw) * 0.5D);
		}
	}

	protected boolean isCurrentViewEntity() {
		return this.mc.getRenderViewEntity() == this;
	}

	/**
	 * Called frequently so the entity can update its state every tick as
	 * required. For example, zombies and skeletons use this to react to
	 * sunlight and start to burn.
	 */
	public void onLivingUpdate()
    {
        if (this.sprintingTicksLeft > 0)
        {
            --this.sprintingTicksLeft;

            if (this.sprintingTicksLeft == 0)
            {
                this.setSprinting(false);
            }
        }

        if (this.sprintToggleTimer > 0)
        {
            --this.sprintToggleTimer;
        }

        this.prevTimeInPortal = this.timeInPortal;

        if (this.inPortal)
        {
            if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame())
            {
                this.mc.displayGuiScreen((GuiScreen)null);
            }

            if (this.timeInPortal == 0.0F)
            {
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), this.rand.nextFloat() * 0.4F + 0.8F));
            }

            this.timeInPortal += 0.0125F;

            if (this.timeInPortal >= 1.0F)
            {
                this.timeInPortal = 1.0F;
            }

            this.inPortal = false;
        }
        else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60)
        {
            this.timeInPortal += 0.006666667F;

            if (this.timeInPortal > 1.0F)
            {
                this.timeInPortal = 1.0F;
            }
        }
        else
        {
            if (this.timeInPortal > 0.0F)
            {
                this.timeInPortal -= 0.05F;
            }

            if (this.timeInPortal < 0.0F)
            {
                this.timeInPortal = 0.0F;
            }
        }

        if (this.timeUntilPortal > 0)
        {
            --this.timeUntilPortal;
        }

        boolean var1 = this.movementInput.jump;
        boolean var2 = this.movementInput.sneak;
        float var3 = 0.8F;
        boolean var4 = this.movementInput.moveForward >= var3;
        this.movementInput.updatePlayerMoveState();

        if (this.isUsingItem() && !this.isRiding())
        {
        	if(!Crispy.INSTANCE.getHackManager().getHack(NoSlowDown.class).isEnabled()) {
				this.movementInput.moveStrafe *= 0.2F;
				this.movementInput.moveForward *= 0.2F;
				this.sprintToggleTimer = 0;
			}
        }

        this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ + (double)this.width * 0.35D);
        this.pushOutOfBlocks(this.posX - (double)this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ - (double)this.width * 0.35D);
        this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ - (double)this.width * 0.35D);
        this.pushOutOfBlocks(this.posX + (double)this.width * 0.35D, this.getEntityBoundingBox().minY + 0.5D, this.posZ + (double)this.width * 0.35D);
        boolean var5 = (float)this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.allowFlying;

        if (this.onGround && !var2 && !var4 && this.movementInput.moveForward >= var3 && !this.isSprinting() && var5 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness))
        {
            if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.getIsKeyPressed())
            {
                this.sprintToggleTimer = 7;
            }
            else
            {
                this.setSprinting(true);
            }
        }

        if (!this.isSprinting() && this.movementInput.moveForward >= var3 && var5 && !this.isUsingItem() && !this.isPotionActive(Potion.blindness) && this.mc.gameSettings.keyBindSprint.getIsKeyPressed())
        {
            this.setSprinting(true);
        }

        if (this.isSprinting() && (this.movementInput.moveForward < var3 || this.isCollidedHorizontally || !var5))
        {
            this.setSprinting(false);
        }

        if (this.capabilities.allowFlying)
        {
            if (this.mc.playerController.isSpectatorMode())
            {
                if (!this.capabilities.isFlying)
                {
                    this.capabilities.isFlying = true;
                    this.sendPlayerAbilities();
                }
            }
            else if (!var1 && this.movementInput.jump)
            {
                if (this.flyToggleTimer == 0)
                {
                    this.flyToggleTimer = 7;
                }
                else
                {
                    this.capabilities.isFlying = !this.capabilities.isFlying;
                    this.sendPlayerAbilities();
                    this.flyToggleTimer = 0;
                }
            }
        }

        if (this.capabilities.isFlying && this.isCurrentViewEntity())
        {
            if (this.movementInput.sneak)
            {
                this.motionY -= (double)(this.capabilities.getFlySpeed() * 3.0F);
            }

            if (this.movementInput.jump)
            {
                this.motionY += (double)(this.capabilities.getFlySpeed() * 3.0F);
            }
        }

        if (this.isRidingHorse())
        {
            if (this.horseJumpPowerCounter < 0)
            {
                ++this.horseJumpPowerCounter;

                if (this.horseJumpPowerCounter == 0)
                {
                    this.horseJumpPower = 0.0F;
                }
            }

            if (var1 && !this.movementInput.jump)
            {
                this.horseJumpPowerCounter = -10;
                this.sendHorseJump();
            }
            else if (!var1 && this.movementInput.jump)
            {
                this.horseJumpPowerCounter = 0;
                this.horseJumpPower = 0.0F;
            }
            else if (var1)
            {
                ++this.horseJumpPowerCounter;

                if (this.horseJumpPowerCounter < 10)
                {
                    this.horseJumpPower = (float)this.horseJumpPowerCounter * 0.1F;
                }
                else
                {
                    this.horseJumpPower = 0.8F + 2.0F / (float)(this.horseJumpPowerCounter - 9) * 0.1F;
                }
            }
        }
        else
        {
            this.horseJumpPower = 0.0F;
        }

        super.onLivingUpdate();

        if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.isSpectatorMode())
        {
            this.capabilities.isFlying = false;
            this.sendPlayerAbilities();
        }
    }
}
