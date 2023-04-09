package tech.dort.dortware.impl.modules.misc;

import com.google.common.eventbus.Subscribe;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.utils.player.ChatUtil;
import tech.dort.dortware.impl.utils.time.Stopwatch;

public class Spammer extends Module {

    private int index;
    private final EnumValue<Mode> enumValue = new EnumValue<>("Mode", this, Spammer.Mode.values());
    private final NumberValue delay = new NumberValue("Delay", this, 3, 0, 10, true);
    private final BooleanValue antiSpam = new BooleanValue("Anti-Spam", this, false);
    private final Stopwatch stopwatch = new Stopwatch();
    private final String[] jakePaul = {
            "Y'all can't handle this\n",
            "Y'all don't know what's about to happen baby\n",
            "Team 10\n",
            "Los Angeles, Cali boy\n",
            "But I'm from Ohio though, white boy\n",
            "It's everyday bro, with the Disney Channel flow\n",
            "5 mill on YouTube in 6 months, never done before\n",
            "Passed all the competition man, PewDiePie is next\n",
            "Man I'm poppin' all these checks, got a brand new Rolex\n",
            "And I met a Lambo too and I'm coming with the crew\n",
            "This is Team 10, bitch, who the hell are flippin' you?\n",
            "And you know I kick them out if they ain't with the crew\n",
            "Yeah, I'm talking about you, you beggin' for attention\n",
            "Talking shit on Twitter too but you still hit my phone last night\n",
            "It was 4:52 and I got the text to prove\n",
            "And all the recordings too, don't make me tell them the truth\n",
            "And I just dropped some new merch and it's selling like a god, church\n",
            "Ohio's where I'm from, we chew 'em like it's gum\n",
            "We shooting with a gun, the tattoo just for fun\n",
            "I Usain Bolt and run, catch me at game one\n",
            "I cannot be outdone, Jake Paul is number one\n",
            "It's everyday bro\n",
            "It's everyday bro\n",
            "It's everyday bro\n",
            "I said it is everyday bro!\n",
            "You know it's Nick Crompton and my collar stay poppin'\n",
            "Yes, I can rap and no, I am not from Compton\n",
            "England is my city\n",
            "And if it weren't for Team 10, then the US would be shitty\n",
            "I'll pass it to Chance 'cause you know he stay litty\n",
            "Two months ago you didn't know my name\n",
            "And now you want my fame? Bitch I'm blowin' up\n",
            "I'm only going up, now I'm going off, I'm never fallin' off\n",
            "Like Mag, who? Digi who? Who are you?\n",
            "All these beefs I just ran through, hit a milli in a month\n",
            "Where were you? Hatin' on me back in West Fake\n",
            "You need to get your shit straight\n",
            "Jakey brought me to the top, now we're really poppin' off\n",
            "Number one and number four, that's why these fans all at our door\n",
            "It's lonely at the top so we all going\n",
            "We left Ohio, now the trio is all rollin'\n",
            "It's Team 10, bitch\n",
            "We back again, always first, never last\n",
            "We the future, we'll see you in the past\n",
            "It's everyday bro\n",
            "It's everyday bro\n",
            "It's everyday bro\n",
            "I said it is everyday bro!\n",
            "Hold on, hold on, hold on, hold on (espera)\n",
            "Can we switch the language? (Ha, ya tú sabes)\n",
            "We 'bout to hit it (dale)\n",
            "Sí, lo unico que quiero es dinero\n",
            "Trabajando en YouTube todo el día entero\n",
            "Viviendo en U.S.A, el sueño de cualquiera\n",
            "Enviando dólares a mi familia entera\n",
            "Tenemos una persona por encima\n",
            "Se llama Donald Trump y está en la cima\n",
            "Desde aquí te cantamos can I get my VISA?\n",
            "Martinez Twins, representando España\n",
            "Desde la pobreza a la fama\n",
            "It's everyday bro\n",
            "It's everyday bro\n",
            "It's everyday bro\n",
            "I said it is everyday bro!\n",
            "Yo, it's Tessa Brooks\n",
            "The competition shook\n",
            "These guys up on me\n",
            "I got 'em with the hook\n",
            "Lemme educate ya'\n",
            "And I ain't talking book\n",
            "Panera is your home?\n",
            "So, stop calling my phone\n",
            "I'm flyin' like a drone\n",
            "They buying like a loan\n",
            "Yeah, I smell good\n",
            "Is that your boy's cologne?\n",
            "Is that your boy's cologne?\n",
            "Started balling', quicken Loans\n",
            "Now I'm in my flippin' zone\n",
            "Yes, they all copy me\n",
            "But, that's some shitty clones\n",
            "Stay in all designer clothes\n",
            "And they ask me what I make\n",
            "I said is 10 with six zeros\n",
            "Always plug, merch link in bio\n",
            "And I will see you tomorrow 'cause it's everyday bro\n",
            "Peace\n",
            "Ya tu sabes baby, Jake Paul"
    }, lobbyCommands = {"/l s",
            "/l b",
            "/l",
            "/l housing",
            "/l duels",
            "/play pit",
            "/l",
            "/l murder",
            "/l arcade",
            "/l build",
            "/l tnt",
            "/l uhc",
            "/l classic"
    }, rightWingDeathSquads = {"KKK, K-K-K!\n",
            "Triple K Mafia back on the scene.\n",
            "Right Wing Death Squads.\n",
            "Cultural appropriation. All of you niggers, cucks, and liberals, we're sending death squads to the neighborhood after midnight.\n",
            "We're setting a curfew. Stay off the streets you fucking coons.\n",
            "Why would anyone be proud to be black?\n",
            "Your whole fucking race is addicted to crack.\n",
            "I'm Moonman, representing White Power.\n",
            "I stack bodies higher than Trump Tower.\n",
            "Control the memes, control the planet.\n",
            "Fascism is back, and the left can't stand it.\n",
            "Death camps for the blacks, sit back and relax.\n",
            "Right Wing Death Squads prepare to attack.\n",
            "Moonman's dad, back for Round 2.\n",
            "Got a life sentence when I shot a Jew.\n",
            "You deserve death if you voted Ted Cruz,\n",
            "That dumb-ass spic was destined to lose.\n",
            "Name the Jew, blame the Jew,\n",
            "And when you're online, then flame the Jew.\n",
            "After midnight we get revenge.\n",
            "Make America Great Again!\n",
            "Feel the burn, you fucking fags.\n",
            "We put fascist symbols on our flags.\n",
            "To make omelettes, you need to break some eggs.\n",
            "Put refugees in body bags.\n",
            "Open the gates for the caliphate?\n",
            "Blacks fuck your daughters while you masturbate.\n",
            "Feel your fury. Feel your hate.\n",
            "Elevate your mental state.\n",
            "Use ancient runes to curse the coons!\n",
            "You can buy gas masks on Amazon, too.\n",
            "While I don't think my dad is unwise,\n",
            "Conventional methods work best on these flies.\n",
            "You wouldn't kill a roach with a magic spell.\n",
            "Bullets work best for sending niggers to hell.\n",
            "Buy cheap riot gear from online suppliers,\n",
            "Blacks are always bound to start fires.\n",
            "When we roll in your hood with the armored cars,\n",
            "This land is ours! This land is ours!\n",
            "After midnight be ready for a fight,\n",
            "This land is ours! This land is ours!\n",
            "This land is your land, this land is my land.\n",
            "This land is, fortunately, only for the White man.\n",
            "Fuck midnight! If it's half-past nine,\n",
            "That means it's Right Wing Death Squad time!\n",
            "KKK, KKK\n",
            "KKK, K-K-K\n",
            "Fourteen eighty-eight!\n",
            "Triple K Mafia\n"};

    public static String spamString = "Spam!";

    public Spammer(ModuleData moduleData) {
        super(moduleData);
        register(enumValue, delay, antiSpam);
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        if (stopwatch.timeElapsed(enumValue.getValue().equals(Mode.HYPIXEL) ? 150L : (long) (delay.getValue() * 1000L))) {
            switch (enumValue.getValue()) {
                case JAKE_PAUL:
                    try {
                        mc.thePlayer.sendChatMessage(antiSpam.getValue() ? jakePaul[index++] + " - " + RandomStringUtils.randomAlphabetic(16) : jakePaul[index++]);
                    } catch (ArrayIndexOutOfBoundsException exc) {
                        index = 0;
                    }
                    stopwatch.resetTime();
                    break;

                case RIGHT_WING_DEATH_SQUADS:
                    try {
                        mc.thePlayer.sendChatMessage(antiSpam.getValue() ? rightWingDeathSquads[index++] + " - " + RandomStringUtils.randomAlphabetic(16) : rightWingDeathSquads[index++]);
                    } catch (ArrayIndexOutOfBoundsException exc) {
                        index = 0;
                    }
                    stopwatch.resetTime();
                    break;

                case CUSTOM:
                    mc.thePlayer.sendChatMessage(antiSpam.getValue() ? spamString + " - " + RandomStringUtils.randomAlphabetic(16) : spamString);
                    stopwatch.resetTime();
                    break;

                case HYPIXEL:
                    mc.thePlayer.sendChatMessage(lobbyCommands[RandomUtils.nextInt(0, lobbyCommands.length - 1)]);
                    mc.thePlayer.sendChatMessage(antiSpam.getValue() ? spamString + " - " + RandomStringUtils.randomAlphabetic(16) : spamString);
                    stopwatch.resetTime();
                    break;
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        stopwatch.resetTime();
        index = 0;
        ChatUtil.displayChatMessage("Use .spam [message] to edit the spam message.");
    }

    public enum Mode implements INameable {
        CUSTOM("Custom"), JAKE_PAUL("Jake Paul"), RIGHT_WING_DEATH_SQUADS("Moonman"), HYPIXEL("Hypixel Flood");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }
}
