package de.verschwiegener.atero.util.chat;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.command.Command;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.util.files.config.Config;
import de.verschwiegener.atero.util.files.config.ConfigType;

public class AutoComplete {

    public static boolean hasSuggestion;
    private static String currentSuggestion;
    
    public static String updateText(String text) {
	if(hasSuggestion && !currentSuggestion.equalsIgnoreCase(" ")) {
	    return currentSuggestion;
	}
	return "";
    }
    
    
    public static String onAutoComplete2(final String text) {
	hasSuggestion = false;
	if (!text.startsWith(".")) {
	    return text;
	}
	try {
	    final String[] args = text.substring(1).split(" ");
	    if (args.length > 0) {
		final Command c = Management.instance.commandmgr.getCommandByName(args[0]);
		if (c != null) {
		    int position = text.endsWith(" ") ? args.length - 1 : args.length - 2;
			String[] suggestionArgs = c.getSuggestion(args, position);
			
			if(suggestionArgs == null) {
			    return text.substring(1);
			}
			
			String currentCommandArgs = args[args.length - 1];
			for (String suggestion : suggestionArgs) {
			    if (suggestion.startsWith("<")) {
				return StringUtils.removeEnd(text.substring(1), currentCommandArgs) + searchModes(suggestion, (currentCommandArgs == args[position])? "" : currentCommandArgs, args);
				
			    } else if (suggestion.startsWith(currentCommandArgs)) {
				hasSuggestion = true;
				currentSuggestion = suggestion;
				return StringUtils.removeEnd(text.substring(1), currentCommandArgs) + suggestion;
			    }
			}
			if(!hasSuggestion && suggestionArgs.length > 0 && (currentCommandArgs == args[position])) {
			    hasSuggestion = true;
			    currentSuggestion = suggestionArgs[0];
			    return suggestionArgs[0];
			}
		} else {
			Command c2 = Management.instance.commandmgr.getCommandByStartsWith(args[0]);
			if(c2 != null) {
			    hasSuggestion = true;
			    currentSuggestion = c2.getSyntax();
			    return c2.getSyntax();
			}
		    }
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return text.substring(1);
    }
    
    private static String searchModes(String suggestion, String currentCommandArgs, String[] args) {
	switch (suggestion) {
	case "<module>":
	    final Module module = Management.instance.modulemgr.getModulebyStartsWith(currentCommandArgs);
	    if (module != null) {
		hasSuggestion = true;
		currentSuggestion = module.getName();
		return module.getName();
	    } else {
		return currentCommandArgs;
	    }
	case "<config>":
	     Config config = Management.instance.configmgr.getConfigByName(currentCommandArgs, ConfigType.valueOf(args[1].toLowerCase()));
	     if(config == null) {
		 config = Management.instance.configmgr.getConfigByStartsWith(currentCommandArgs,
			    ConfigType.valueOf(args[1].toLowerCase()));
	     }
	    if (config != null) {
		hasSuggestion = true;
		currentSuggestion = config.getName();
		return config.getName();
	    }else {
		if(Management.instance.configmgr.configs.size() > 0) {
		    Config config2 = Management.instance.configmgr.configs.get(0);
		    hasSuggestion = true;
		    currentSuggestion = config2.getName();
		    return config2.getName();
		}
	    }
	    break;
	default:
	    if(currentCommandArgs.equalsIgnoreCase("")) {
		return suggestion;
	    }
	    return "";
	}
	return "";
    }
    
    public static String onAutoComplete(final String text) {
	if (!text.startsWith("."))
	    return "";
	AutoComplete.hasSuggestion = false;

	final String[] args = text.substring(1).split(" ");
	if (args.length > 1) {
	    final Command c = Management.instance.commandmgr.getCommandByName(args[0]);
	    if (c != null) {
		for(String str : c.getComplete()) {
		    final String[] args2 = str.split(" ");
			final String lastArgument = args2[args.length - 2];
			if (lastArgument != null) {
			    switch (lastArgument) {
			    case "<module>":
				final Module m = Management.instance.modulemgr.getModulebyStartsWith(args[args.length - 1]);
				try {
				    if (m != null) {
					if (text.endsWith(" ")) {
					    System.out.println("Module: " + m.getName());
					    AutoComplete.hasSuggestion = true;
					    return m.getName().toLowerCase().replaceFirst(args[args.length - 1].toLowerCase(),
						    "") + str.toLowerCase().replaceFirst("<module>", "");
					} else {
					    AutoComplete.hasSuggestion = true;
					    return m.getName().toLowerCase().replaceFirst(args[args.length - 1].toLowerCase(),
						   "") + " " + str.toLowerCase().replaceFirst("<module>", "");
					}
				    }
				} catch (final NullPointerException ex) {
				    ex.printStackTrace();
				}
			    case "<key>":
				break;
			    case "<config>":
				break;

			    default:
				break;
			    }
			}
		}
	    }
	} else if (!args[0].isEmpty()) {
	    final Command c = Management.instance.commandmgr.getCommandByStartsWith(args[0]);
	    if (c != null) {
		System.out.println("Return");
		if (text.endsWith(" ")) {
		    AutoComplete.hasSuggestion = true;
		    return c.getSyntax().toLowerCase().replaceFirst(args[0].toLowerCase(), "") + c.getComplete()[0];
		} else {
		    AutoComplete.hasSuggestion = true;
		    return c.getSyntax().toLowerCase().replaceFirst(args[0].toLowerCase(), "") + " " + c.getComplete()[0];
		}
	    }
	}
	return "";
    }
}
