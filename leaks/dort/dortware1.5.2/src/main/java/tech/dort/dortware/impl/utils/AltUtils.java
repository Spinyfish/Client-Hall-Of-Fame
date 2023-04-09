package tech.dort.dortware.impl.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author - Aidan#1337
 * @created 9/27/2020 at 7:36 PM
 * Do not distribute this code without credit
 * or ill get final on ur ass
 */

public class AltUtils {

    public static String genCracked() {
        String name = AltUtils.generateName();
        Minecraft.getMinecraft().session = new Session(name, "", "", "mojang");
        return name;
    }

    /**
     * yes
     *
     * @return the generated name
     */
    public static String generateName() {
        return RandomStringUtils.random(16, "abcdefghijklmnopqrstuvwxyz0123456789");
    }

    /**
     * yes
     *
     * @return the generated string
     */
    public static String generateString() {
        return RandomStringUtils.random(16, "abcdefghijklmnopqrstuvwxyz0123456789");
    }
}
