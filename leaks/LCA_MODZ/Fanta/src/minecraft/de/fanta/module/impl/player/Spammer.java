package de.fanta.module.impl.player;

import optifine.MathUtils;
import org.lwjgl.input.Keyboard;



import de.fanta.events.Event;
import de.fanta.events.listeners.EventTick;
import de.fanta.module.Module;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.setting.settings.Slider;
import de.fanta.utils.TimeUtil;

import java.awt.Color;
import java.util.Random;

public class Spammer extends Module {

    private String[] emote = new String[]{"( ͡° ͜ʖ ͡°)", "ಠ_ಠ", "(╯°□°）╯︵ ┻━┻", "┻━┻ ︵ ヽ(°□°ヽ)", "┻━┻ ︵ ＼( °□° )／ ︵ ┻━┻", "┬─┬ノ( º _ ºノ)",
            "(ﾉಥ益ಥ）ﾉ ┻━┻", "ʕノ•ᴥ•ʔノ ︵ ┻━┻", "┻━┻ ︵ヽ(`Д´)ﾉ︵ ┻━┻", "┌∩┐(◣_◢)┌∩┐", "ლ(ಠ益ಠ)ლ", "(ง’̀-‘́)ง", "(ಠ_ಠ)", "╭∩╮（︶︿︶）╭∩╮",
            "( 。・_・。)人(。・_・。 )", "└(^o^ )Ｘ( ^o^)┘", "(✿◠‿◠)", "(｡◕‿◕｡)", "ヽ༼ຈل͜ຈ༽ﾉ", "(づ｡◕‿‿◕｡)づ", "~(˘▾˘~)", "ヘ( ^o^)ノ＼(^_^ )",
            "(. ❛ ᴗ ❛.)", "｡^‿^｡", "( ͡ᵔ ͜ʖ ͡ᵔ )", "☉_☉", "(゜-゜)", "(・_・ヾ", "o_O", "(¬_¬)", "( ͡° ʖ̯ ͡°)", "╮ (. ❛ ᴗ ❛.) ╭", "(•_•) ( •_•)>⌐■-■ (⌐■_■)",
            "(▀̿Ĺ̯▀̿ ̿)", "( ° ͜ʖ͡°)╭∩╮", "( ͡⚆ ͜ʖ ͡⚆)╭∩╮", "(͡• ͜ʖ ͡•)", "╚═( ͡° ͜ʖ ͡°) ═╝", "( ͡°з ͡°)", "͡° ͜ʖ ͡ –", "(͡◔ ͜ʖ ͡◔)",
    };

    private String[] intave = new String[]{"Nice aura checks, do you sell?", "Richiboy ist stur", "Nichts"
            , "Are you serious?", "Intave entfernt für fast 50 Euro alle Legits auf eurem Server!", "NCP > AAC > Alle anderen > Intave"
    };

    private String[] hypixel = new String[]{"Watch out for the dog!", "This game is going to end right now", "Watchdog overlooks me", "I might be banned soon, but not from Watchdog.",
            "I will be banned soon, but not from Watchdog."
    };

    private String[] cubecraft = new String[]{"SeNtInEl Is AlWaYs WaTcHiNg", "Dont stop me now", "Sentinel overlooks me"
    };

    private String[] Fanta = new String[]{"Get Good Get Fanta", "Fanta > FDP/RISE", "FDP Better than ALL!?!?", "Fucked By Fanta Client", "Fanta Client By LCA_MODZ", "deletefdp.today"
    };

    
    public Spammer() {
        super("Spammer", Keyboard.KEY_NONE, Type.Misc, Color.magenta);
        this.settings.add(new Setting("SpammerMode", new DropdownBox("Emote", new String[] {"Fanta","Cubecraft", "Intave", "Hypixel", "Emote"})));
        this.settings.add(new Setting("Delay", new Slider(1, 10000, 1, 100)));
    }

    TimeUtil time = new TimeUtil();

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventTick && e.isPre()) {
            Random rnd = new Random();
            if (time.hasReached((long) ((Slider) this.getSetting("Delay").getSetting()).curValue)) {
                if (((DropdownBox) this.getSetting("SpammerMode").getSetting()).curOption == "Emote") {
                    mc.thePlayer.sendChatMessage(emote[rnd.nextInt(emote.length)]);
                } else if (((DropdownBox) this.getSetting("SpammerMode").getSetting()).curOption == "Intave") {
                    mc.thePlayer.sendChatMessage(intave[rnd.nextInt(intave.length)]);
                } else if (((DropdownBox) this.getSetting("SpammerMode").getSetting()).curOption == "Cubecraft") {
                    mc.thePlayer.sendChatMessage(cubecraft[rnd.nextInt(cubecraft.length)]);
                } else if (((DropdownBox) this.getSetting("SpammerMode").getSetting()).curOption == "Hypixel") {
                    mc.thePlayer.sendChatMessage(hypixel[rnd.nextInt(hypixel.length)]);
                }   else if (((DropdownBox) this.getSetting("SpammerMode").getSetting()).curOption == "Fanta") {
                    mc.thePlayer.sendChatMessage(Fanta[rnd.nextInt(Fanta.length)]);
                }
                time.reset();
            }
        }
    }
}
