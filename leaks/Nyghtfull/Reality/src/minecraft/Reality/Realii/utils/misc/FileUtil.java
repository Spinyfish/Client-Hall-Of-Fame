package Reality.Realii.utils.misc;

import net.minecraft.client.Minecraft;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static File mcDir = Minecraft.getMinecraft().mcDataDir;
    public static File dir = new File(mcDir, "Reality Client");

    public FileUtil() {
        if (!FileUtil.dir.exists()) {
            FileUtil.dir.mkdir();
        }
    }

    public static void copyFile(String srcPathStr, String desPathStr) {
        //1.èŽ·å�–æº�æ–‡ä»¶çš„å��ç§°
        String newFileName = srcPathStr.substring(srcPathStr.lastIndexOf("\\") + 1); //ç›®æ ‡æ–‡ä»¶åœ°å�€
        System.out.println(newFileName);
        desPathStr = desPathStr + File.separator + newFileName; //æº�æ–‡ä»¶åœ°å�€
        System.out.println(desPathStr);

        try {
            //2.åˆ›å»ºè¾“å…¥è¾“å‡ºæµ�å¯¹è±¡
            FileInputStream fis = new FileInputStream(srcPathStr);
            FileOutputStream fos = new FileOutputStream(desPathStr);

            //åˆ›å»ºæ�¬è¿�å·¥å…·
            byte datas[] = new byte[1024 * 8];
            //åˆ›å»ºé•¿åº¦
            int len = 0;
            //å¾ªçŽ¯è¯»å�–æ•°æ�®
            while ((len = fis.read(datas)) != -1) {
                fos.write(datas, 0, len);
            }
            //3.é‡Šæ”¾èµ„æº�
            fis.close();
            fis.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String readFile(String fileName) {
        StringBuilder result = new StringBuilder();

        try {
            File file = new File(FileUtil.dir, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileInputStream fIn = new FileInputStream(file);
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn))) {
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    result.append(str);
                    result.append(System.lineSeparator());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }
    public static String readFile(String dir,String fileName) {
        StringBuilder result = new StringBuilder();

        try {
            File file = new File(dir, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileInputStream fIn = new FileInputStream(file);
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fIn))) {
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    result.append(str);
                    result.append(System.lineSeparator());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static List<String> read(String file) {
        final List<String> out = new ArrayList<>();
        try {
            if (!FileUtil.dir.exists()) {
                FileUtil.dir.mkdir();
            }
            final File f = new File(FileUtil.dir, file);
            if (!f.exists()) {
                f.createNewFile();
            }
            Throwable t = null;
            try {
                final FileInputStream fis = new FileInputStream(f);
                try {
                    final InputStreamReader isr = new InputStreamReader(fis);
                    try {
                        final BufferedReader br = new BufferedReader(isr);
                        try {
                            String line = "";
                            while ((line = br.readLine()) != null) {
                                out.add(line);
                            }
                        } finally {
                            if (br != null) {
                                br.close();
                            }
                        }
                        if (isr != null) {
                            isr.close();
                        }
                    } finally {
                        if (t == null) {
                            final Throwable t2 = null;
                            t = t2;
                        } else {
                            final Throwable t2 = null;
                            if (t != t2) {
                                t.addSuppressed(t2);
                            }
                        }
                        if (isr != null) {
                            isr.close();
                        }
                    }
                    if (fis != null) {
                        fis.close();
                        return out;
                    }
                } finally {
                    if (t == null) {
                        final Throwable t3 = null;
                        t = t3;
                    } else {
                        final Throwable t3 = null;
                        if (t != t3) {
                            t.addSuppressed(t3);
                        }
                    }
                    if (fis != null) {
                        fis.close();
                    }
                }
            } finally {
                if (t == null) {
                    final Throwable t4 = null;
                    t = t4;
                } else {
                    final Throwable t4 = null;
                    if (t != t4) {
                        t.addSuppressed(t4);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }



    public static void save(final String file, final String content, final boolean append) {
        try {
            final File f = new File(FileUtil.dir, file);
            if (!f.exists()) {
                f.createNewFile();
            }
            Throwable t = null;
            try {
                final FileWriter writer = new FileWriter(f, append);
                try {
                    writer.write(content);
                } finally {
                    if (writer != null) {
                        writer.close();
                    }
                }
            } finally {
                if (t == null) {
                    final Throwable t2 = null;
                    t = t2;
                } else {
                    final Throwable t2 = null;
                    if (t != t2) {
                        t.addSuppressed(t2);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveFile(String fileName, String context) {
        File dir = new File(mcDir, "Lune");
        File file = new File(dir, fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(context);
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void saveFile(String dir,String fileName, String context) {
        File file = new File(dir, fileName);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(context);
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static List<File> getFiles(String path) {
    	List<File> files = new ArrayList<File>();
    	File pathFile = new File(path);
    	
		File[] filesInPath = pathFile.listFiles();
		for(File file : filesInPath) {
	    	if (file.isDirectory()) {
	    		files.addAll(getFiles(file.getAbsolutePath()));
	    	} else {
	    		files.add(file);
	    	}
		}
    	
		return files;
    }



}
