package org.crystalpvp.anticheat.data.player;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumDirection;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.crystalpvp.anticheat.CrystalAC;

import org.crystalpvp.anticheat.api.checks.ICheck;

import org.crystalpvp.anticheat.check.combat.aimassist.*;
import org.crystalpvp.anticheat.check.combat.autoclicker.*;
import org.crystalpvp.anticheat.check.combat.range.*;
import org.crystalpvp.anticheat.check.connection.badpackets.*;
import org.crystalpvp.anticheat.check.misc.fastbow.*;
import org.crystalpvp.anticheat.check.misc.fastbreak.*;
import org.crystalpvp.anticheat.check.misc.fastrefill.*;
import org.crystalpvp.anticheat.check.misc.headrotations.*;
import org.crystalpvp.anticheat.check.movement.blink.BlinkA;
import org.crystalpvp.anticheat.check.movement.fly.*;
import org.crystalpvp.anticheat.check.misc.inventory.*;
import org.crystalpvp.anticheat.check.movement.jesus.*;

import org.crystalpvp.anticheat.check.combat.killaura.*;
import org.crystalpvp.anticheat.check.movement.nofall.*;
import org.crystalpvp.anticheat.check.movement.noslowdown.*;
import org.crystalpvp.anticheat.check.movement.phase.*;
import org.crystalpvp.anticheat.check.connection.ping.*;
import org.crystalpvp.anticheat.check.movement.scaffold.*;
import org.crystalpvp.anticheat.check.movement.speed.*;
import org.crystalpvp.anticheat.check.movement.step.*;
import org.crystalpvp.anticheat.check.misc.timer.*;
import org.crystalpvp.anticheat.check.misc.vclip.*;
import org.crystalpvp.anticheat.check.misc.velocity.*;
import org.crystalpvp.anticheat.check.movement.wtap.*;
import org.crystalpvp.anticheat.data.location.CrystalLocation;
import org.crystalpvp.anticheat.data.version.ClientType;
import org.crystalpvp.anticheat.data.version.EnumClientType;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.BlockPosition;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

@Setter @Getter public final class PlayerData {

	private static final Map<Class<? extends ICheck>, Constructor<? extends ICheck>> CONSTRUCTORS;
	public static final Class<? extends ICheck>[] CHECKS;

	private final Map<ICheck, Set<Long>> checkViolationTimes;
	private final Map<ICheck, Double> checkVlMap;
	private final Map<Class<? extends ICheck>, ICheck> checkMap;
	private int totalVl;

	private Map<String, String> forgeMods;
	private ClientType client;

	private String randomBanReason;
	private double randomBanRate;
	private boolean randomBan;
	private boolean banned;
	private boolean banWave;
	private boolean banning;

	private boolean exempted = false;

	private final Set<CrystalLocation> teleportCrystalLocations;
	private boolean allowTeleport;
	private long lastTeleport;
	private boolean sendingVape;
	public int totalTicks = 0;

	private org.bukkit.Location spawnPoint;
	private long joinTime;
	private long respawnTime;


	private boolean inventoryOpen;
	private boolean setInventoryOpen;
	private boolean isShiftClicking;
	private long windowClick;
	private int lastSlotClicked;
	private String lastItemSlotClicked, lastSlotType;


	private boolean attackedSinceVelocity;


	private long lastShoot;



	private final Set<BlockPosition> fakeBlocks;
	private boolean underBlock;
	private boolean sprinting;
	private boolean sneaking;
	private boolean inLiquid;
	private boolean inWeb;
	private boolean onIce;
	private boolean onPiston;

	private boolean wasUnderBlock;
	private boolean wasOnGround;
	private boolean wasInLiquid;
	private boolean wasInWeb;
	private boolean wasOnPiston;

	private BlockPosition diggingBlock = null;
	private EnumDirection diggingBlockFace = null;
	private boolean abortedDigging = false;

	private int movementsSinceIce;
	private int movementsSinceUnderBlock;
	private String xDirection;
	private String zDirection;
	private long lastMove;


	private boolean instantBreakDigging;
	private boolean fakeDigging;
	private boolean sniffing;
	private boolean placing;
	public boolean digging;
	private StringBuilder sniffedPacketBuilder;

	private boolean onGround;
	private boolean onStairs;
	private boolean onCarpet;

	private long lastAutoclickerLSwing;

	private double lastGroundY;
	private double velocityX;
	private double velocityY;
	private double velocityZ;
	private int velocityH;
	private int velocityV;
	private long lastVelocity;

	private final Set<UUID> playersWatching;
	private UUID lastTarget;
	private int entityAction;


	private final Map<UUID, List<CrystalLocation>> recentPlayerPackets;
	private CrystalLocation lastMovePacket;
	private CrystalLocation queue1LastMovePacket;
	private long lastDelayedMovePacket;
	private long lastAnimationPacket;
	private long lastAttackPacket;
	private long lastDamagePacket;
	private long lastTeleportTime;


	private double lastYawH;
	private double lastPitchH;

	private String facing;
	private String wasFacing;
	private float lastDiffPitch;
	private float lastDiffYaw;

	private long lastClick;
	private int lastCps;
	private int lastArmSwing;
	private int lastBlocksPlace;
	private int lastCpsJ;
	private int autoClickerJ;


	private double lastDistanceXZ;
	private double lastDistanceY;


	private final Map<Integer, Long> keepAliveTimes;
	private long transactionPing;
	private long ping;

	private Player player;

	public PlayerData() {
		this.fakeBlocks = new HashSet<BlockPosition>() {};
		this.recentPlayerPackets = new HashMap<>();
		this.checkViolationTimes = new HashMap<>();
		this.playersWatching = new HashSet<UUID>();
		this.checkMap = new HashMap<>();
		this.keepAliveTimes = new HashMap<>();
		this.checkVlMap = new HashMap<>();
		this.teleportCrystalLocations = Collections.newSetFromMap(new ConcurrentHashMap<CrystalLocation, Boolean>());
		this.sniffedPacketBuilder = new StringBuilder();
		this.client = EnumClientType.VANILLA;

		CrystalAC.getInstance().getServer().getScheduler().runTaskAsynchronously(CrystalAC.getInstance(), () -> {
			PlayerData.CONSTRUCTORS.keySet().stream().map(o -> (Class<? extends ICheck>) o).forEach(check -> {
				Constructor<? extends ICheck> constructor = PlayerData.CONSTRUCTORS.get(check);
				try {
					this.checkMap.put(check, constructor.newInstance(this));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
		});
	}

	public <T extends ICheck> T getCheck(final Class<T> clazz) {
		return (T) this.checkMap.get(clazz);
	}

	public CrystalLocation getLastPlayerPacket(final UUID playerUUID, final int index) {
		final List<CrystalLocation> crystalLocations = this.recentPlayerPackets.get(playerUUID);
		if (crystalLocations != null && crystalLocations.size() > index) {
			return crystalLocations.get(crystalLocations.size() - index);
		}
		return null;
	}

	public void addPlayerPacket(final UUID playerUUID, final CrystalLocation crystalLocation) {
		List<CrystalLocation> crystalLocations = this.recentPlayerPackets.get(playerUUID);
		if (crystalLocations == null) {
			crystalLocations = new ArrayList<>();
		}
		if (crystalLocations.size() == 20) {
			crystalLocations.remove(0);
		}
		crystalLocations.add(crystalLocation);
		this.recentPlayerPackets.put(playerUUID, crystalLocations);
	}

	public void addTeleportLocation(final CrystalLocation teleportCrystalLocation) {
		this.teleportCrystalLocations.add(teleportCrystalLocation);
	}

	public boolean allowTeleport(final CrystalLocation teleportCrystalLocation) {
		for (final CrystalLocation crystalLocation : this.teleportCrystalLocations) {
			final double delta = Math.pow(teleportCrystalLocation.getX() - crystalLocation.getX(), 2.0) +
			                     Math.pow(teleportCrystalLocation.getZ() - crystalLocation.getZ(), 2.0);
			if (delta <= 0.005) {
				this.teleportCrystalLocations.remove(crystalLocation);
				return true;
			}
		}
		return false;
	}

	public double getCheckVl(final ICheck check) {
		if (!this.checkVlMap.containsKey(check)) {
			this.checkVlMap.put(check, 0.0);
		}

		return this.checkVlMap.get(check);
	}

	public void setCheckVl(double vl, final ICheck check) {
		if (vl < 0.0) {
			vl = 0.0;
		}

		this.checkVlMap.put(check, vl);
	}

	public boolean keepAliveExists(final int id) {
		return this.keepAliveTimes.containsKey(id);
	}

	public long getKeepAliveTime(final int id) {
		return this.keepAliveTimes.get(id);
	}

	public void removeKeepAliveTime(final int id) {
		this.keepAliveTimes.remove(id);
	}

	public void addKeepAliveTime(final int id) {
		this.keepAliveTimes.put(id, System.currentTimeMillis());
	}

	public int getViolations(final ICheck check, final Long time) {
		final Set<Long> timestamps = this.checkViolationTimes.get(check);

		if (timestamps != null) {
			int violations = 0;

			for (final long timestamp : timestamps) {
				if (System.currentTimeMillis() - timestamp <= time) {
					++violations;
				}
			}

			return violations;
		}

		return 0;
	}

	public void addViolation(final ICheck check) {
		Set<Long> timestamps = this.checkViolationTimes.get(check);

		if (timestamps == null) {
			timestamps = new HashSet<>();
		}

		timestamps.add(System.currentTimeMillis());

		this.checkViolationTimes.put(check, timestamps);
	}

	static {
		CHECKS = new Class[]{
				AimAssistA.class, AimAssistB.class, AimAssistC.class, AimAssistD.class,
				AimAssistE.class, AimAssistF.class, AimAssistG.class, AimAssistG.class, AimAssistH.class,

				AutoClickerA.class, AutoClickerB.class, AutoClickerC.class, AutoClickerD.class,
				AutoClickerE.class, AutoClickerF.class, AutoClickerG.class, AutoClickerH.class,
				AutoClickerI.class, AutoClickerJ.class, AutoClickerK.class, AutoClickerl.class, AutoClickerM.class, AutoClickerN.class, AutoClickerO.class,

				BadPacketsA.class, BadPacketsB.class, BadPacketsC.class, BadPacketsD.class,
				BadPacketsE.class, BadPacketsF.class, BadPacketsG.class, BadPacketsH.class,
				BadPacketsI.class, BadPacketsJ.class, BadPacketsK.class,

				BlinkA.class,

				FastBowA.class,

				FastBreakA.class,

				FastRefillA.class,


				FlyA.class, FlyB.class, FlyC.class, FlyD.class, FlyE.class, FlyF.class,
                FlyG.class, FlyH.class, FlyI.class, FlyJ.class, FlyK.class,

				InventoryA.class, InventoryB.class, InventoryC.class, InventoryD.class,

				JesusA.class, JesusB.class, JesusC.class, JesusD.class,

				KillAuraA.class, KillAuraB.class, KillAuraC.class, KillAuraD.class,
				KillAuraE.class, KillAuraF.class, KillAuraG.class, KillAuraH.class,
				KillAuraI.class, KillAuraJ.class, KillAuraK.class, KillAuraL.class,
				KillAuraM.class, KillAuraN.class, KillAuraO.class, KillAuraP.class,
				KillAuraQ.class, KillAuraR.class, KillAuraS.class,

				PhaseA.class, PhaseB.class,

				PingSpoofA.class,

                NoFallA.class, NoFallB.class, NoFallC.class, NoFallD.class,

				SpeedA.class, SpeedB.class, SpeedC.class, SpeedD.class,

                NoSlowDownA.class, NoSlowDownB.class,



				RangeA.class, RangeB.class, RangeC.class, RangeE.class, RangeD.class, RangeF.class, RangeG.class,

				HeadRotationsA.class,

				ScaffoldA.class, ScaffoldB.class, ScaffoldC.class,

				StepA.class, StepB.class, StepC.class, StepD.class,

				TimerA.class, TimerB.class, TimerC.class,

				VClipA.class, VClipB.class,

				VelocityA.class, VelocityB.class, VelocityC.class, VelocityD.class, VelocityE.class, VelocityF.class,

				WTapA.class, WTapB.class,
		};

		CONSTRUCTORS = new ConcurrentHashMap<>();

		for (final Class<? extends ICheck> check : PlayerData.CHECKS) {
			try {
				PlayerData.CONSTRUCTORS.put(check, check.getConstructor(PlayerData.class));
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}


	public Set<BlockPosition> getFakeBlocks() {
		return this.fakeBlocks;
	}


	public static void timer() throws InterruptedException {
		Thread thread = new Thread();
		while(true) {
			thread.sleep(1000);
		}
	}

	public long getLastTeleportTime() {
		return this.lastTeleportTime;
	}

	public boolean isBanning() {
		return this.banning;
	}

	public void setBanning(final boolean banning) {
		this.banning = banning;
	}

	public void setBanWave(final boolean banWave) {
		this.banWave = banWave;
	}

	public boolean isPlayerWatching(final Player player) {
		return this.playersWatching.contains(player.getUniqueId());
	}

	public void togglePlayerWatching(final Player player) {
		if (!this.playersWatching.remove(player.getUniqueId())) {
			this.playersWatching.add(player.getUniqueId());
		}
	}

	public Set<UUID> getPlayersWatching() {
		return this.playersWatching;
	}

}
