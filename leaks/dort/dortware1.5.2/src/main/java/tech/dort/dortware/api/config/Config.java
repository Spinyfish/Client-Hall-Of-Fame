package tech.dort.dortware.api.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.file.FileManager;
import tech.dort.dortware.api.file.MFile;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.property.Value;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.StringValue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.List;

public class Config extends MFile {

    public Config(String name) {
        super(name, "configs/");
    }

    @Override
    public void load(FileManager fileManager) {
        BufferedReader fileContents;
        try {
            fileContents = fileManager.getBufferedReaderForFile(this);
        } catch (FileNotFoundException e) {
            fileManager.initializeFile(this);
            return;
        }
        try {
            JsonObject json = new JsonParser().parse(fileContents).getAsJsonObject();
            for (Module module : Client.INSTANCE.getModuleManager().getObjects()) {
                List<Value> valueList = Client.INSTANCE.getValueManager().getValuesFromOwner(module);
                JsonObject jsonObject = json.getAsJsonObject(module.getModuleData().getName());
                try {
                    boolean toggled = jsonObject.getAsJsonPrimitive("toggled").getAsBoolean();
                    if (toggled && !module.isToggled()) {
                        module.toggle();
                    } else if (!toggled && module.isToggled()) {
                        module.toggle();
                    }
                } catch (Exception exception) {

                }
                for (Value value : valueList) {
                    try {
                        if (value instanceof StringValue) {
                            StringValue stringValue = (StringValue) value;
                            stringValue.setValue(jsonObject.getAsJsonPrimitive(value.getName()).getAsString());
                        } else if (value instanceof EnumValue) {
                            EnumValue enumValue = (EnumValue) value;
                            enumValue.setValue(jsonObject.getAsJsonPrimitive(value.getName()).getAsString());
                        } else if (value instanceof BooleanValue) {
                            BooleanValue booleanValue = (BooleanValue) value;
                            booleanValue.setValue(jsonObject.getAsJsonPrimitive(value.getName()).getAsBoolean());
                        } else if (value instanceof NumberValue) {
                            NumberValue numberValue = (NumberValue) value;
                            numberValue.setValue(jsonObject.getAsJsonPrimitive(value.getName()).getAsDouble());
                        }
                    } catch (Exception ignored) {

                    }
                }
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Override
    public void save() {
        FileManager fileManager = Client.INSTANCE.getFileManager();
        final JsonObject json = new JsonObject();
        for (Module module : Client.INSTANCE.getModuleManager().getObjects()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("toggled", module.isToggled());
            List<Value> valueList = Client.INSTANCE.getValueManager().getValuesFromOwner(module);
            for (Value value : valueList) {
                if (value instanceof StringValue) {
                    StringValue stringValue = (StringValue) value;
                    jsonObject.addProperty(value.getName(), stringValue.getValue());
                } else if (value instanceof EnumValue<?>) {
                    EnumValue<?> enumValue = (EnumValue<?>) value;
                    jsonObject.addProperty(value.getName(), enumValue.getValue().name());
                } else if (value instanceof BooleanValue) {
                    BooleanValue booleanValue = (BooleanValue) value;
                    jsonObject.addProperty(value.getName(), booleanValue.getValue());
                } else if (value instanceof NumberValue) {
                    NumberValue numberValue = (NumberValue) value;
                    jsonObject.addProperty(value.getName(), numberValue.isInteger() ? numberValue.getValue().intValue() : numberValue.getValue().floatValue());
                }
            }
            json.add(module.getModuleData().getName(), jsonObject);
        }
        try {
            fileManager.writeFile(this, json.toString());
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }
}
