package club.marsh.bloom.api.config;
//apple26j made it
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map.Entry;

import club.marsh.bloom.impl.ui.hud.Component;
import club.marsh.bloom.impl.ui.hud.HudDesigner;
import org.lwjgl.input.Keyboard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.mods.render.ClickGUI;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.api.value.ModeValue;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.api.value.Value;

public class ConfigManager
{
	public static File directory = new File("Bloom");
	public static File configDirectory = new File(directory, "configs");
	public static File scriptDirectory = new File(directory, "scripts");
	public static File configFile = new File(configDirectory, "default.json");
	public static File hudFile = new File(configDirectory, "hud.json");
	public static File preventDiscordRPCUsageFile = new File("IdontHaveDiscord.txt");
	public static Gson gson = new Gson();
	public static Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
	public static JsonParser jsonParser = new JsonParser();

	public static void init() {
		if (!directory.exists()) {
			directory.mkdir();
		}
		if (!scriptDirectory.exists())
			scriptDirectory.mkdir();
		if (!configDirectory.exists())
			configDirectory.mkdir();
		if (!configFile.exists()) {
			Bloom.INSTANCE.firstLoad = true;
			saveModules();
		} else {
			loadModules();
		}

	}

	public static void saveModules () {
		if (Bloom.INSTANCE.hudDesigner == null || Bloom.INSTANCE.moduleManager == null || Bloom.INSTANCE.moduleManager.getModules() == null)
			return;
		new Thread (() ->
		{
			try {
				JsonObject jsonObject1 = new JsonObject();
				JsonObject jsonObject3 = new JsonObject();
				for (Component component : Bloom.INSTANCE.hudDesigner.components) {
					JsonObject componentObject = new JsonObject();
					componentObject.addProperty("Toggled",component.isEnabled());
					componentObject.addProperty("x",component.getX());
					componentObject.addProperty("y",component.getY());
					componentObject.addProperty("width",component.getWidth());
					componentObject.addProperty("height",component.getHeight());
					jsonObject3.add(component.getName(), componentObject);
				}
				for (Module hack : Bloom.INSTANCE.moduleManager.getModules()) {
					JsonObject jsonObject2 = new JsonObject();

					try
					{
						if (!(hack instanceof ClickGUI)) {
							jsonObject2.addProperty("Toggled", hack.isToggled());

						}

						if (Bloom.INSTANCE.valueManager.getAllValuesFrom(hack.getName()) != null) {
							for (Value value : Bloom.INSTANCE.valueManager.getAllValuesFrom(hack.getName())) {
								if (value != null) {
									if (value.isCheck()) {
										jsonObject2.addProperty(value.getName(), ((BooleanValue) (value)).isOn());

									}

									if (value.isCombo()) {
										jsonObject2.addProperty(value.getName(), value.getValString());

									}

									if (value.isSlider()) {
										jsonObject2.addProperty(value.getName(), value.getValDouble());

									}

								}

							}

						}

						jsonObject2.addProperty("Key", Keyboard.getKeyName(hack.keybind));
						jsonObject1.add(hack.getName(), jsonObject2);
					}
					
					catch(Exception e)
					{
						;
					}

				}
				
				PrintWriter printWriter = new PrintWriter(new FileWriter(configFile));
				printWriter.println(prettyGson.toJson(jsonObject1));
				printWriter.close();

				PrintWriter printWriter2 = new PrintWriter(new FileWriter(hudFile));
				printWriter2.println(prettyGson.toJson(jsonObject3));
				printWriter2.close();
			} catch (Exception e) {
				e.printStackTrace();

			}

		}).start();

	}

	public static void loadModules () {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(configFile));
			JsonObject jsonObject1 = (JsonObject) jsonParser.parse(bufferedReader);
			bufferedReader.close();
			Iterator<Entry<String, JsonElement>> iterator = jsonObject1.entrySet().iterator();

			BufferedReader hudbufferedReader = new BufferedReader(new FileReader(hudFile));
			JsonObject hudjsonObject = (JsonObject) jsonParser.parse(hudbufferedReader);
			hudbufferedReader.close();
			Iterator<Entry<String, JsonElement>> huditerator = hudjsonObject.entrySet().iterator();
			Entry<String, JsonElement> entry;

			while (huditerator.hasNext()) {
				Entry<String, JsonElement> entryy = huditerator.next();

				HudDesigner.components.forEach((component) -> {
					//System.out.println(component.getName().toLowerCase() + " " + entry.getKey().toLowerCase());
					if (component.getName().toLowerCase().contains(entryy.getKey().toLowerCase())) {
						JsonObject jsonObject = entryy.getValue().getAsJsonObject();
						component.setEnabled(jsonObject.get("Toggled").getAsBoolean());
						component.setX(jsonObject.get("x").getAsInt());
						component.setY(jsonObject.get("y").getAsInt());
					}
				});
			}

			while (iterator.hasNext() && (entry = iterator.next()) != null) {
				Module hack = Bloom.INSTANCE.moduleManager.getByName(entry.getKey());

				if (hack != null) {
					try {
						JsonObject jsonObject2 = entry.getValue().getAsJsonObject();
						boolean toggled = hack instanceof ClickGUI ? false : jsonObject2.get("Toggled").getAsBoolean();
						boolean valBoolean;
						String valString;
						double valDouble;

						if (Bloom.INSTANCE.valueManager.getAllValuesFrom(hack.getName()) != null) {
							for (Value value : Bloom.INSTANCE.valueManager.getAllValuesFrom(hack.getName())) {
								if (value != null) {
									try {
										if (value.isCheck()) {
											valBoolean = jsonObject2.get(value.getName()).getAsBoolean();
											if (((BooleanValue) value).isOn() != valBoolean)
												((BooleanValue) value).flip();
											//((BooleanValue) value).on = valBoolean;

										}

										if (value.isCombo()) {
											valString = jsonObject2.get(value.getName()).getAsString();
											for (String mode : ((ModeValue) value).modes) {
												if (mode.contains(valString)) {
													((ModeValue) value).mode = mode;
													value.hitboxname = value.name + ":" + ((ModeValue) value).mode;
												}
											}

										}

										if (value.isSlider()) {

											valDouble = jsonObject2.get(value.getName()).getAsDouble();
											double calculatedValue = valDouble;
											Number number = ((NumberValue) value).getObject();
											if (number instanceof Double) {
												((NumberValue) value).setObject(calculatedValue);
											} else if (number instanceof Integer) {
												((NumberValue) value).setObject((int) (calculatedValue));
											} else if (number instanceof Long) {
												((NumberValue) value).setObject((long) (calculatedValue));
											} else if (number instanceof Float) {
												((NumberValue) value).setObject((float) (calculatedValue));
											}

										}
									} catch (Exception e) {
										System.out.println("A exception occoured while loading a config");
									}
								}

							}

						}
						try {
							if (toggled == true)
								hack.setToggled(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
						hack.keybind = Keyboard.getKeyIndex(jsonObject2.get("Key").getAsString());
					} catch (Exception e) {
						System.out.println("A exception occoured while loading a config.");
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}