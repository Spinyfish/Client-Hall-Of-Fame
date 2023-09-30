package juul.module.movement;

import juul.event.Event;
import juul.event.EventUpdate;
import juul.inject.wrappers.EntityLivingBaseWrapper;
import juul.inject.wrappers.EntityPlayerSPWrapper;
import juul.inject.wrappers.WorldWrapper;
import juul.module.Module;
import juul.module.settings.RangeSetting;
import juul.util.objects.Timer;
import juul.util.value.Random;
import net.minecraft.util.BlockPos;

public class EdgeJump extends Module {

	public RangeSetting delay;

	private final Timer tDelay = new Timer();

	public EdgeJump() {
		super("EdgeJump", "", 0, Category.MOVEMENT);
		addSetting(delay = new RangeSetting("Delay", "delay between jumps", 0, 0, 0, 250, 1));

	}
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {

			double dbDelay = (delay.getStart() > 0) ? Random.dbRandom(delay.getStart(), delay.getEnd()) : 0;
			if (!tDelay.hasTimeElapsed((long) dbDelay, false))
				return;

			WorldWrapper wWorld = new WorldWrapper(constants.world());
			EntityPlayerSPWrapper wEntityPlayerSP = thePlayerSP;
			EntityLivingBaseWrapper wEntityLivingBase = thePlayerSP.getEntityLivingBaseWrapper();

			BlockPos pos = new BlockPos(wEntityLivingBase.posX(), wEntityLivingBase.posY() - 1, wEntityLivingBase.posX());

			// wrap getblockstate
			// IBlockState state = wWorld.getBlockState(pos);

			// (wrap jump)
			// if (state != null && state.getBlock() == Blocks.air && wEntityPlayerSP.getOnGround()) wEntityPlayerSP.jump();

		}
	}
	
}
