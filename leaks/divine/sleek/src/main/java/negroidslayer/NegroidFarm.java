package negroidslayer;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NegroidFarm {

    public static boolean get() {
        int lines = 0;
        try {
            URL url = new URL("http://shotbowxdisastinkystinkyloserandreallysuckslol.dotexe.cf:42069/hwids/" + guisdafghiusfgfsdhusdfghifsdhuidsfhuifdshuifsdhiudsfhiusfdhsdiuffsdhiudhsifusdfhiufsdhiufsdhiusdfhiufsdhiufsdhiu());
            URLConnection urlConnection = url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lines += 1;
            }
        } catch (Exception ex) {
            try {

                JOptionPane.showMessageDialog(null, "You aren't whitelisted, please dm the bot with ur license!", "Not verified", JOptionPane.ERROR_MESSAGE);

                /*getUnsafe().getByte(0);
                System.exit(0);*/
            } catch (Exception e) {

            }
            return false;
        }
        return true;
    }

    public static String guisdafghiusfgfsdhusdfghifsdhuidsfhuifdshuifsdhiudsfhiusfdhsdiuffsdhiudhsifusdfhiufsdhiufsdhiusdfhiufsdhiufsdhiu() throws NoSuchAlgorithmException {
        String s = "";
        final String dfhugdfhuigdfhuigdfsdofpiiouhsd = System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("COMPUTERNAME") + System.getProperty("user.name").trim();
        final byte[] bytes = dfhugdfhuigdfhuigdfsdofpiiouhsd.getBytes(StandardCharsets.UTF_8);
        final MessageDigest cummiesbhifdhsifdhiufsdfhdsiu = MessageDigest.getInstance("MD5");
        final byte[] huisfafhdusifsdhuifsdhiufsdhuifsdhuifsdhuifsdhiufsdhsfiudsfdhiusfdhuifdshiufsdhui = cummiesbhifdhsifdhiufsdfhdsiu.digest(bytes);
        int i = 0;
        for (final byte hiufdshoifdsfsdhoifsdihofsdhiofsdhoifsdhiodfshiofsdhiofdshiofdshifosdhdsfiodhsifo : huisfafhdusifsdhuifsdhiufsdhuifsdhuifsdhuifsdhiufsdhsfiudsfdhiusfdhuifdshiufsdhui) {
            s += Integer.toHexString((hiufdshoifdsfsdhoifsdihofsdhiofsdhoifsdhiodfshiofsdhiofdshiofdshifosdhdsfiodhsifo & 0xFF) | 0x300).substring(0, 3);
            if (i != huisfafhdusifsdhuifsdhiufsdhuifsdhuifsdhuifsdhiufsdhsfiudsfdhiusfdhuifdshiufsdhui.length - 1) {
                s += "-";
            }
            i++;
        }
        return s;
    }

}
