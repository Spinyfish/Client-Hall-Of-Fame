package crispy.features.hacks;

import crispy.Crispy;
import crispy.features.event.Event;
import crispy.features.hacks.impl.render.HUD;
import crispy.util.animation.Translate;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

import static crispy.features.hacks.impl.combat.Aura.randomNumber;


@Getter
public abstract class Hack {
    @Setter
    private String displayName;
    private final String name;
    @Setter
    private int key;
    private final Category category;
    private boolean enabled;
    private final Translate gui = new Translate(0, 0);

    @Setter
    private Translate novoGui = new Translate(0,0);

    @Setter
    private boolean expanded;

    public int color = new Color(randomNumber(255, 0), randomNumber(255, 0), randomNumber(255, 0)).getRGB();
    private final Translate translate = new Translate(0, 0);

    public Hack() {
        HackInfo hackInfo = getHackInfo();
        name = hackInfo.name();
        displayName = name;
        key = hackInfo.key();
        category = hackInfo.category();
    }

    public Hack(String name, String desc, Category category) {
        this.name = name;
        this.category = category;
        this.displayName = name;
    }


    public HackInfo getHackInfo() {
        if (this.getClass().isAnnotationPresent(HackInfo.class)) {
            return this.getClass().getAnnotation(HackInfo.class);
        } else {
            System.err.println("HackInfo was not set from class " + this.getClass().getSimpleName() + ".");
        }
        return null;
    }

    public void onEnable() {

    }

    public void onDisable() {

    }

    public void toggle() {
        HUD hud = Crispy.INSTANCE.getHackManager().getHack(HUD.class);
        enabled = !enabled;
        if (!enabled) {
            if (hud.toggleSounds.getMode().equalsIgnoreCase("Jello")) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("jelloOff")));
            } else if (hud.toggleSounds.getMode().equalsIgnoreCase("Click")) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), (float) .7));
            }
            onDisable();
        } else {
            if (hud.toggleSounds.getMode().equalsIgnoreCase("Jello")) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("jelloOn")));
            } else if (hud.toggleSounds.getMode().equalsIgnoreCase("Click")) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), (float) .9));
            }
            onEnable();
        }
    }

    protected Minecraft minecraft = Minecraft.getMinecraft();
    protected Minecraft mc = Minecraft.getMinecraft();

    public Minecraft mc() {
        return Minecraft.getMinecraft();
    }

    public abstract void onEvent(Event e);

    public void setState(boolean state) {
        if (state) {
            this.enabled = true;

            onEnable();
        } else {
            this.enabled = false;
            onDisable();
        }
    }
}