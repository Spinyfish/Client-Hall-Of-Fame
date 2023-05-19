package club.marsh.bloom.impl.mods.movement;

import org.lwjgl.input.Keyboard;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.mods.movement.speeds.*;
import club.marsh.bloom.api.value.ModeValue;


public class Speed extends Module {
	public Speed() {
		super("Speed",Keyboard.KEY_N,Category.MOVEMENT);
	}
	
	@Override
	public void onDisable() {
		mc.timer.timerSpeed = 1f;
	}
	
	ModeValue mode = new ModeValue("Mode", "Vanilla", new String[] {"Vanilla", "Acentra", "Vulcan Ground", "AAC 3", "AAC 4", "AAC 5", "Hurttime", "Vanilla Bhop", "No Rules", "Incognito", "NCP", "Updated NCP", "Dev", "Morgan", "Matrix", "Vulcan", "Sloth", "Watchdog","Legit"});
	
	@Override
	  public void addModesToModule() {
			autoSetName(mode);
			addModes(
					new VulcanSpeed(this,"Vulcan",mode),
					new DevSpeed(this,"Dev",mode),
					new UNCPSpeed(this,"Updated NCP",mode),
					new VanillaSpeed(this,"Vanilla",mode),
					new MatrixSpeed(this,"Matrix",mode),
					new SlothSpeed(this,"Sloth",mode),
					new HypixelSpeed(this,"Watchdog",mode),
					new NCPSpeed(this,"NCP",mode),
					new LegitSpeed(this,"Legit",mode),
					new MorganSpeed(this,"Morgan",mode),
					new IncognitoSpeed(this,"Incognito",mode),
					new NoRulesSpeed(this,"No Rules",mode),
					new VanillaBhopSpeed(this,"Vanilla Bhop",mode),
					new AAC3Speed(this, "AAC 3", mode),
					new VulcanGroundSpeed(this,"Vulcan Ground", mode),
					new AAC4Speed(this, "AAC 4", mode),
					new AAC5Speed(this, "AAC 5", mode),
					new HurttimeSpeed(this,"Hurttime", mode),
					new AcentraSpeed(this,"Acentra", mode)
			);
	  }
}
