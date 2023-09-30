package axolotl.cheats.modules.impl.world;

import axolotl.cheats.events.Event;
import axolotl.cheats.events.EventType;
import axolotl.cheats.events.EventUpdate;
import axolotl.cheats.modules.Module;
import axolotl.util.RenderUtils;

public class BedNuker extends Module {

	public BedNuker() {

		super("BedNuker", Category.WORLD, true);

	}
	
	public void onEvent(Event e) {
		
		if(e instanceof EventUpdate && e.eventType == EventType.PRE) {

			for(int x=-2;x<2;x++) {

				for(int y=-2;y<2;y++) {

					for(int z=-2;z<2;z++) {

						//if(mc.theWorld.getBlockState(new BlockPos(x + mc.thePlayer.posX, y + mc.thePlayer.posY, z + mc.thePlayer.posZ)).getBlock() == Blocks.bed) {
							RenderUtils.drawOutlinedBlockESP(x + mc.thePlayer.posX, y + mc.thePlayer.posY, z + mc.thePlayer.posZ, 1f, 0f, 0f, 1f, 1f);
						//}

					}

				}

			}
			
		}
		
	}

}
