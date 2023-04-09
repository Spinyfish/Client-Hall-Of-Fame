package tech.dort.dortware.impl.command;

import tech.dort.dortware.Client;
import tech.dort.dortware.api.command.Command;
import tech.dort.dortware.api.command.CommandData;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.property.Value;
import tech.dort.dortware.api.property.impl.CustomStringValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.StringValue;
import tech.dort.dortware.impl.managers.ValueManager;
import tech.dort.dortware.impl.utils.player.ChatUtil;

public class ModuleCommandBase extends Command {
    private final Module module;

    public ModuleCommandBase(CommandData data, Module module) {
        super(data);
        this.module = module;
    }

    @Override
    public void run(String command, String... args) {
        ValueManager valueManager = Client.INSTANCE.getValueManager();
        if (command.equalsIgnoreCase(this.getData().getName())) {
            for (Value<?> value : valueManager.getObjects()) {
                if (value.getName().equalsIgnoreCase(args[1])) {
                    String text = buildStringFromArray(args);
                    if (!text.isEmpty()) {
                        if (value instanceof CustomStringValue) {
                            CustomStringValue customStringValue = (CustomStringValue) value;
                            customStringValue.setValueAutoSave(text);
                        } else if (value instanceof EnumValue<?>) {
                            EnumValue<?> enumValue = (EnumValue<?>) value;
                            enumValue.setValueAutoSave(text);
                        } else if (value instanceof StringValue) {
                            StringValue stringValue = (StringValue) value;
                            stringValue.setValueAutoSave(text);
                        } else if (value instanceof NumberValue) {
                            NumberValue numberValue = (NumberValue) value;
                            double parsed = Double.parseDouble(text);
                            numberValue.setValueAutoSave(parsed);
                        }
                    } else {
                        ChatUtil.displayChatMessage("Not enough arguments.");
                        return;
                    }
                }
                return;
            }
            ChatUtil.displayChatMessage("nice brain.");
        }
    }

    private String buildStringFromArray(Object[] array) {
        if (2 > array.length) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 2; i < array.length; ++i) {
            stringBuilder.append(array[i]);
        }
        return stringBuilder.toString();
    }

    public Module getModule() {
        return module;
    }
}
