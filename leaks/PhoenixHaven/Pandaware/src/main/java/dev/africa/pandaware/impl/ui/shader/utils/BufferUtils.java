package dev.africa.pandaware.impl.ui.shader.utils;

import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@UtilityClass
public class BufferUtils {
    public String readFile(String path) {
        InputStream inputStream = BufferUtils.class.getResourceAsStream(path);
        StringBuilder stringBuilder = new StringBuilder();

        if (inputStream != null) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }

                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }
}