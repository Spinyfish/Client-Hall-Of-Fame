package club.marsh.bloom.impl.mods.combat;

import club.marsh.bloom.impl.mods.combat.criticals.FlyOverCriticals;
import club.marsh.bloom.impl.mods.combat.criticals.JumpCriticals;
import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.ModeValue;
import club.marsh.bloom.impl.events.AttackEvent;
import club.marsh.bloom.impl.mods.combat.criticals.PacketCriticals;


public class Criticals extends Module {
	  ModeValue mode = new ModeValue("Mode", "Packet", new String[] {"Packet", "Jump", "Fly Over"});

	  public Criticals() {
		super("Criticals",Keyboard.KEY_NONE,Category.COMBAT);
	  }
	  @Override
	  public void addModesToModule() {
		    autoSetName(mode);
			addModes(
					new PacketCriticals(this,"Packet",mode),
					new JumpCriticals(this,"Jump",mode),
					new FlyOverCriticals(this,"Fly Over",mode)
			);
	  }
	  

}
