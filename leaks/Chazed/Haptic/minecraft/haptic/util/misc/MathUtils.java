package haptic.util.misc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class MathUtils {
	
	private MathUtils() {
		
    }

    public static double round(final double value, final double inc) {
        if (inc == 0.0) return value;
        else if (inc == 1.0) return Math.round(value);
        else {
            final double halfOfInc = inc / 2.0;
            final double floored = Math.floor(value / inc) * inc;

            if (value >= floored + halfOfInc)
                return new BigDecimal(Math.ceil(value / inc) * inc)
                    .doubleValue();
            else return new BigDecimal(floored)
                .doubleValue();
        }
    }

    public static float getRandomInRange(float min, float max) {
        Random random = new Random();
        double range = max - min;
        double scaled = random.nextDouble() * range;
        if (scaled > max) {
            scaled = max;
        }
        double shifted = scaled + min;

        if (shifted > max) {
            shifted = max;
        }
        return (float) shifted;
    }

    public static byte[] getRandomBytes(int minSize, int maxSize, byte min, byte max) {
        int size = getRandom_int(minSize, maxSize);
        final byte[] out = new byte[size];
        for (int i = 0; i < size; i++) {
            out[i] = getRandomByte(min, max);
        }
        return out;
    }

    public static int getRandom_int(int min, int max) {
        if(min > max) return min;
        Random RANDOM = new Random();
        return RANDOM.nextInt(max) + min;
    }

    private static byte getRandomByte(byte min, byte max) {
        if(min > max) return min;
        Random RANDOM = new Random();
        return (byte) (RANDOM.nextInt(max) + min);
    }

    public static float[][] getArcVertices(final float radius,
                                           final float angleStart,
                                           final float angleEnd,
                                           final int segments) {
        final float range = Math.max(angleStart, angleEnd) - Math.min(angleStart, angleEnd);
        final int nSegments = Math.max(2, Math.round((360.f / range) * segments));
        final float segDeg = range / nSegments;

        final float[][] vertices = new float[nSegments + 1][2];
        for (int i = 0; i <= nSegments; i++) {
            final float angleOfVert = (angleStart + i * segDeg) / 180.f * (float) Math.PI;
            vertices[i][0] = ((float) Math.sin(angleOfVert)) * radius;
            vertices[i][1] = ((float) -Math.cos(angleOfVert)) * radius;
        }

        return vertices;
    }

    public static double distance(final double srcX, final double srcY, final double srcZ,
                                  final double dstX, final double dstY, final double dstZ) {
        final double xDist = dstX - srcX;
        final double yDist = dstY - srcY;
        final double zDist = dstZ - srcZ;
        return Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
    }
    
    public static double getDifference(final double x, final double x2) {
    	return Math.round(x - x2);
    }

    public static double distance(final double srcX, final double srcZ,
                                  final double dstX, final double dstZ) {
        final double xDist = dstX - srcX;
        final double zDist = dstZ - srcZ;
        return Math.sqrt(xDist * xDist + zDist * zDist);
    }
	
    public static double randomNumber(double max, double min) {
        return (Math.random() * (max - min)) + min;
    }


    public static double square(double d) {
        d *= d;
        return d;
    }

}
