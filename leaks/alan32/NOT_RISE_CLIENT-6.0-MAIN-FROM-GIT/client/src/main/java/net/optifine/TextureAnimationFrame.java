package net.optifine;

public class TextureAnimationFrame {
    public int index;
    public int duration;
    public int counter;

    public TextureAnimationFrame(final int index, final int duration) {
        this.index = index;
        this.duration = duration;
        this.counter = 0;
    }
}
