package me.levansj01.verus.compat.api;

public interface Transactionable
{
    short id();
    
    default boolean valid() {
        return true;
    }
}
