package net.java.games.input;

import net.java.games.util.plugins.Plugin;

public class AWTEnvironmentPlugin extends ControllerEnvironment implements Plugin {
   private final Controller[] controllers = new Controller[]{new AWTKeyboard(), new AWTMouse()};

   public Controller[] getControllers() {
      return this.controllers;
   }

   public boolean isSupported() {
      return true;
   }
}
