package axolotl.cheats.modules.impl.chat;

import axolotl.Axolotl;
import axolotl.cheats.events.Event;
import axolotl.cheats.events.EventPacket;
import axolotl.cheats.events.EventType;
import axolotl.cheats.events.EventUpdate;
import axolotl.cheats.modules.Module;
import axolotl.cheats.settings.BooleanSetting;
import axolotl.cheats.settings.ModeSetting;
import axolotl.cheats.settings.NumberSetting;
import axolotl.util.ChatUtil;
import axolotl.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class Killsults extends Module {

	private final String axo = "Axo" + "lo" + "tl";

	public ModeSetting mode = new ModeSetting("Mode", axo, axo, "UwU");

	public Killsults() {
		super("Killsults", Category.CHAT, true);
		this.addSettings(mode);
		this.setSpecialSetting(mode);
	}

	private Entity target;
	private int buffer;
	
	public void onEvent(Event e) {

		if(e instanceof EventPacket) {
			if (((EventPacket) e).getPacket() instanceof C02PacketUseEntity) {
				if (e.eventType == EventType.PRE) {

					C02PacketUseEntity c02 = (C02PacketUseEntity) ((EventPacket) e).getPacket();

					if (c02.getAction() == C02PacketUseEntity.Action.ATTACK) {

						int entityId = c02.entityId;

						Entity target = mc.theWorld.getEntityByID(entityId);

						if (target instanceof EntityPlayer) {

							this.target = target;
							buffer = 40;

						}

					}

				}

			}
			
		} else if (e instanceof EventUpdate) {

			if (target != null && target.isDead) {

				mc.thePlayer.sendChatMessage(getMessage());
				target = null;

			} else {
				buffer--;
				if(buffer <= 0) {
					target = null;
				}
			}

		}
 		
	}

	private String[] axolotlMessage = new String[]{
			"Haha, Axolotl on top! %player%",
			"%player% got fucked by Axolotl Client!",
			"Get Axolotl Client on intent store! %player%",
			"Axolotl beat you %player%",
			"Aww.. gonna cry, %player%? Axolotl on top",
			"%player% uses vanilla minecraft. Get Axolotl!",
			"%player% lost due to not being able to afford a gaming chair. Axolotl on top!"
	};
	private String[] uwuMessage = new String[]{
			"Haha it's so small, %player%",
			"You came too fast, %player%",
			"I see why you're still a virgin, %player%",
			"%player% has the smallest pp I've seen in a while, >w<",
			"Axolotl client is hotter than you >w<",
			"Senpai~ why is it so small, %player%",
			"%player%-san's thing is so thin, it can't pleasure me!"
	};

	public String getMessage() {
		switch(mode.getMode()) {
			case axo:
				return format(axolotlMessage[(int)Math.floor(Math.random() * axolotlMessage.length)]);
			case "UwU":
				return format(uwuMessage[(int)Math.floor(Math.random() * uwuMessage.length)]);
		}
		return null;
	}

	public String format(String message) {
		return message.replace("%player%", target.getName());
	}
	
}
