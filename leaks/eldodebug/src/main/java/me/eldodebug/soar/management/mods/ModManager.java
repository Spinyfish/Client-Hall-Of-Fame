package me.eldodebug.soar.management.mods;

import java.util.ArrayList;

import me.eldodebug.soar.management.mods.impl.*;

public class ModManager {

	private ArrayList<Mod> mods = new ArrayList<Mod>();
	
	public ModManager() {
		mods.add(new ArmorStatusMod());
		mods.add(new BlockInfoMod());
		mods.add(new BlockOverlayMod());
		mods.add(new BloodParticlesMod());
		mods.add(new BorderlessFullscreenMod());
		mods.add(new BossbarMod());
		mods.add(new BowZoomMod());
		mods.add(new BreadcrumbsMod());
		mods.add(new ChatMod());
		mods.add(new ChunkAnimatorMod());
		mods.add(new ChunkBordersMod());
		mods.add(new ClearGlassMod());
		mods.add(new ClearWaterMod());
		mods.add(new ClickEffectMod());
		mods.add(new ClickGUIMod());
		mods.add(new CompassMod());
		mods.add(new CoordsMod());
		mods.add(new ClientInfoMod());
		mods.add(new ClientMod());
		mods.add(new ComboCounterMod());
		mods.add(new CPSDisplayMod());
		mods.add(new CrosshairMod());
		mods.add(new DamageParticleMod());
		mods.add(new DayCounterMod());
		mods.add(new DamageTintMod());
		mods.add(new FovModifierMod());
		mods.add(new FarCameraMod());
		mods.add(new ForgeSpooferMod());
		mods.add(new FPSBoostMod());
		mods.add(new FPSDisplayMod());
		mods.add(new FPSLimiterMod());
		mods.add(new FPSSpooferMod());
		mods.add(new FreelookMod());
		mods.add(new FullbrightMod());
		mods.add(new GlintColorMod());
		mods.add(new HealthDisplayMod());
		mods.add(new HitboxMod());
		mods.add(new HitColorMod());
		mods.add(new HitDelayFixMod());
		mods.add(new HorseStatsMod());
		mods.add(new HotbarMod());
		mods.add(new HUDMod());
		mods.add(new HypixelMod());
		mods.add(new HypixelQuickPlayMod());
		mods.add(new ImageDisplayMod());
		mods.add(new InventoryDisplayMod());
		mods.add(new InventoryMod());
		mods.add(new ItemInfoMod());
		mods.add(new ItemPhysicsMod());
		mods.add(new KeystrokesMod());
		mods.add(new KillEffectsMod());
		mods.add(new MemoryUsageMod());
		mods.add(new MenuBlurMod());
		mods.add(new MinimalBobbingMod());
		mods.add(new MinimalDamageShakeMod());
		mods.add(new MinimapMod());
		mods.add(new MotionBlurMod());
		mods.add(new MouseDisplayMod());
		mods.add(new MusicInfoMod());
		mods.add(new NameDisplayMod());
		mods.add(new NameProtectMod());
		mods.add(new NametagMod());
		mods.add(new OldAnimationsMod());
		mods.add(new OverlayEditorMod());
		mods.add(new PackDisplayMod());
		mods.add(new ParticlesMod());
		mods.add(new PingDisplayMod());
		mods.add(new PlayerDisplayMod());
		mods.add(new PotionCounterMod());
		mods.add(new PotionStatusMod());
		mods.add(new RawInputMod());
		mods.add(new ReachCirclesMod());
		mods.add(new ReachDisplayMod());
		mods.add(new RearviewMod());
		mods.add(new SaturationMod());
		mods.add(new ScoreboardMod());
		mods.add(new ScreenshotViewerMod());
		mods.add(new ServerIPDisplayMod());
		mods.add(new SessionInfoMod());
		mods.add(new SkinProtectMod());
		mods.add(new SlowSwingMod());
		mods.add(new SmallHeldItemsMod());
		mods.add(new SneakMod());
		mods.add(new SoundModifierMod());
		mods.add(new SpawnNPCMod());
		mods.add(new SpeedometerMod());
		mods.add(new SprintMod());
		mods.add(new StopwatchMod());
		mods.add(new TabEditorMod());
		mods.add(new TaplookMod());
		mods.add(new TargetIndicatorMod());
		mods.add(new TargetInfoMod());
		mods.add(new TimeChangerMod());
		mods.add(new TimeDisplayMod());
		mods.add(new TNTTimerMod());
		mods.add(new UHCOverlayMod());
		mods.add(new WeatherChangerMod());
		mods.add(new WeatherDisplayMod());
		mods.add(new WingsMod());
		mods.add(new ZoomMod());
	}
	
	public Mod getModByName(String name) {
		return mods.stream().filter(mod -> mod.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	public Mod getModByClass(Class<?> modClass) {
		return mods.stream().filter(mod -> mod.getClass().equals(modClass)).findFirst().orElse(null);
	}
	
	public ArrayList<Mod> getModulesInCategory(ModCategory c) {
		ArrayList<Mod> mods = new ArrayList<Mod>();
		for (Mod m : this.mods) {
			if (m.getCategory() == c) {
				mods.add(m);
			}
		}
		return mods;
	}

	public ArrayList<Mod> getMods() {
		return mods;
	}
}
