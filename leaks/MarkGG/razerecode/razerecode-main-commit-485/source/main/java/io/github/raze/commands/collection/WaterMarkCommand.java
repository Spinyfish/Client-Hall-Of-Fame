package io.github.raze.commands.collection;

import io.github.raze.commands.system.BaseCommand;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.Display;

public class WaterMarkCommand extends BaseCommand {

    public WaterMarkCommand() {
        super("WaterMark", "Set the client's watermark", "watermark <Text>", "wm");
    }

    public String onCommand(String[] arguments, String command) {
        if (arguments.length == 1) {
            return "Usage: " + getSyntax();
        }

        String waterMarkText = String.join(" ", arguments);
        String oneWaterMarkText = waterMarkText.replaceAll("\\.(watermark|wm)\\s*", "");

        Display.setTitle(oneWaterMarkText);

        return String.format("Set the watermark to " + EnumChatFormatting.GREEN + "%s", oneWaterMarkText);
    }

}
