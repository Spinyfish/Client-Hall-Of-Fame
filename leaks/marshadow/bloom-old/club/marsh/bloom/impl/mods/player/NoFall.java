package club.marsh.bloom.impl.mods.player;

import org.lwjgl.input.Keyboard;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.mods.player.nofalls.*;
import club.marsh.bloom.api.value.ModeValue;


public class NoFall extends Module {
	public NoFall() {
		super("Nofall",Keyboard.KEY_NONE,Category.PLAYER);
	}

	
	ModeValue mode = new ModeValue("Mode", "Vanilla", new String[] {"Vanilla", "No Ground", "Vulcan", "Ground Spoof", "Dev", "Verus", "Clip"});
	
	  @Override
	  public void addModesToModule() {
		    autoSetName(mode);
			addModes(
					new DevNoFall(this,"Dev",mode),
					new NoGroundNoFall(this,"No Ground",mode),
					new GroundSpoofNoFall(this,"Ground Spoof",mode),
					new VerusNoFall(this,"Verus",mode),
					new ClipNofall(this,"Clip",mode),
					new VulcanNoFall(this, "Vulcan", mode)
			);
	  }
}
