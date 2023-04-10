package crispy.features.commands.impl;

import crispy.Crispy;
import crispy.features.commands.Command;
import crispy.features.commands.CommandInfo;
import crispy.util.file.Filer;

import java.util.ArrayList;

@CommandInfo(name = "pattern", description = "Set a custom click pattern for your aura", syntax = ".pattern start | .pattern stop", alias = "pattern")
public class ClickPatternCommand extends Command {
    public long lastClick;
    public static Filer filer = new Filer("ClickPatterns", "Crispy");
    public ArrayList<Long> delays = new ArrayList<>();
    public boolean isToggled;

    public ClickPatternCommand() {
        for(String lines : filer.read()) {
            long parsed = Long.parseLong(lines);
            delays.add(parsed);
        }
    }

    @Override
    public void onCommand(String arg, String[] args) throws Exception {
        if(args.length > 0) {
            if (args[0].equalsIgnoreCase("start")) {
                Crispy.addChatMessage("Recording started");
                isToggled = true;
                lastClick = System.currentTimeMillis();
                filer.clear();
            } else if (args[0].equalsIgnoreCase("stop")) {
                for (long del : delays) {
                    filer.write(del + "");
                }
                Crispy.addChatMessage("Stopped recording");
                isToggled = false;
            } else {
                Crispy.addChatMessage(getSyntax());
            }
        }

    }
}
