package org.crystalpvp.anticheat.api.config;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

@Getter
public class FileConfig {

	private File file;
	private FileConfiguration config;

	public FileConfig(JavaPlugin plugin, String fileName) {
		this.file = new File(plugin.getDataFolder(), fileName);

		if (!this.file.exists()) {
			this.file.getParentFile().mkdirs();

			if (plugin.getResource(fileName) == null) {
				try {
					this.file.createNewFile();
				} catch (IOException e) {
					plugin.getLogger().severe("Failed to create new file " + fileName);
					return;
				}
			} else {
				plugin.saveResource(fileName, false);
			}
		}

		this.config = YamlConfiguration.loadConfiguration(this.file);
	}

	public void save() {
		try {
			this.config.save(this.file);
		} catch (IOException e) {
			Bukkit.getLogger().severe("Could not save config file " + this.file.toString());
			e.printStackTrace();
		}
	}
}