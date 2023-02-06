package dev.elysium.client.extensions;

import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.*;
import dev.elysium.client.Elysium;
import dev.elysium.client.utils.AES;
import dev.elysium.client.utils.render.BColor;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.io.*;

public class ConfigManager {
	public static Minecraft mc = Minecraft.getMinecraft();

	public static void createConfigDirectories() {
		try {
			String clientPath = mc.mcDataDir.getCanonicalPath().concat("\\elysium\\");

			if(!(new File(clientPath).exists())) {
				new File(clientPath).mkdir();
				System.out.println("[CONFIG] created folder 'elysium' in ".concat(mc.mcDataDir.getCanonicalPath()));
			}

			if(!(new File(clientPath.concat("configs\\")).exists())) {
				new File(clientPath.concat("configs\\")).mkdir();
				System.out.println("[CONFIG] created folder 'elysium\\configs' in ".concat(mc.mcDataDir.getCanonicalPath()));
			}

			if(!(new File(clientPath.concat("defaultconfig.elysium")).exists())) {
				new File(clientPath.concat("defaultconfig.elysium")).createNewFile();
				System.out.println("[CONFIG] created file 'elysium\\defaultconfig.elysium' in ".concat(mc.mcDataDir.getCanonicalPath()));
			}
			if(!(new File(clientPath.concat("saveduser.elysium")).exists())) {
				new File(clientPath.concat("saveduser.elysium")).createNewFile();
				System.out.println("[CONFIG] created file 'elysium\\saveduser.elysium' in ".concat(mc.mcDataDir.getCanonicalPath()));
			}
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
	}

	static String key = "SUPER_SECRET_KEY_ARAB_IS_GAY_CHANGE_THAT_LATER";

	public static void saveLastUser(String name, String token, String pid) {
		System.out.println("[CONFIG] saving last user data");

		String clientPath;
		try {
			clientPath = mc.mcDataDir.getCanonicalPath().concat("\\elysium\\");

			File file = new File(clientPath + "saveduser" + ".elysium");
			if(!file.exists())
				file.createNewFile();

			FileWriter f = new FileWriter(clientPath + "saveduser" + ".elysium");

			AES.setKey(key);
			name = AES.encrypt(name, key);
			token = AES.encrypt(token, key);
			pid = AES.encrypt(pid, key);

			f.write(name + ":SEP:" + token + ":SEP:" + pid);

			f.close();
		} catch (IOException e) {
			e.printStackTrace();
			Elysium.getInstance().addChatMessageConfig("falied to write config!");
		}
	}

	public static String[] getData() {
		System.out.println("[CONFIG] getting data of ".concat("saveduser"));

		try {
			String clientPath = mc.mcDataDir.getCanonicalPath().concat("\\elysium\\");
			File cong = new File(clientPath.concat("saveduser" + ".elysium"));

			if(!cong.exists()) {
				System.out.println("[CONFIG] ERROR config named " + "saveduser" + " does not exist");

				return new String[] {"UNKNOWN", "UNKNOWN", "UNKNOWN"};
			}

			FileReader fr = new FileReader(cong);

			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(fr);
			StringBuffer sb= new StringBuffer();

			String line;

			while((line=br.readLine())!=null)
			{
				br.close();
				fr.close();
				String s1 = line.split(":SEP:")[0];
				String s2 = line.split(":SEP:")[1];
				String s3 = line.split(":SEP:")[2];
				AES.setKey(key);
				s1 = AES.decrypt(s1, key);
				s2 = AES.decrypt(s2, key);
				s3 = AES.decrypt(s3, key);
				return new String[] {s1, s2, s3};
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("[CONFIG] falied to read data of " + "saveduser");
		}

		System.out.println("[CONFIG] falied to read data of " + "saveduser");
		return new String[] {"UNKNOWN", "UNKNOWN"};
	}

	public static void save(String config) {
		Elysium.getInstance().addChatMessageConfig("saving ".concat(config));

		String clientPath;
		try {
			clientPath = mc.mcDataDir.getCanonicalPath().concat("\\elysium\\configs\\");

			File file = new File(clientPath + config + ".elysium");
			if(!file.exists())
				file.createNewFile();

			FileWriter f = new FileWriter(clientPath + config + ".elysium");

			f.write("");

			f.append(Elysium.version).append("\n");

			String add = "";

			int i = 0;

			for(Mod m : Elysium.getInstance().getModManager().getMods()) {

				f.append(m.name.toLowerCase()).append(":").append(m.toggled ? "true" : "false").append("\n"); // module - enabled - value

				for(Setting s : m.settings) {
					String settingValue = "ERROR";
					if(s instanceof BooleanSetting) {
						settingValue = "".concat(((BooleanSetting)s).isEnabled() ? "true" : "false");
					}
					if(s instanceof NumberSetting) {
						settingValue = "".concat(String.valueOf(((NumberSetting)s).getValue()));
					}
					if(s instanceof ModeSetting) {
						settingValue = "".concat(((ModeSetting)s).getMode());
					}
					if(s instanceof KeybindSetting) {
						settingValue = "".concat(((KeybindSetting)s).combo + "-" + ((KeybindSetting)s).getKeyCode());
					}
					if(s instanceof ColorSetting) {
						BColor clr = ((ColorSetting) s).color;
						settingValue = clr.r + "." + clr.g + "." + clr.b + "." + clr.a;
					}

					f.append(m.name.toLowerCase()).append(":").append(s.name.toLowerCase()).append(":").append(settingValue).append("\n"); // module - setting - value
				}

				f.append("\n");

				i++;
			}

			f.close();
		} catch (IOException e) {
			e.printStackTrace();
			Elysium.getInstance().addChatMessageConfig("falied to write config!");
		}
	}

	public static void delete(String config) {
		String clientPath;
		try {
			clientPath = mc.mcDataDir.getCanonicalPath().concat("\\elysium\\configs\\");

			File cong = new File(clientPath.concat(config + ".elysium"));

			if(cong.exists()) {
				cong.delete();
				Elysium.getInstance().addChatMessageConfig("deleted " + config);
			} else {
				Elysium.getInstance().addChatMessageConfig("config " + config + " does not exist");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadDefaultConfig() {
		String config = "defaultconfig";

		System.out.println("[CONFIG] loading ".concat(config));

		try {
			String clientPath = mc.mcDataDir.getCanonicalPath().concat("\\elysium\\");
			File cong = new File(clientPath.concat("defaultconfig.elysium"));

			FileReader fr = new FileReader(cong);

			BufferedReader br= new BufferedReader(fr);
			StringBuffer sb= new StringBuffer();

			String line;

			int i = 0;

			while((line=br.readLine())!=null)
			{
				if(i == 0 || line.isEmpty()) {
					i++;
					continue;
				}

				if(line.split(":").length != 2 && line.split(":").length != 3) {
					System.out.println("[CONFIG] ERROR on line " + i + " - line.split(:) length is " + line.split(":").length);
					i++;
					continue;
				}

				String[] lines = line.split(":");

				if(line.split(":").length == 2) {
					if(Elysium.getInstance().byName(lines[0]) != null) {
						Mod m = Elysium.getInstance().byName(lines[0]);
						if(lines[1].equalsIgnoreCase("true") && !m.toggled)
							m.toggle();
						else if(lines[1].equalsIgnoreCase("false") && m.toggled)
							m.toggle();
					} else {
						System.out.println("[CONFIG] ERROR on line " + i + " there is no module named " + lines[0]);
					}
				}

				if(line.split(":").length == 3) {
					if(Elysium.getInstance().byName(lines[0]) != null) {
						Mod m = Elysium.getInstance().byName(lines[0]);
						boolean found = false;
						for(Setting s : m.settings) {
							if(s.name.equalsIgnoreCase(lines[1])) {
								if(s instanceof BooleanSetting) {
									((BooleanSetting)s).setEnabled(lines[2].equalsIgnoreCase("true"));
								}
								if(s instanceof ColorSetting) {
									ColorSetting cs = (ColorSetting)s;
									String[] colors = lines[2].split("\\.");
									cs.color.r = Integer.parseInt(colors[0]);
									cs.color.g = Integer.parseInt(colors[1]);
									cs.color.b = Integer.parseInt(colors[2]);
									cs.color.a = Integer.parseInt(colors[3]);
								}
								if(s instanceof ModeSetting) {
									((ModeSetting)s).setMode(lines[2]);
								}
								if(s instanceof NumberSetting) {
									((NumberSetting)s).setValue(Float.parseFloat(lines[2]));
								}
								if(s instanceof KeybindSetting) {
									if(lines[2].split("-").length != 2) {
										System.out.println("[CONFIG] ERROR on line " + i + " lines[2] split lenght isnt 2 but is " + lines[2].split("-").length);
										break;
									}
									((KeybindSetting)s).code = Integer.parseInt(lines[2].split("-")[1]);
									((KeybindSetting)s).combo = Integer.parseInt(lines[2].split("-")[0]);
								}
								found = true;
							}
						}
						if(!found) {
							System.out.println("[CONFIG] ERROR on line " + i + " there is no setting named " + lines[1]);
						}
					} else {
						System.out.println("[CONFIG] ERROR on line " + i + " there is no module named " + lines[0]);
					}
				}

				i++;
			}

			fr.close();

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("[CONFIG] falied to read default config!");
		}
	}

	public static String[] configs() {
		try {
			String clientPath = mc.mcDataDir.getCanonicalPath().concat("\\elysium\\configs\\");
			return new File(clientPath).list();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static String getVersion(String config) {
		System.out.println("[CONFIG] getting version of ".concat(config));

		try {
			String clientPath = mc.mcDataDir.getCanonicalPath().concat("\\elysium\\configs\\");
			File cong = new File(clientPath.concat(config + ".elysium"));

			if(!cong.exists()) {
				System.out.println("[CONFIG] ERROR config named " + config + " does not exist");

				return "-1";
			}

			FileReader fr = new FileReader(cong);

			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(fr);
			StringBuffer sb= new StringBuffer();

			String line;

			while((line=br.readLine())!=null)
			{
				br.close();
				fr.close();
				return line;
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("[CONFIG] falied to read version of " + config);
		}

		System.out.println("[CONFIG] falied to read version of " + config);
		return "-1";
	}

	public static void load(String config) {
		Elysium.getInstance().addChatMessageConfig("loading ".concat(config));

		try {
			String clientPath = mc.mcDataDir.getCanonicalPath().concat("\\elysium\\configs\\");
			File cong = new File(clientPath.concat(config + ".elysium"));

			if(!cong.exists()) {
				Elysium.getInstance().addChatMessageConfig("ERROR config named " + config + " does not exist");

				return;
			}

			if(!(getVersion(config) + "").equals(Elysium.getInstance().version)) {
				Elysium.getInstance().addChatMessageConfig("Warning: config version is " + getVersion(config) + " and client version is " + Elysium.version);
			}

			FileReader fr = new FileReader(cong);
			BufferedReader br= new BufferedReader(fr);
			StringBuffer sb = new StringBuffer();

			String line;

			int i = 0;

			while((line=br.readLine())!=null)
			{
				if(i == 0 || line.isEmpty()) {
					i++;
					continue;
				}

				if(line.split(":").length != 2 && line.split(":").length != 3) {
					Elysium.getInstance().addChatMessageConfig("ERROR on line " + i + " - line.split(:) length is " + line.split(":").length);
					i++;
					continue;
				}

				String[] lines = line.split(":");

				if(line.split(":").length == 2) {
					if(Elysium.getInstance().byName(lines[0]) != null) {
						Mod m = Elysium.getInstance().byName(lines[0]);
						if(lines[1].equalsIgnoreCase("true") && !m.toggled)
							m.toggle();
						else if(lines[1].equalsIgnoreCase("false") && m.toggled)
							m.toggle();
					} else {
						Elysium.getInstance().addChatMessageConfig("ERROR on line " + i + " there is no module named " + lines[0]);
					}
				}

				if(line.split(":").length == 3) {
					if(Elysium.getInstance().byName(lines[0]) != null) {
						Mod m = Elysium.getInstance().byName(lines[0]);
						boolean found = false;
						for(Setting s : m.settings) {
							if(s.name.equalsIgnoreCase(lines[1])) {
								if(s instanceof BooleanSetting) {
									((BooleanSetting)s).setEnabled(lines[2].equalsIgnoreCase("true"));
								}
								if(s instanceof ModeSetting) {
									((ModeSetting)s).setMode(lines[2]);
								}
								if(s instanceof NumberSetting) {
									((NumberSetting)s).setValue(Float.parseFloat(lines[2]));
								}
								if(s instanceof KeybindSetting) {
									((KeybindSetting)s).code = Integer.parseInt(lines[2].split("-")[1]);
									((KeybindSetting)s).combo = Integer.parseInt(lines[2].split("-")[0]);
								}
								found = true;
							}
						}
						if(!found) {
							Elysium.getInstance().addChatMessageConfig("ERROR on line " + i + " there is no setting named " + lines[1]);
						}
					} else {
						Elysium.getInstance().addChatMessageConfig("ERROR on line " + i + " there is no module named " + lines[0]);
					}
				}

				i++;
			}

			fr.close();
			saveDefaultConfig();
		} catch (IOException e) {
			e.printStackTrace();
			Elysium.getInstance().addChatMessageConfig("falied to read default config!");
		}
	}

	public static void saveDefaultConfig() {
		//if(Panic.panic) {
		//	System.out.println("[CONFIG] did not write default config because panic is enabled!"); //TODO: panic
		//	return;
		//}

		String config = "defaultconfig";

		System.out.println("[CONFIG] saving ".concat(config));

		String clientPath;
		try {
			clientPath = mc.mcDataDir.getCanonicalPath().concat("\\elysium\\");

			FileWriter f = new FileWriter(clientPath + config + ".elysium");

			f.write("");

			f.append(Elysium.version).append("\n");

			String add = "";

			int i = 0;

			for(Mod m : Elysium.getInstance().getModManager().getMods()) {

				f.append(m.name.toLowerCase()).append(":").append(m.toggled ? "true" : "false").append("\n"); // module - enabled - value

				for(Setting s : m.settings) {
					String settingValue = "";
					if(s instanceof BooleanSetting) {
						settingValue = "".concat(((BooleanSetting)s).isEnabled() ? "true" : "false");
					}
					if(s instanceof NumberSetting) {
						settingValue = "".concat(String.valueOf(((NumberSetting)s).getValue()));
					}
					if(s instanceof ModeSetting) {
						settingValue = "".concat(((ModeSetting)s).getMode());
					}
					if(s instanceof KeybindSetting) {
						settingValue = "".concat(((KeybindSetting)s).combo + "-" + ((KeybindSetting)s).getKeyCode());
					}
					if(s instanceof ColorSetting) {
						BColor clr = ((ColorSetting) s).color;
						settingValue = clr.r + "." + clr.g + "." + clr.b + "." + clr.a;
					}

					f.append(m.name.toLowerCase()).append(":").append(s.name.toLowerCase()).append(":").append(settingValue).append("\n"); // module - setting - value
				}

				f.append("\n");

				i++;
			}

			f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
