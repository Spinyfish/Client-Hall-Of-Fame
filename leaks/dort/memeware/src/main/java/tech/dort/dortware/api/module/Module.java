package tech.dort.dortware.api.module;

import net.minecraft.client.Minecraft;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.file.MFile;
import tech.dort.dortware.api.module.interfaces.IToggleable;
import tech.dort.dortware.api.property.Value;

import java.awt.*;
import java.util.Collections;
import java.util.Random;

/**
 * @author Dort
 */
public abstract class Module implements IToggleable {

    protected static final Minecraft mc = Minecraft.getMinecraft();
    protected static final Random RANDOM = new Random();

    private final ModuleData moduleData;
    private final int color;
    private boolean toggled;
    private int keyBind;

    public Module(ModuleData moduleData) {
        this.moduleData = moduleData;
        float r = RANDOM.nextFloat() / 2 + 0.5F;
        float g = RANDOM.nextFloat() / 2 + 0.5F;
        float b = RANDOM.nextFloat() / 2 + 0.5F;
        color = new Color(r, g, b).getRGB();
        keyBind = moduleData.getDefaultBind();
    }

    public ModuleData getModuleData() {
        return moduleData;
    }

    public int getKeyBind() {
        return keyBind;
    }

    public void setKeyBind(int keyBind) {
        this.keyBind = keyBind;
        Client.INSTANCE.getFileManager().getObjects().forEach(MFile::save);
    }

    public void setKeyBindNoCall(int keyBind) {
        this.keyBind = keyBind;
    }

    @Override
    public void toggle() {
        toggled = !toggled;
        mc.timer.timerSpeed = 1F;
        if (toggled) {
            Client.INSTANCE.getEventBus().register(this);
            onEnable();
        } else {
            Client.INSTANCE.getEventBus().unregister(this);
            onDisable();
            mc.thePlayer.speedInAir = 0.02f;
            mc.thePlayer.setBlocking(false);
        }
        onToggled();

    }

    @Override
    public void onToggled() {
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    public boolean isToggled() {
        return toggled;
    }

    public String getSuffix() {
        return null;
    }

    public void register(Value... properties) {
        Collections.addAll(Client.INSTANCE.getValueManager().getObjects(), properties);
    }


    public int getColor() {
        return color;
    }
}
