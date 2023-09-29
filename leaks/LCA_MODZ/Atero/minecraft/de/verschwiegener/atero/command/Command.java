package de.verschwiegener.atero.command;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public abstract class Command {

    String syntax, description;
    String[] complete;

    public Command(String syntax, String description, String[] complete) {
	this.syntax = syntax;
	this.description = description;
	this.complete = complete;
    }

    public abstract void onCommand(String[] args);

    public String getSyntax() {
	return syntax;
    }

    public String getDescription() {
	return description;
    }

    public String[] getComplete() {
	return complete;
    }
    
    public String[] getSuggestion(String[] args, int position) {
	try {
	    if ((complete.length > position) && position != -1) {
		ArrayList<String> suggestions = new ArrayList<String>();
		String[] suggestion = complete[position].split(" / ");
		for (String str : suggestion) {
		    //System.out.println("Str: " + str);
		    if (str.contains("(")) {
			String[] parent = StringUtils.substringBetween(str, "(", ")").split(",");
			int parentPosition = Integer.parseInt(parent[0]);
			if (args[parentPosition].equalsIgnoreCase(parent[1])) {
			    suggestions.add(str.split(Pattern.quote("("))[0]);
			}
		    } else {
			suggestions.add(str);
		    }
		}
		//System.out.println("List: " + suggestions);
		return suggestions.stream().toArray(String[]::new);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	//System.out.println("Return");
	return null;
    }

}
