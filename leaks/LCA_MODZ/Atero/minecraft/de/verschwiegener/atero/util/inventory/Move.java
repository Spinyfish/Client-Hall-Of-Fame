package de.verschwiegener.atero.util.inventory;

public class Move {

    int currentInvID;
    int targetInvID;

    public Move(int currentInvId, int targetInvId) {
        this.currentInvID = currentInvId;
        this.targetInvID = targetInvId;
    }

    public int getCurrentInvID() {
        return currentInvID;
    }
    public int getTargetInvID() {
        return targetInvID;
    }

}
