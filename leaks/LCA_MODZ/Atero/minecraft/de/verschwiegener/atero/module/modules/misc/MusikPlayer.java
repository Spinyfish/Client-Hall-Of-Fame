package de.verschwiegener.atero.module.modules.misc;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.audio.Streamer;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.settings.Setting;
import de.verschwiegener.atero.settings.SettingsItem;
import de.verschwiegener.atero.ui.audio.AudioPanel;
import de.verschwiegener.atero.ui.clickgui.ClickGUI;
import de.verschwiegener.atero.ui.clickgui.component.components.ComponentCheckBox;
import de.verschwiegener.atero.util.render.ShaderRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class MusikPlayer extends Module {

    public MusikPlayer() {
	super("MusikPlayer", "Plays Musik", Keyboard.KEY_NONE, Category.Misc);
    }

    @Override
    public void onEnable() {
	super.onEnable();
	Minecraft.getMinecraft().displayGuiScreen(new AudioPanel());
	toggle();
    }
}
