package me.eldodebug.soar.management.file;

import java.io.File;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.utils.FileUtils;
import net.minecraft.client.Minecraft;

public class FileManager {

	private Minecraft mc = Minecraft.getMinecraft();
	
	//Directory
	private File soarDir;
	private File tempDir;
	private File imageDir;
	private File musicDir;
	private File configDir;
	
	//File
	private File configFile;
	private File versionFile;
	private File accountFile;
	
	public FileManager() {
		
		soarDir = new File(mc.mcDataDir, "soar");
		tempDir = new File(soarDir, "temp");
		imageDir = new File(soarDir, "image");
		musicDir = new File(soarDir, "music");
		configDir = new File(soarDir, "config");
		
		configFile = new File(soarDir, "Config.txt");
		accountFile = new File(soarDir, "Accounts.txt");
		versionFile = new File(tempDir, Soar.instance.getVersion() + ".ver");
		
		FileUtils.createDir(soarDir);
		FileUtils.createDir(tempDir);
		FileUtils.createDir(imageDir);
		FileUtils.createDir(musicDir);
		FileUtils.createDir(configDir);
		
		FileUtils.createFile(configFile);
		FileUtils.createFile(accountFile);
		FileUtils.createFile(versionFile);
	}

	public File getSoarDir() {
		return soarDir;
	}

	public File getConfigFile() {
		return configFile;
	}

	public File getTempDir() {
		return tempDir;
	}

	public File getVersionFile() {
		return versionFile;
	}

	public File getImageDir() {
		return imageDir;
	}

	public File getMusicDir() {
		return musicDir;
	}

	public File getAccountFile() {
		return accountFile;
	}

	public File getConfigDir() {
		return configDir;
	}
}
