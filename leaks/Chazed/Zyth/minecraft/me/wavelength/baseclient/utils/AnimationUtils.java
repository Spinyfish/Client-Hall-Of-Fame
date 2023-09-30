package me.wavelength.baseclient.utils;

public class AnimationUtils {

    /*
        This function gets the percent of animation completion,
        eg: int startTimeMS = System.currentTimeMillis(); double percentage = getPercentage(1000, startTimeMS);
     */
    public static double getPercentage(double animationLengthMS, long startSysMS){
        double time = System.currentTimeMillis() - startSysMS;
        return (time / animationLengthMS) * 100;
    }

    public static double getDoubleFromPercentage(double percentage, double size) {
        return (size / 100 * percentage);
    }
}