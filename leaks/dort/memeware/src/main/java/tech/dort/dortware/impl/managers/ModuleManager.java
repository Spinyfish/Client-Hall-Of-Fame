package tech.dort.dortware.impl.managers;

import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Keyboard;
import tech.dort.dortware.api.font.CustomFontRenderer;
import tech.dort.dortware.api.manager.Manager;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.module.enums.ModuleCategory;
import tech.dort.dortware.impl.modules.combat.*;
import tech.dort.dortware.impl.modules.exploit.*;
import tech.dort.dortware.impl.modules.misc.*;
import tech.dort.dortware.impl.modules.movement.*;
import tech.dort.dortware.impl.modules.player.*;
import tech.dort.dortware.impl.modules.render.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ModuleManager extends Manager<Module> {

    public ModuleManager() {
        super(new ArrayList<>());
    }

    public Module getByName(String name) {
        for (Module module : this.getObjects())
            if (module.getModuleData().getName().equals(name)) return module;
        throw new NoSuchElementException("Retard Exception: No module found");
    }

    public Module getByNameIgnoreSpaceCaseInsensitive(String name) {
        for (Module module : this.getObjects())
            if (module.getModuleData().getName().replace(" ", "").equalsIgnoreCase(name)) return module;
        throw new NoSuchElementException("Retard Exception: No module found");
    }

    /**
     * Sorts this manager by it's list's objects name string length
     *
     * @param fontRenderer - The {@code FontRenderer} used for obtaining the length
     */
    public void sort(FontRenderer fontRenderer) {
        getObjects().sort((a, b) -> {
            String dataA = a.getSuffix() == null ? "" : a.getSuffix();
            String dataB = b.getSuffix() == null ? "" : b.getSuffix();
            int first = fontRenderer.getStringWidth(a.getModuleData().getName() + dataA);
            int second = fontRenderer.getStringWidth(b.getModuleData().getName() + dataB);
            return second - first;
        });
    }

    public List<Module> getModulesSortedAlternative(CustomFontRenderer customFontRenderer) { // TODO make this shit into 1 method
        List<Module> moduleList = new ArrayList<>(getObjects());
        moduleList.sort((a, b) -> {
            String dataA = a.getSuffix() == null ? "" : a.getSuffix();
            String dataB = b.getSuffix() == null ? "" : b.getSuffix();
            String nameA = a.getModuleData().hasOtherName() ? a.getModuleData().getOtherName() : a.getModuleData().getName();
            String nameB = b.getModuleData().hasOtherName() ? b.getModuleData().getOtherName() : b.getModuleData().getName();

            int first = (int) customFontRenderer.getWidth(nameA + dataA);
            int second = (int) customFontRenderer.getWidth(nameB + dataB);
            return second - first;
        });
        return moduleList;
    }

    public List<Module> getModulesSortedAlternativeDumb(CustomFontRenderer customFontRenderer) {
        List<Module> moduleList = new ArrayList<>(getObjects());
        moduleList.sort((a, b) -> {
            String dataA = a.getSuffix() == null ? "" : a.getSuffix();
            String dataB = b.getSuffix() == null ? "" : b.getSuffix();
            String nameA = a.getModuleData().hasOtherNameDumb() ? a.getModuleData().getOtherNameDumb() : a.getModuleData().getName();
            String nameB = b.getModuleData().hasOtherNameDumb() ? b.getModuleData().getOtherNameDumb() : b.getModuleData().getName();

            int first = (int) customFontRenderer.getWidth(nameA + dataA);
            int second = (int) customFontRenderer.getWidth(nameB + dataB);
            return second - first;
        });
        return moduleList;
    }

    public List<Module> getModulesSortedAlternativeDumbNoFont(FontRenderer fontRenderer) {
        List<Module> moduleList = new ArrayList<>(getObjects());
        moduleList.sort((a, b) -> {
            String dataA = a.getSuffix() == null ? "" : a.getSuffix();
            String dataB = b.getSuffix() == null ? "" : b.getSuffix();
            String nameA = a.getModuleData().hasOtherNameDumb() ? a.getModuleData().getOtherNameDumb() : a.getModuleData().getName();
            String nameB = b.getModuleData().hasOtherNameDumb() ? b.getModuleData().getOtherNameDumb() : b.getModuleData().getName();

            int first = fontRenderer.getStringWidth(nameA + dataA);
            int second = fontRenderer.getStringWidth(nameB + dataB);
            return second - first;
        });
        return moduleList;
    }

    public List<Module> getModulesSortedAlternativeNoFont(FontRenderer fontRenderer) {
        List<Module> moduleList = new ArrayList<>(getObjects());
        moduleList.sort((a, b) -> {
            String dataA = a.getSuffix() == null ? "" : a.getSuffix();
            String dataB = b.getSuffix() == null ? "" : b.getSuffix();
            String nameA = a.getModuleData().hasOtherName() ? a.getModuleData().getOtherName() : a.getModuleData().getName();
            String nameB = b.getModuleData().hasOtherName() ? b.getModuleData().getOtherName() : b.getModuleData().getName();

            int first = fontRenderer.getStringWidth(nameA + dataA);
            int second = fontRenderer.getStringWidth(nameB + dataB);
            return second - first;
        });
        return moduleList;
    }

    /**
     * Sorts this manager by its list's objects name string length
     *
     * @param customFontRenderer - The {@code CustomFontRenderer} used for obtaining the length
     */
    public void sort(CustomFontRenderer customFontRenderer) {
        getObjects().sort((a, b) -> {
            String dataA = a.getSuffix() == null ? "" : a.getSuffix();
            String dataB = b.getSuffix() == null ? "" : b.getSuffix();
            String nameA = a.getModuleData().getName();
            String nameB = b.getModuleData().getName();

            int first = (int) customFontRenderer.getWidth(nameA + dataA);
            int second = (int) customFontRenderer.getWidth(nameB + dataB);
            return second - first;
        });
    }

    /**
     * Sorts this manager by its list's objects name string length with no suffix
     *
     * @param customFontRenderer - The {@code CustomFontRenderer} used for obtaining the length
     */
    public void sortNoSuffix(CustomFontRenderer customFontRenderer) {
        getObjects().sort((a, b) -> {
            String nameA = a.getModuleData().getName();
            String nameB = b.getModuleData().getName();

            int first = (int) customFontRenderer.getWidth(nameA);
            int second = (int) customFontRenderer.getWidth(nameB);
            return second - first;
        });
    }

    @Override
    public void onCreated() {
        // COMBAT
        add(new KillAura(new ModuleData("Kill Aura", "KFC Nigger Slayer", "Minorities Run Over", 0, ModuleCategory.COMBAT)));
//        add(new AutoUse(new ModuleData("Auto Use", 0, ModuleCategory.COMBAT)));
        add(new TPAura(new ModuleData("TP Aura", 0, ModuleCategory.COMBAT)));
        add(new Velocity(new ModuleData("Velocity", "Weight Gain 5000", "No Hit Move", 0, ModuleCategory.COMBAT)));
        add(new AutoSoup(new ModuleData("Auto Soup", "Bat Soup", "Soup Eater", 0, ModuleCategory.COMBAT)));
        add(new AutoArmor(new ModuleData("Auto Armor", "Auto Antifa Prot", "Armorer", 0, ModuleCategory.COMBAT)));
        add(new TargetStrafe(new ModuleData("Target Strafe", "Nigger Spinner", "Nigger Spinner", 0, ModuleCategory.COMBAT)));
        add(new FastBow(new ModuleData("Fast Bow", "Columbine Harvester", "Speedy Bow", 0, ModuleCategory.COMBAT)));
        add(new Criticals(new ModuleData("Criticals", "Police Brutality", "Oversized Truck Driver", 0, ModuleCategory.COMBAT)));
        add(new AntiBot(new ModuleData("Anti Bot", "Anti Nigger", "Flies Remover", 0, ModuleCategory.COMBAT)));
        add(new AutoPot(new ModuleData("Auto Pot", 0, ModuleCategory.COMBAT)));
        add(new AutoHead(new ModuleData("Auto Head", "Give Me Head", "Head Eater", 0, ModuleCategory.COMBAT)));
//        add(new AutoApple(new ModuleData("Auto Apple", 0, ModuleCategory.COMBAT)));
        add(new Regen(new ModuleData("Fast Heal", 0, ModuleCategory.COMBAT)));
        add(new AimBot(new ModuleData("Aim Bot", 0, ModuleCategory.COMBAT)));

        // MOVEMENT
        add(new NoSlow(new ModuleData("No Slowdown", "Blacks Leaving Son Inator", "Weird Hands", 0, ModuleCategory.MOVEMENT)));
        add(new Flight(new ModuleData("Flight", "Jews When They See Money", "Drug Consumer", 0, ModuleCategory.MOVEMENT)));
        add(new LongJump(new ModuleData("Long Jump", "Faggot Flinger", "Long Legs", 0, ModuleCategory.MOVEMENT)));
        add(new Speed(new ModuleData("Speed", "Border Hopper", "Bigfoot", 0, ModuleCategory.MOVEMENT)));
        add(new Strafe(new ModuleData("Strafe", "Bullet Dodger", "Fast Mover", 0, ModuleCategory.MOVEMENT)));
        add(new Sprint(new ModuleData("Sprint", "Chink Walk", "Naruto Run", 0, ModuleCategory.MOVEMENT)));
        add(new InvMove(new ModuleData("Inventory Move", 0, ModuleCategory.MOVEMENT)));
        add(new Step(new ModuleData("Step", "Tall Guy", "Auto Jump", 0, ModuleCategory.MOVEMENT)));
        add(new NoVoid(new ModuleData("No Void", 0, ModuleCategory.MOVEMENT)));
        add(new Jesus(new ModuleData("Jesus", "Jesus is Real", "Piece of Plastic", 0, ModuleCategory.MOVEMENT)));
        add(new AirJump(new ModuleData("Air Jump", 0, ModuleCategory.MOVEMENT)));
        add(new FastLadder(new ModuleData("Fast Ladder", 0, ModuleCategory.MOVEMENT)));
        add(new CustomSpeed(new ModuleData("Custom Speed", 0, ModuleCategory.MOVEMENT)));
        add(new CustomFly(new ModuleData("Custom Fly", 0, ModuleCategory.MOVEMENT)));
        add(new Glide(new ModuleData("Glide", 0, ModuleCategory.MOVEMENT)));
        add(new Teleport(new ModuleData("Teleport", 0, ModuleCategory.MOVEMENT)));

        // PLAYER
        add(new Derp(new ModuleData("Derp", "Brain Fard", "Noob Mode", 0, ModuleCategory.PLAYER)));
        add(new Breaker(new ModuleData("Breaker", "Border Breaker", "Auto Bed", 0, ModuleCategory.PLAYER)));
        add(new Scaffold(new ModuleData("Scaffold", "Swat Evasion", "Cock Fly", 0, ModuleCategory.PLAYER)));
        add(new Sneak(new ModuleData("Sneak", "Robbery", "Robbery", 0, ModuleCategory.PLAYER)));
        add(new Stealer(new ModuleData("Stealer", "Steal More Cookies U Fat Fuck", "Auto Rob", 0, ModuleCategory.PLAYER)));
        add(new tech.dort.dortware.impl.modules.player.InventoryManager(new ModuleData("Inv Manager", 0, ModuleCategory.PLAYER)));
        add(new NoFall(new ModuleData("No Fall", "Ass So Big No Fall Damage", "Fall Damage", 0, ModuleCategory.PLAYER)));
        add(new FastInteract(new ModuleData("Fast Interact", 0, ModuleCategory.PLAYER)));
        add(new FastUse(new ModuleData("Fast Use", 0, ModuleCategory.PLAYER)));
        add(new AutoTool(new ModuleData("Auto Tool", 0, ModuleCategory.PLAYER)));
        add(new Nuker(new ModuleData("Nuker", 0, ModuleCategory.PLAYER)));

        // MISC
        add(new ChatBypass(new ModuleData("Chat Bypass", 0, ModuleCategory.MISC)));
        add(new KillInsult(new ModuleData("Kill Insult", "Child Roaster", "Auto Roast", 0, ModuleCategory.MISC)));
        add(new Commands(new ModuleData("Commands", 0, ModuleCategory.MISC)));
        add(new NameProtect(new ModuleData("Name Protect", "new op start!!", "Name Hider", 0, ModuleCategory.MISC)));
        add(new AutoHypixel(new ModuleData("Auto Hypixel", 0, ModuleCategory.MISC)));
        add(new LeetSpeak(new ModuleData("Leet Speak", 0, ModuleCategory.MISC)));
        add(new Timer(new ModuleData("Timer", 0, ModuleCategory.MISC)));
        add(new Blink(new ModuleData("Blink", 0, ModuleCategory.MISC)));
        add(new Panic(new ModuleData("Panic", 0, ModuleCategory.MISC)));
        add(new Spammer(new ModuleData("Spammer", 0, ModuleCategory.MISC)));
        add(new MCF(new ModuleData("MCF", 0, ModuleCategory.MISC)));
        add(new HackerDetect(new ModuleData("Hacker Detect", 0, ModuleCategory.MISC)));

        // RENDER
        add(new Hud(new ModuleData("HUD", 0, ModuleCategory.RENDER)));
        add(new Tags(new ModuleData("Tags", 0, ModuleCategory.RENDER)));
        add(new Ambience(new ModuleData("Ambience", "Spooky Dark Time", "Time Editor", 0, ModuleCategory.RENDER)));
        add(new ESP(new ModuleData("ESP", "Nibba Finder", "Player Outliner", 0, ModuleCategory.RENDER)));
        add(new Chams(new ModuleData("Chams", "The CIA Niggers Glow In The Dark", "Player xRay", 0, ModuleCategory.RENDER)));
        add(new Animations(new ModuleData("Animations", 0, ModuleCategory.RENDER)));
        add(new FullBright(new ModuleData("Fullbright", "The sun is a deadly laser", "Auto Bright", 0, ModuleCategory.RENDER)));
        add(new ClickGUI(new ModuleData("Click UI", Keyboard.KEY_RSHIFT, ModuleCategory.RENDER)));
        add(new ViewBobbing(new ModuleData("View Bobbing", "Frank Bobbing", "Big Bob", 0, ModuleCategory.RENDER)));
        add(new Rotate(new ModuleData("Rotate", 0, ModuleCategory.RENDER)));
        add(new MoreParticles(new ModuleData("More Particles", 0, ModuleCategory.RENDER)));
        add(new ChestESP(new ModuleData("Chest ESP", 0, ModuleCategory.RENDER)));
        add(new Camera(new ModuleData("Camera", 0, ModuleCategory.RENDER)));
        add(new FreeCam(new ModuleData("Free Cam", 0, ModuleCategory.RENDER)));
        add(new TrueSight(new ModuleData("True Sight", 0, ModuleCategory.RENDER)));
        add(new HurtColor(new ModuleData("Hurt Color", 0, ModuleCategory.RENDER)));
        add(new ZamboHat(new ModuleData("Zambo Hat", 0, ModuleCategory.RENDER)));

        // EXPLOIT
        add(new Crasher(new ModuleData("Crasher", "Allah Akbar", "Australia Simulator", 0, ModuleCategory.EXPLOIT)));
        add(new PotionSaver(new ModuleData("Potion Saver", 0, ModuleCategory.EXPLOIT)));
        add(new InfiniteDurability(new ModuleData("Infinite Durability", 0, ModuleCategory.EXPLOIT)));
        add(new Phase(new ModuleData("Phase", 0, ModuleCategory.EXPLOIT)));
        add(new PingSpoof(new ModuleData("Ping Spoof", 0, ModuleCategory.EXPLOIT)));
        add(new NoRotate(new ModuleData("No Rotate", 0, ModuleCategory.EXPLOIT)));
        add(new Disabler(new ModuleData("Disabler", "Anti-Cheat Deleter", "Anti Anti-Cheat", 0, ModuleCategory.EXPLOIT)));
        add(new LunarClientSpoofer(new ModuleData("Lunar Spoofer", 0, ModuleCategory.EXPLOIT)));
        add(new AntiTabComplete(new ModuleData("Anti Tab Complete", 0, ModuleCategory.EXPLOIT)));
        add(new Vanish(new ModuleData("Entity Disabler", 0, ModuleCategory.EXPLOIT)));
        add(new Zoot(new ModuleData("Zoot", 0, ModuleCategory.EXPLOIT)));
        add(new SpamTP(new ModuleData("Spam Teleport", 0, ModuleCategory.EXPLOIT)));


        // HIDDEN
        super.get(Hud.class).toggle();
        super.get(Sprint.class).toggle();
        super.get(Commands.class).toggle();
    }

    public List<Module> getAllInCategory(ModuleCategory category) {
        List<Module> list = new ArrayList<>();
        for (Module module : getObjects()) {
            if (module.getModuleData().category() == category) {
                list.add(module);
            }
        }
        return list;
    }

    public boolean isEnabled(Class<? extends Module> moduleClass) {
        return get(moduleClass).isToggled();
    }
}
