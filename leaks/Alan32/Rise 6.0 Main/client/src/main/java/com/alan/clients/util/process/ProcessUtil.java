package com.alan.clients.util.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessUtil {
    public static boolean running(String name) {
        try {
            String line;
            StringBuilder pidInfo = new StringBuilder();

            Process p;

            p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");

            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }

            input.close();

            return pidInfo.toString().toLowerCase().contains(name.toLowerCase());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
