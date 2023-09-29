package de.verschwiegener.atero.util.files.config;

import java.awt.Color;
import java.util.Arrays;

import de.verschwiegener.atero.Management;

public class ConfigItem {

    String[] args;

    public ConfigItem(String[] args) {
	this.args = args;
    }

    public void execute() {
	try {
	    switch (args[0]) {
	    case "toggle":
		Management.instance.modulemgr.getModuleByName(args[1]).toggle(Boolean.parseBoolean(args[2]));
		break;
	    case "set":
		if (args[3].equalsIgnoreCase("true") || args[3].equalsIgnoreCase("false")) {
		    Management.instance.settingsmgr.getSettingByName(args[1]).getItemByName(args[2])
			    .setState(Boolean.valueOf(args[3]));
		    break;
		} else if (isInt(args[3])) {
		    Management.instance.settingsmgr.getSettingByName(args[1]).getItemByName(args[2]).setCurrentValue((float) Float.parseFloat(args[3]));
		    break;
		} else if (args.length == 6) {
		    Color color = new Color(Integer.valueOf(args[3]), Integer.valueOf(args[4]),
			    Integer.valueOf(args[5]), Integer.valueOf(args[6]));
		    Management.instance.settingsmgr.getSettingByName(args[1]).getItemByName(args[2]).setColor(color);
		    break;
		} else {
		    Management.instance.settingsmgr.getSettingByName(args[1]).getItemByName(args[2]).setCurrent(args[3]);
		    break;
		}
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    public String getMode() {
	return args[0];
    }
    public String[] getArgs() {
	return args;
    }

    private boolean isInt(String str) {
	try {
	    double x = Float.parseFloat(str);
	    return true;
	} catch (NumberFormatException e) {
	    return false;
	}

    }

}
