/*
 * This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kevin.utils;

import java.awt.*;
import java.util.Objects;

public class MilkUtils {
    public static float[] lastFraction = new float[]{};
    public static Color[] lastColors = new Color[]{};
    static int blendAmount;
    public static Color[] getColor(String mode){
        switch (mode){
            case "Stichh":
                return Stichh;
            case "Night":
                return lol;
            case "2":
                return _2;
            case "3":
                return _3;
        }
        return lol;
    }
    static public Color[] Stichh=new Color[]{
            new Color(150, 143, 253),
            new Color(62, 65, 224, 255),
            new Color(203, 208, 246)
    };
    static public Color[] _3=new Color[]{
            new Color(255, 56, 56),
            new Color(102, 62, 224, 255),
            new Color(255, 226, 35)
    };
    static public Color[] _2=new Color[]{
            new Color(56, 255, 179),
            new Color(62, 162, 224, 255),
            new Color(35, 162, 255)
    };
    static public Color[] lol=new Color[]{
            new Color(150, 143, 253),
            new Color(147, 67, 168),
            new Color(76, 147, 169)
    };

    public static int[] getFractionIndices(float[] fractions, float progress) {
        int startPoint;
        int[] range = new int[2];
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
        }
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }
    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
        if (fractions.length == colors.length) {
            int[] indices = getFractionIndices(fractions, progress);
            float[] range = new float[]{fractions[indices[0]], fractions[indices[1]]};
            Color[] colorRange = new Color[]{colors[indices[0]], colors[indices[1]]};
            float max = range[1] - range[0];
            float value = progress - range[0];
            float weight = value / max;
            return ColorUtils.blend(colorRange[0], colorRange[1], (double)(1.0F - weight));
        } else {
            throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
        }
    }
    public static String lastMode = "";
    public static Color getMixedColor(int index, int seconds) {
        if (lastColors.length <= 0 || lastFraction.length <= 0) regenerateColors(true, "Stichh");
        // just to make sure it won't go white

        return blendColors(lastFraction, lastColors, (System.currentTimeMillis() + index) % (seconds * 1000) / (float) (seconds * 1000));
    }
    public static Color getMixedColor(int index, int seconds, String mode) {
        if(!Objects.equals(lastMode, mode)){
            lastColors = new Color[]{};
            lastFraction = new float[]{};
            regenerateColors(true, mode);
            lastMode = mode;
        }

        if (lastColors.length <= 0 || lastFraction.length <= 0) regenerateColors(true, mode); // just to make sure it won't go white

        return blendColors(
                lastFraction, lastColors, (System.currentTimeMillis() + index) % (seconds * 1000) / (float) (seconds * 1000));
    }

    public static void regenerateColors(boolean forceValue, String mode) {
        blendAmount=getColor(mode).length;

        // color generation
        if (forceValue || lastColors.length != (blendAmount * 2) - 1) {
            Color[] generator = new Color[(blendAmount * 2) - 1];

            // reflection is cool
            for (int i = 0; i < blendAmount; i++) {
                Color result = Color.white;
                try {
                            result = new Color(Math.max(0, Math.min(getColor(mode)[i].getRed(), 255)),
                                    Math.max(0, Math.min(getColor(mode)[i].getGreen(), 255)),
                                    Math.max(0, Math.min(getColor(mode)[i].getBlue(), 255)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                generator[i] = result;
            }

            int h = blendAmount;
            for (int z = blendAmount - 2; z >= 0; z--) {
                generator[h] = generator[z];
                h++;
            }

            lastColors = generator;
        }

        // cache thingy
        if (forceValue || lastFraction.length != (blendAmount * 2) - 1) {
            // color frac regenerate if necessary
            float[] colorFraction = new float[(blendAmount * 2) - 1];

            for (int i = 0; i <= (blendAmount * 2) - 2; i++)
            {
                colorFraction[i] = (float)i / (float)((blendAmount * 2) - 2);
            }

            lastFraction = colorFraction;
        }
    }
}
