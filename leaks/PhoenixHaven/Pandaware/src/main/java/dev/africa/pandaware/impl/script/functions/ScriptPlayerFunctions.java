package dev.africa.pandaware.impl.script.functions;


import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.utils.player.MovementUtils;

public class ScriptPlayerFunctions {
    public double getBaseMoveSpeed() {
        return MovementUtils.getBaseMoveSpeed();
    }

    public void strafe() {
        MovementUtils.strafe();
    }

    public void strafe(MoveEvent event) {
        MovementUtils.strafe(event);
    }

    public void strafe(double movementSpeed) {
        MovementUtils.strafe(movementSpeed);
    }

    public void strafe(MoveEvent moveEvent, double movementSpeed) {
        MovementUtils.strafe(moveEvent, movementSpeed);
    }

    public double getSpeed() {
        return MovementUtils.getSpeed();
    }

    public double getSpeed(MoveEvent moveEvent) {
        return MovementUtils.getSpeed(moveEvent);
    }

    public double calculateLastDistance() {
        return MovementUtils.getLastDistance();
    }

    public double getDirection() {
        return MovementUtils.getDirection();
    }
}
