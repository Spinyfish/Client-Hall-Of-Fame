// 
// Decompiled by Procyon v0.5.36
// 

package org.newdawn.slick.command;

import org.newdawn.slick.util.InputAdapter;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.Input;
import java.util.ArrayList;
import java.util.HashMap;

public class InputProvider
{
    private HashMap commands;
    private ArrayList listeners;
    private Input input;
    private HashMap commandState;
    private boolean active;
    
    public InputProvider(final Input input) {
        this.listeners = new ArrayList();
        this.commandState = new HashMap();
        this.active = true;
        (this.input = input).addListener(new InputListenerImpl());
        this.commands = new HashMap();
    }
    
    public List getUniqueCommands() {
        final List uniqueCommands = new ArrayList();
        for (final Command command : this.commands.values()) {
            if (!uniqueCommands.contains(command)) {
                uniqueCommands.add(command);
            }
        }
        return uniqueCommands;
    }
    
    public List getControlsFor(final Command command) {
        final List controlsForCommand = new ArrayList();
        for (final Map.Entry entry : this.commands.entrySet()) {
            final Control key = entry.getKey();
            final Command value = entry.getValue();
            if (value == command) {
                controlsForCommand.add(key);
            }
        }
        return controlsForCommand;
    }
    
    public void setActive(final boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public void addListener(final InputProviderListener listener) {
        this.listeners.add(listener);
    }
    
    public void removeListener(final InputProviderListener listener) {
        this.listeners.remove(listener);
    }
    
    public void bindCommand(final Control control, final Command command) {
        this.commands.put(control, command);
        if (this.commandState.get(command) == null) {
            this.commandState.put(command, new CommandState());
        }
    }
    
    public void clearCommand(final Command command) {
        final List controls = this.getControlsFor(command);
        for (int i = 0; i < controls.size(); ++i) {
            this.unbindCommand(controls.get(i));
        }
    }
    
    public void unbindCommand(final Control control) {
        final Command command = this.commands.remove(control);
        if (command != null && !this.commands.keySet().contains(command)) {
            this.commandState.remove(command);
        }
    }
    
    private CommandState getState(final Command command) {
        return this.commandState.get(command);
    }
    
    public boolean isCommandControlDown(final Command command) {
        return this.getState(command).isDown();
    }
    
    public boolean isCommandControlPressed(final Command command) {
        return this.getState(command).isPressed();
    }
    
    protected void firePressed(final Command command) {
        this.getState(command).down = true;
        this.getState(command).pressed = true;
        if (!this.isActive()) {
            return;
        }
        for (int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).controlPressed(command);
        }
    }
    
    protected void fireReleased(final Command command) {
        this.getState(command).down = false;
        if (!this.isActive()) {
            return;
        }
        for (int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).controlReleased(command);
        }
    }
    
    private class CommandState
    {
        private boolean down;
        private boolean pressed;
        
        public boolean isPressed() {
            if (this.pressed) {
                this.pressed = false;
                return true;
            }
            return false;
        }
        
        public boolean isDown() {
            return this.down;
        }
    }
    
    private class InputListenerImpl extends InputAdapter
    {
        public boolean isAcceptingInput() {
            return true;
        }
        
        public void keyPressed(final int key, final char c) {
            final Command command = InputProvider.this.commands.get(new KeyControl(key));
            if (command != null) {
                InputProvider.this.firePressed(command);
            }
        }
        
        public void keyReleased(final int key, final char c) {
            final Command command = InputProvider.this.commands.get(new KeyControl(key));
            if (command != null) {
                InputProvider.this.fireReleased(command);
            }
        }
        
        public void mousePressed(final int button, final int x, final int y) {
            final Command command = InputProvider.this.commands.get(new MouseButtonControl(button));
            if (command != null) {
                InputProvider.this.firePressed(command);
            }
        }
        
        public void mouseReleased(final int button, final int x, final int y) {
            final Command command = InputProvider.this.commands.get(new MouseButtonControl(button));
            if (command != null) {
                InputProvider.this.fireReleased(command);
            }
        }
        
        public void controllerLeftPressed(final int controller) {
            final Command command = InputProvider.this.commands.get(new ControllerDirectionControl(controller, ControllerDirectionControl.LEFT));
            if (command != null) {
                InputProvider.this.firePressed(command);
            }
        }
        
        public void controllerLeftReleased(final int controller) {
            final Command command = InputProvider.this.commands.get(new ControllerDirectionControl(controller, ControllerDirectionControl.LEFT));
            if (command != null) {
                InputProvider.this.fireReleased(command);
            }
        }
        
        public void controllerRightPressed(final int controller) {
            final Command command = InputProvider.this.commands.get(new ControllerDirectionControl(controller, ControllerDirectionControl.RIGHT));
            if (command != null) {
                InputProvider.this.firePressed(command);
            }
        }
        
        public void controllerRightReleased(final int controller) {
            final Command command = InputProvider.this.commands.get(new ControllerDirectionControl(controller, ControllerDirectionControl.RIGHT));
            if (command != null) {
                InputProvider.this.fireReleased(command);
            }
        }
        
        public void controllerUpPressed(final int controller) {
            final Command command = InputProvider.this.commands.get(new ControllerDirectionControl(controller, ControllerDirectionControl.UP));
            if (command != null) {
                InputProvider.this.firePressed(command);
            }
        }
        
        public void controllerUpReleased(final int controller) {
            final Command command = InputProvider.this.commands.get(new ControllerDirectionControl(controller, ControllerDirectionControl.UP));
            if (command != null) {
                InputProvider.this.fireReleased(command);
            }
        }
        
        public void controllerDownPressed(final int controller) {
            final Command command = InputProvider.this.commands.get(new ControllerDirectionControl(controller, ControllerDirectionControl.DOWN));
            if (command != null) {
                InputProvider.this.firePressed(command);
            }
        }
        
        public void controllerDownReleased(final int controller) {
            final Command command = InputProvider.this.commands.get(new ControllerDirectionControl(controller, ControllerDirectionControl.DOWN));
            if (command != null) {
                InputProvider.this.fireReleased(command);
            }
        }
        
        public void controllerButtonPressed(final int controller, final int button) {
            final Command command = InputProvider.this.commands.get(new ControllerButtonControl(controller, button));
            if (command != null) {
                InputProvider.this.firePressed(command);
            }
        }
        
        public void controllerButtonReleased(final int controller, final int button) {
            final Command command = InputProvider.this.commands.get(new ControllerButtonControl(controller, button));
            if (command != null) {
                InputProvider.this.fireReleased(command);
            }
        }
    }
}
