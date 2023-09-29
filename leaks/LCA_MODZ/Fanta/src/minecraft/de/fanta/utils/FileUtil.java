package de.fanta.utils;

import net.minecraft.client.Minecraft;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import de.fanta.Client;
import de.fanta.module.Module;
import de.fanta.module.Module.Type;
import de.fanta.setting.Setting;
import de.fanta.setting.settings.CheckBox;
import de.fanta.setting.settings.DropdownBox;
import de.fanta.setting.settings.Slider;

public class FileUtil {

	public static boolean isTutorialDone;
	public static File configFile = new File(
			Minecraft.getMinecraft().mcDataDir + "/" + Client.INSTANCE.name + "/config.txt");
	public static File tutorialFile = new File(
			Minecraft.getMinecraft().mcDataDir + "/" + Client.INSTANCE.name + "/tutorial.txt");
	public static File friendFile = new File(
			Minecraft.getMinecraft().mcDataDir + "/" + Client.INSTANCE.name + "/friends.txt");
	public String getName;
	public String getTutorialStatus;
	public Minecraft mc;

	public FileUtil() {
		mc = Minecraft.getMinecraft();
	}

	public static void saveTutorial() {
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(tutorialFile));
			String tutorialString = "isTutorialDone" + ":" + isTutorialDone;
			writer.println(tutorialString);
			writer.close();
		} catch (Exception ignored) {
		}
	}

	public static void saveModules() {
		File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.INSTANCE.name + "/modules.txt");
		try {
			PrintWriter printWriter = new PrintWriter(new FileWriter(file));
			for (Module module : Client.INSTANCE.moduleManager.modules) {
				String modname = module.name;
				String string = modname + ":" + module.state;
				printWriter.println(string);
			}
			printWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveKeys() {
		File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.INSTANCE.name + "/keys.txt");
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(file));
			for (Module module : Client.INSTANCE.moduleManager.modules) {
				String modulename = module.name;
				int modulekey = module.getKeyBind(); // TODO: Keybinding system
				String endstring = modulename + ":" + modulekey;
				writer.println(endstring);
			}
			writer.close();
		} catch (Exception ignored) {
		}
	}

	public static void saveFriends() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(friendFile));
			Iterator iterator = FriendSystem.getFriends().iterator();

			while (iterator.hasNext()) {
				String name = (String) iterator.next();
				writer.write(name + ":" + FriendSystem.getFriends());
				writer.newLine();
			}

			writer.close();
		} catch (IOException var4) {
			var4.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	public boolean isTutorialDone() {
		try {
			if (tutorialFile.exists()) {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(tutorialFile));
				String readString;
				while ((readString = bufferedReader.readLine()) != null) {
					String[] split = readString.split(":");
					if (split[1] != null) {
						getTutorialStatus = split[1];
						if (getTutorialStatus.equals("true"))
							return true;
						if (getTutorialStatus.equals("false"))
							return false;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("resource")
	public static void loadModules() {
		try {
			File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.INSTANCE.name + "/modules.txt");
			if (!file.exists()) {
				PrintWriter printWriter = new PrintWriter(new FileWriter(file));
				printWriter.println();
				printWriter.close();
			} else if (file.exists()) {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				String readString;
				while ((readString = bufferedReader.readLine()) != null) {
					String[] split = readString.split(":");
					Module mod = Client.INSTANCE.moduleManager.getModule(split[0]);
					boolean enabled = Boolean.parseBoolean(split[1]);
					if (mod != null) {
						mod.state = enabled;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	public static void loadKeys() {
		try {
			File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.INSTANCE.name + "/keys.txt");
			if (!file.exists()) {
				PrintWriter printWriter = new PrintWriter(new FileWriter(file));
				printWriter.println();
				printWriter.close();
			} else if (file.exists()) {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				String readString;
				while ((readString = bufferedReader.readLine()) != null) {
					String[] split = readString.split(":");
					Module mod = Client.INSTANCE.moduleManager.getModule(split[0]);
					if (mod != null) {
						int key = Integer.parseInt(split[1]);
						mod.setKeyBind(key);
						// TODO: Keybinding system
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadFriends() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(friendFile));

			String line;
			while ((line = reader.readLine()) != null) {
				String[] arguments = line.split(":");
				FriendSystem.addFriend(arguments[0]);
			}

			reader.close();
		} catch (FileNotFoundException var4) {
			var4.printStackTrace();
		} catch (IOException var5) {
			var5.printStackTrace();
		}
	}

	public static void saveValues(String name, boolean config) {
		File f = new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.INSTANCE.name + (config ? "/configs/" : "/" ) + name + ".txt");
		try {
			if (!f.exists())
				f.createNewFile();
			PrintWriter pw = new PrintWriter(f);

			for (Module mod : Client.INSTANCE.moduleManager.modules) {
				pw.println(mod.name + ":state:toggled:" + mod.state);
				if (!mod.getType().equals(Type.Visual) || !config) {
					for (Setting setting : mod.settings) {
						Object setObject = setting.getSetting();
						String setName = setting.getName();
						
						if (setObject instanceof CheckBox) {
							pw.println(mod.name + ":" + setName + ":b:" + ((CheckBox) setObject).state);
							continue;
						}
						if (setObject instanceof Slider) {
//							System.out.println("Saving slider" + setName + ":" + ((Slider) setObject).curValue);
							pw.println(mod.name + ":" + setName + ":d:" + ((Slider) setObject).curValue);
							continue;
						}
						if (setObject instanceof DropdownBox) {
							pw.println(mod.name + ":" + setName + ":o:" + ((DropdownBox) setObject).curOption);
						}
					}
				}
			}
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	public static void loadValues(String name, boolean config, boolean onlineConfig) {
		try {
			File f = new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.INSTANCE.name + (config ? "/configs/" : "/" ) + name + ".txt");
			if (!f.exists()) {
				f.createNewFile();
			}
			String line;
			BufferedReader br = new BufferedReader(new FileReader(f));
            if(onlineConfig) {
            	Thread thread = new Thread(() -> {
                    ChatUtil.sendChatMessageWithPrefix("Loading...");
                    try {
                        URLConnection urlConnection = new URL("https://raw.githubusercontent.com/LCAMODZ/Fanta-configs/main/" + name.toLowerCase() + ".txt").openConnection();
                        urlConnection.setConnectTimeout(10000);
                        urlConnection.connect();
                        StringBuilder stringBuilder = new StringBuilder();
                        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                        	String text;
                            while ((text = bufferedReader.readLine()) != null) {
                                if(text.contains("404: Not Found")) {
                                	ChatUtil.sendChatMessageWithPrefix("An error occurred while loading this config.");
                                    return;
                                }
                                String[] values = text.split(":");
                				if (values.length == 4) {
                					String moduleName = values[0];
                					Module module = Client.INSTANCE.moduleManager.getModule(moduleName);
                					if (module != null) {
                						String setName = values[1];
                						String object = values[2];
                						String lastValue = values[3];
                						
                						switch (object) {
                						case "toggled":
                							module.state = Boolean.parseBoolean(lastValue);
                						break;
                						case "b":
                							try {
                							((CheckBox) module.getSetting(setName).getSetting()).state = Boolean.parseBoolean(lastValue);
                							  }catch (NullPointerException e) {
                								   
                							   }
                							break;
                						case "d":
                							System.out.println("Loading SLider: " + setName + ":" + Double.parseDouble(lastValue));
                							try {
                							((Slider) module.getSetting(setName).getSetting()).curValue = Double.parseDouble(lastValue);
                							   }catch (NullPointerException e) {
                								   
                							   }
                							break;
                						case "o":
                							try {
                								if(module.getSetting(setName).getSetting() instanceof DropdownBox)
                							((DropdownBox) module.getSetting(setName).getSetting()).curOption = lastValue;
                							  }catch (NullPointerException e) {
                								   
                							   }
                						}
                					}

                				}
                            }
                            
                            ChatUtil.sendChatMessageWithPrefix("You successfully loaded the config named " + name);
                        }
                    } catch (IOException e) {
                    	ChatUtil.sendChatMessageWithPrefix("An error occurred while loading this config.");
                        e.printStackTrace();
                    }
                });
                thread.start();
            }else {
            	while ((line = br.readLine()) != null) {
    				String[] values = line.split(":");
    				if (values.length == 4) {
    					String moduleName = values[0];
    					Module module = Client.INSTANCE.moduleManager.getModule(moduleName);
    					if (module != null) {
    						String setName = values[1];
    						String object = values[2];
    						String lastValue = values[3];
    						switch (object) {
    						case "toggled":
    							module.state = Boolean.parseBoolean(lastValue);
    							break;
    						case "b":
    							try {
    							((CheckBox) module.getSetting(setName).getSetting()).state = Boolean.parseBoolean(lastValue);
    							  }catch (NullPointerException e) {
    								   
    							   }
    							break;
    						case "d":
    							System.out.println("Loading SLider: " + setName + ":" + Double.parseDouble(lastValue));
    							try {
    							((Slider) module.getSetting(setName).getSetting()).curValue = Double.parseDouble(lastValue);
    							   }catch (NullPointerException e) {
    								   
    							   }
    							break;
    						case "o":
    							try {
    								if(module.getSetting(setName).getSetting() instanceof DropdownBox)
    									((DropdownBox) module.getSetting(setName).getSetting()).curOption = lastValue;
    							  }catch (NullPointerException e) {
    								   
    							   }
    						}
    					}

    				}

    			}
            }
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void load() {
		File folder = new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.INSTANCE.name);
		if (!folder.exists())
			folder.mkdir();
		loadModules();
		loadKeys();
		loadFriends();
		loadValues("values", false, false);
	
	}

	public static void save() {
		saveModules();
		saveKeys();
		saveFriends();
		saveValues("values", false);
	}
}
