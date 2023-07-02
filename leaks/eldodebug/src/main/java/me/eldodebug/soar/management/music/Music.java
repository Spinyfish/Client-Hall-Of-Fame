package me.eldodebug.soar.management.music;

import java.io.File;

import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.SceneBuilder;
import javafx.scene.control.LabelBuilder;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.StageBuilder;
import javafx.stage.WindowEvent;
import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.impl.ClientMod;
import me.eldodebug.soar.management.mods.impl.MusicInfoMod;
import net.minecraft.client.Minecraft;

@SuppressWarnings("deprecation")
public class Music {
	
	private String name, path;

	private Media media;
	
	public MediaPlayer mediaPlayer;
	
	public Music(String name, String path) {
		this.name = name;
		this.path = path;
	}
	
	public void playAsyncMusic() {
		new Thread() {
			@Override
			public void run() {
				playMusic();
			}
		}.start();
	}
	
	public void playMusic() {
		
		if(Soar.instance.modManager.getModByName("Music Info").isToggled()) {
			MusicInfoMod.instance.addX = 0;
		}
		
		SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JFXPanel();
                Platform.runLater(new Runnable() {
					@Override
                    public void run() {
                        StageBuilder.create()
                                .scene(SceneBuilder.create()
                                        .width(320)
                                        .height(240)
                                        .root(LabelBuilder.create()
                                                .font(new javafx.scene.text.Font("Arial", 54d))
                                                .text("Music Player")
                                                .build())
                                        .build())
                                .onCloseRequest(new EventHandler<WindowEvent>() {
                                    @Override
                                    public void handle(WindowEvent windowEvent) {
                                        System.exit(0);
                                    }
                                })
                                .build();
                    }
                });
            }
        });
		
		media = new Media(new File(Minecraft.getMinecraft().mcDataDir, "soar/music/" + path).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setAudioSpectrumInterval(0.06);
		mediaPlayer.setAudioSpectrumListener(new AudioSpectrumListener() {

			@Override
			public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
				for(int i = 0; i < magnitudes.length; i++) {
					MusicInfoMod.visualizer[i] = (float) ((magnitudes[i] + 60) * (-1.17));
				}
			}
			
		});
		
		mediaPlayer.play();
		setVolume();
	}
	
	public void setVolume() {
		mediaPlayer.setVolume(Soar.instance.settingsManager.getSettingByClass(ClientMod.class, "Volume").getValDouble());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
	}
}
