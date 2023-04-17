package me.levansj01.verus.storage.database.check;

import java.util.ArrayList;
import java.util.List;
import me.levansj01.verus.check.Check;

public class CheckValues {
    private boolean punish;
    private final String checkId;
    private boolean alert;
    private List<String> commands;
    private int maxViolation;

    public boolean isPunish() {
        return this.punish;
    }

    public boolean hasCommands() {
        return !this.commands.isEmpty();
    }

    public void removeCommand(int command) {
        this.commands.remove(command);
    }

    public void addCommand(String command) {
        this.commands.add(command);
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }

    public List<String> getCommands() {
        return this.commands;
    }

    public String getCheckId() {
        return this.checkId;
    }

    public boolean isAlert() {
        return this.alert;
    }

    public void setPunish(boolean bool) {
        this.punish = bool;
    }

    public CheckValues(Check violation) {
        this(violation.identifier());
        this.maxViolation = violation.getMaxViolation();
    }

    public CheckValues(String checkId) {
        this.punish = true;
        this.alert = true;
        this.maxViolation = 2147483647;
        this.commands = new ArrayList<>();
        this.checkId = checkId;
    }

    public boolean hasCommand(String command) {
        return this.commands.contains(command);
    }

    public int getCommandsSize() {
        return this.commands.size();
    }

    public void setAlert(boolean bool) {
        this.alert = bool;
    }

    public String getCommand(int command) {
        return this.commands.get(command);
    }

    public int getMaxViolation() {
        return this.maxViolation;
    }

    public void setMaxViolation(int value) {
        this.maxViolation = value;
    }
}
