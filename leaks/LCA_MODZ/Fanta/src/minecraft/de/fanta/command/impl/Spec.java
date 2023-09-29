package de.fanta.command.impl;

import de.fanta.command.Command;

public class Spec extends Command {
    public Spec() {
        super("spec");
    }
    public static String name = " ";
    @Override
    public void execute(String[] args) {
    	 if (args.length != 1) {  
    		    messageWithPrefix("User Name");
    		    return;
    	 }
    	 		name = args[0];
    }
}