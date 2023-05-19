package club.marsh.bloom.impl.mods.movement;

import club.marsh.bloom.impl.mods.movement.flys.*;
import org.lwjgl.input.Keyboard;


import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.api.value.ModeValue;


public class Flight extends Module {
	  ModeValue mode = new ModeValue("Mode", "Vanilla", new String[] {"Vanilla", "Custom Damage", "Vulcan New", "NCP Blink Headhitter", "Vulcan Damage", "No Rules Disabler", "Spigot Exploit", "Grim TNT", "Old NCP", "Grim", "No Rules", "Morgan", "Jump", "Incognito", "Block Place", "Morgan High", "Collide", "Dev", "Vulcan", "Sloth"});
	  BooleanValue viewbobbing = new BooleanValue("View bobbing",true);
	  public Flight() {
		super("Fly",Keyboard.KEY_F,Category.MOVEMENT);
	  }
	  @Override
	  public void addModesToModule() {
		    autoSetName(mode);
			addModes(
					new VulcanFly(this,"Vulcan",mode),
					new DevFly(this,"Dev",mode),
					new CollideFly(this,"Collide",mode),
					new BlockPlaceFly(this,"Block Place",mode),
					new VanillaFly(this,"Vanilla",mode),
					new SlothFly(this,"Sloth",mode),
					new JumpFly(this,"Jump",mode),
					new MorganFly(this,"Morgan",mode),
					new MorganHighFly(this,"Morgan High",mode),
					new IncognitoFly(this,"Incognito",mode),
					new NoRulesFly(this,"No Rules",mode),
					new GrimACFly(this,"Grim",mode),
					new OldNCPFly(this,"Old NCP",mode),
					new CustomDamageFly(this, "Custom Damage", mode),
					new GrimTntFly(this,"Grim TNT",mode),
					new SpigotExploitFly(this,"Spigot Exploit", mode),
					new NoRulesDisablerFly(this, "No Rules Disabler",mode),
					new VulcanDamageFly(this, "Vulcan Damage", mode),
					new NCPBlinkHeadhitterFly(this,"NCP Blink Headhitter",mode),
					new VulcanNewFly(this,"Vulcan New", mode)
			);
	  }
	  // NumberValue speed = new NumberValue("Speed",1.0,0,3, () -> (mode.mode == "Vanilla"));
	  @Override
	  public void onDisable() {
		  mc.thePlayer.speedInAir = 0.02f;
		  mc.timer.timerSpeed = 1;
	  }
	  @Subscribe
	  public void onUpdate(UpdateEvent e) {
		  if (viewbobbing.isOn())
			  mc.thePlayer.cameraYaw = 0.1f;
	  }
}
