package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventLoadWorld;
import me.eldodebug.soar.management.events.impl.EventReceivePacket;
import me.eldodebug.soar.management.events.impl.EventSendPacket;
import me.eldodebug.soar.management.events.impl.EventTick;
import me.eldodebug.soar.management.events.impl.EventUpdate;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.utils.TimerUtils;
import me.eldodebug.soar.utils.server.ServerUtils;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.S02PacketChat;

public class HypixelMod extends Mod{

	private TimerUtils timer = new TimerUtils();
	
    public String playCommand = "";
    
    private boolean autoplay = false, autogg = false, loaded = false;
    
    private TimerUtils tipTimer = new TimerUtils();
    
	public HypixelMod() {
		super("Hypixel", "Hypixel with useful mods", ModCategory.PLAYER);
	}

	@Override
	public void setup() {
		this.addBooleanSetting("Auto GG", this, false);
		this.addBooleanSetting("Auto Tip", this, false);
		this.addBooleanSetting("Auto Play", this, false);
		this.addBooleanSetting("Level Head", this, true);
		this.addSliderSetting("Delay", this, 3, 0, 5, true);
	}
	
	@Override
	public void onEnable() {
		this.autoplay = false;
		this.autogg = false;
		timer.reset();
		super.onEnable();
	}
	
	@EventTarget
	public void onTick(EventTick event) {
		if(Soar.instance.settingsManager.getSettingByName(this, "Auto Tip").getValBoolean()) {
			if(ServerUtils.isHypixel()) {
				if(tipTimer.delay(1200000)) {
					mc.thePlayer.sendChatMessage("/tip all");
					tipTimer.reset();
				}
			}else {
				tipTimer.reset();
			}
		}else {
			tipTimer.reset();
		}
	}
	
	@EventTarget
	public void onUpdate(EventUpdate event) {
		
		int delay = (int) Soar.instance.settingsManager.getSettingByName(this, "Delay").getValDouble();

		if(ServerUtils.isHypixel()) {
			if(Soar.instance.settingsManager.getSettingByName(this, "Auto GG").getValBoolean()) {
				if(this.autogg == true) {
					mc.thePlayer.sendChatMessage("/achat gg");
					this.autogg = false;
				}
			}
			
			if(Soar.instance.settingsManager.getSettingByName(this, "Auto Play").getValBoolean()) {
				if(this.autoplay == true) {
					if(!loaded) {
						loaded = true;
					}
					if(timer.delay(1000 * delay)) {
						mc.thePlayer.sendChatMessage(playCommand);
						timer.reset();
						this.autoplay = false;
						loaded = false;
					}
				}else {
					timer.reset();
				}
			}
		}
	}
    
	@EventTarget
	public void onLoadWorld(EventLoadWorld event) {
		this.autoplay = false;
		this.autogg = false;
		timer.reset();
	}
	
    @EventTarget
    public void onReceivePacket(EventReceivePacket event) {
    	
    	if(ServerUtils.isHypixel()) {
            if (event.getPacket() instanceof S02PacketChat) {
                S02PacketChat chatPacket = (S02PacketChat) event.getPacket();
                String chatMessage = chatPacket.getChatComponent().getUnformattedText();
                if(chatMessage.contains("WINNER!") ||  chatMessage.contains("1st Killer -") || chatMessage.contains("Top Survivors")) {
                	this.autogg = true;
                }
                
                if(chatMessage.contains("WINNER!") ||  chatMessage.contains("1st Killer -") || chatMessage.contains("Top Survivors") || chatMessage.contains("You died!")) {
                	this.autoplay = true;
                }
            }
    	}
    }
    
    @EventTarget
    public void onSendPacket(EventSendPacket e) {
    	
    	if(ServerUtils.isHypixel()) {
            if (playCommand.startsWith("/play ")) {
                String display = playCommand.replace("/play ", "").replace("_", " ");
                boolean nextUp = true;
                for (char c : display.toCharArray()) {
                    if (c == ' ') {
                        nextUp = true;
                        continue;
                    }
                    if (nextUp) {
                        nextUp = false;
                    }
                }
            }

            if (e.getPacket() instanceof C0EPacketClickWindow) {
            	
                C0EPacketClickWindow packet = (C0EPacketClickWindow) e.getPacket();
                String itemname;
                
                if(packet.getClickedItem() == null) {
                	return;
                }
                
                itemname = packet.getClickedItem().getDisplayName();
                
                if (packet.getClickedItem().getDisplayName().startsWith("\247a")) {
                    int itemID = Item.getIdFromItem(packet.getClickedItem().getItem());
                    if (itemID == 381 || itemID == 368) {
                        if (itemname.contains("SkyWars")) {
                            if (itemname.contains("Doubles")) {
                                if (itemname.contains("Normal")) {
                                    playCommand = "/play teams_normal";
                                } else if (itemname.contains("Insane")) {
                                    playCommand = "/play teams_insane";
                                }
                            } else if (itemname.contains("Solo")) {
                                if (itemname.contains("Normal")) {
                                    playCommand = "/play solo_normal";
                                } else if (itemname.contains("Insane")) {
                                    playCommand = "/play solo_insane";
                                }
                            }
                        }
                    } else if (itemID == 355) {
                        if (itemname.contains("Bed Wars")) {
                            if (itemname.contains("4v4")) {
                                playCommand = "/play bedwars_four_four";
                            } else if (itemname.contains("3v3")) {
                                playCommand = "/play bedwars_four_three";
                            } else if (itemname.contains("Doubles")) {
                                playCommand = "/play bedwars_eight_two";
                            } else if (itemname.contains("Solo")) {
                                playCommand = "/play bedwars_eight_one";
                            }
                        }
                    }
                }
            } else if (e.getPacket() instanceof C01PacketChatMessage) {
                C01PacketChatMessage packet = (C01PacketChatMessage) e.getPacket();
                if (packet.getMessage().startsWith("/play")) {
                    playCommand = packet.getMessage();
                }
            }
    	}
    }
}
