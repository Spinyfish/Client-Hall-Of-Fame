package me.levansj01.verus.api.check;

public class InvalidCheckException extends Exception {

    private final Check check;

    public InvalidCheckException(Check check) {
        super("Invalid check object");
        this.check = check;
    }

    public Check getCheck() {
        return this.check;
    }
}