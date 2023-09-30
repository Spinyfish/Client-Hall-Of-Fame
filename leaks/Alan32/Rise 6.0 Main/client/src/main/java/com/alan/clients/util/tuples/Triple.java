package com.alan.clients.util.tuples;

/**
 * This is property of Error.
 * All rights reserved.
 * I hereby grant permission to use this code in Rise 6.0 and all future versions.
 * I do not grant permission to use this code in any other client.
 **/

public class Triple<A, B, C> {

    private A first;
    private B second;
    private C third;

    public Triple(A first, B second, C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public A getFirst() {
        return first;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public B getSecond() {
        return second;
    }

    public void setSecond(B second) {
        this.second = second;
    }

    public C getThird() {
        return third;
    }

    public void setThird(C third) {
        this.third = third;
    }
}