/**
 * @project Myth
 * @author Skush/Duzey
 * @at 13.08.2022
 */
package dev.myth.api.utils.process;

import java.util.Scanner;

public class ProcessUtil {

    // Check if a process is running
    public static boolean isProcessRunning(String processName) {
        try {
            String line;
            Process p = Runtime.getRuntime().exec("tasklist.exe");

            p.getOutputStream().close();
            Scanner s = new Scanner(p.getInputStream());
            while (s.hasNext()) {
                line = s.nextLine();
                if (line.contains(processName))
                    return true;
            }
            s.close();
        } catch (Exception ignored) {}
        return false;
    }
}
