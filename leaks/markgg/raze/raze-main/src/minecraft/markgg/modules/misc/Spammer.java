package markgg.modules.misc;

import java.util.ArrayList;
import java.util.Random;

import markgg.events.Event;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;
import markgg.modules.Module.Category;
import markgg.settings.ModeSetting;
import markgg.settings.NumberSetting;
import markgg.util.RandomUtil;
import markgg.util.Timer;

public class Spammer extends Module {

	public ModeSetting mode = new ModeSetting("Mode", this, "Default", "Default", "Bypass", "Communism");
	public NumberSetting delay = new NumberSetting("Delay", this, 10, 1, 300, 1);
	private Timer timer = new Timer();
	
	public Spammer() {
		super("Spammer", "Spam chat", 0, Category.MISC);
		addSettings(mode,delay);
	}
	
	public void onEvent(Event e){
		if(e instanceof EventUpdate) {
			if(mode.is("Default") && timer.hasTimeElapsed((long) (delay.getValue() * 1000), true)) {
				mc.thePlayer.sendChatMessage(getDefaultMessages());
			}else if(mode.is("Bypass") && timer.hasTimeElapsed((long) (delay.getValue() * 1000), true)) {
				mc.thePlayer.sendChatMessage(getCommunismMessages());
			}else if(mode.is("Communism") && timer.hasTimeElapsed((long) (delay.getValue() * 1000), true)) {
				mc.thePlayer.sendChatMessage(getBypassMessage());
			}
		}
	}

	public static String getDefaultMessages() {
		ArrayList<String> message = new ArrayList<>();

		message.add("I have the greatest gaming chair - discord.gg/VbJMMwkuMm");
		message.add("raze helped me get a girlfriend");
		message.add("raze  raze  raze  raze ");
		message.add("The world's best PVP Client compared to Lunar!");
		message.add("Hawk? More like Raze");
		message.add("LiquidBounce? More like LiquidBAD! RAZE ON TOP");
		message.add("FDP CLIENT LMAO WTF - RAZE ON TOP");
		message.add("without kants rat");
		message.add("Kellohylly rated 999/10 <3");
		message.add("Others say 1/10, but FUCK them! - discord.gg/VbJMMwkuMm");
		message.add("I stuck Raze into my version folder discord.gg/VbJMMwkuMm");
		message.add("0zg rates it 100/10! discord.gg/VbJMMwkuMm");
		message.add("Bloksteri uses raze!");
		message.add("Technoblade? more like technoBAD - RAZEEEEE");
		message.add("discord.gg/VbJMMwkuMm");
		message.add("lolololololooloolollolololololololololololololollolololol");
		message.add("gamer client go brrr");
		message.add("completely clean code - discord.gg/VbJMMwkuMm");
		message.add("atleast mine is not skidded!");

		Random random = new Random();
		int randomIndex = (int)(Math.random() * message.size());
		return message.get(randomIndex);
	}
	
	public static String getCommunismMessages() {
		ArrayList<String> message = new ArrayList<>();

	    message.add("communism isnt that bad, bring it back!");
	    message.add("help me bring back communism!");
	    message.add("imagine not liking communism");
	    message.add("Best verus bypasses since 2022, powered by Communism!");
	    message.add("Verus? More like shittus");
	    message.add("This client is sponsored by Da Baby");
	    message.add("no skidded lines on this");
	    message.add("Imagine losing to communists");
	    message.add("Cuba is the best!!");
	    message.add("discord.gg/VbJMMwkuMm WAZ LOST?");
	    message.add("Support our local furry zoos - discord.gg/VbJMMwkuMm");
	    message.add("Communism is getting rebranded!");
	    
		Random random = new Random();
		int randomIndex = (int)(Math.random() * message.size());
		return message.get(randomIndex);
	}
	
	public static String getBypassMessage() {
		ArrayList<String> message = new ArrayList<>();

	    message.add(RandomUtil.generateRandomLine() + "fdp sucks ass");
	    message.add(RandomUtil.generateRandomLine() + "raze is actually free!");
	    message.add(RandomUtil.generateRandomLine() + "imagine using fdp :clown:");
	    message.add(RandomUtil.generateRandomLine() + "Best vulcan client Razeeeee");
	    message.add(RandomUtil.generateRandomLine() + "Anticheat got destroyed like your a*s");
	    message.add(RandomUtil.generateRandomLine() + "This client is owned by 2 people");
	    message.add(RandomUtil.generateRandomLine() + "Atleast mine is bigger!");
	    message.add(RandomUtil.generateRandomLine() + "Imagine paying taxes");
	    message.add(RandomUtil.generateRandomLine() + "Stop using Lunar and get Raze!");
	    message.add(RandomUtil.generateRandomLine() + "discord.gg/VbJMMwkuMm");
	    message.add(RandomUtil.generateRandomLine() + "discord.gg/VbJMMwkuMm");
	    message.add(RandomUtil.generateRandomLine() + "discord.gg/VbJMMwkuMm");
	    message.add(RandomUtil.generateRandomLine() + "discord.gg/VbJMMwkuMm");
	    message.add(RandomUtil.generateRandomLine() + "get raze - discord.gg/VbJMMwkuMm");
	    message.add(RandomUtil.generateRandomLine() + "raze on top m*f*s!!");
	    
		Random random = new Random();
		int randomIndex = (int)(Math.random() * message.size());
		return message.get(randomIndex);
	}

}
