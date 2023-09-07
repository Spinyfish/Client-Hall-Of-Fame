package optifine;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

public class Utils
{
    public static final String MAC_OS_HOME_PREFIX = "Library/Application Support";
    private static final char[] hexTable = new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private Utils()
    {
    }

    public static File getWorkingDirectory()
    {
        return getWorkingDirectory("minecraft");
    }

    public static File getWorkingDirectory(String applicationName)
    {
        String s = System.getProperty("user.home", ".");
        File file1 = null;

        switch (getPlatform().ordinal())
        {
            case 1:
            case 2:
                file1 = new File(s, '.' + applicationName + '/');
                break;

            case 3:
                String s1 = System.getenv("APPDATA");

                if (s1 != null)
                {
                    file1 = new File(s1, "." + applicationName + '/');
                }
                else
                {
                    file1 = new File(s, '.' + applicationName + '/');
                }

                break;

            case 4:
                file1 = new File(s, "Library/Application Support/" + applicationName);
                break;

            default:
                file1 = new File(s, applicationName + '/');
        }

        if (!file1.exists() && !file1.mkdirs())
        {
            throw new RuntimeException("The working directory could not be created: " + file1);
        }
        else
        {
            return file1;
        }
    }

    public static Utils.OS getPlatform()
    {
        String s = System.getProperty("os.name").toLowerCase();

        if (s.contains("win"))
        {
            return Utils.OS.WINDOWS;
        }
        else if (s.contains("mac"))
        {
            return Utils.OS.MACOS;
        }
        else if (s.contains("solaris"))
        {
            return Utils.OS.SOLARIS;
        }
        else if (s.contains("sunos"))
        {
            return Utils.OS.SOLARIS;
        }
        else if (s.contains("linux"))
        {
            return Utils.OS.LINUX;
        }
        else
        {
            return s.contains("unix") ? Utils.OS.LINUX : Utils.OS.UNKNOWN;
        }
    }

    public static int find(byte[] buf, byte[] pattern)
    {
        return find(buf, 0, pattern);
    }

    public static int find(byte[] buf, int index, byte[] pattern)
    {
        for (int i = index; i < buf.length - pattern.length; ++i)
        {
            boolean flag = true;

            for (int j = 0; j < pattern.length; ++j)
            {
                if (pattern[j] != buf[i + j])
                {
                    flag = false;
                    break;
                }
            }

            if (flag)
            {
                return i;
            }
        }

        return -1;
    }

    public static byte[] readAll(InputStream is) throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        byte[] abyte = new byte[1024];

        while (true)
        {
            int i = is.read(abyte);

            if (i < 0)
            {
                is.close();
                byte[] abyte1 = bytearrayoutputstream.toByteArray();
                return abyte1;
            }

            bytearrayoutputstream.write(abyte, 0, i);
        }
    }

    public static void dbg(String str)
    {
        System.out.println(str);
    }

    public static String[] tokenize(String str, String delim)
    {
        List list = new ArrayList();
        StringTokenizer stringtokenizer = new StringTokenizer(str, delim);

        while (stringtokenizer.hasMoreTokens())
        {
            String s = stringtokenizer.nextToken();
            list.add(s);
        }

        String[] astring = (String[]) list.toArray(new String[list.size()]);
        return astring;
    }

    public static String getExceptionStackTrace(Throwable e)
    {
        StringWriter stringwriter = new StringWriter();
        PrintWriter printwriter = new PrintWriter(stringwriter);
        e.printStackTrace(printwriter);
        printwriter.close();

        try
        {
            stringwriter.close();
        }
        catch (IOException var4)
        {
            ;
        }

        return stringwriter.getBuffer().toString();
    }

    public static void copyFile(File fileSrc, File fileDest) throws IOException
    {
        if (!fileSrc.getCanonicalPath().equals(fileDest.getCanonicalPath()))
        {
            FileInputStream fileinputstream = new FileInputStream(fileSrc);
            FileOutputStream fileoutputstream = new FileOutputStream(fileDest);
            copyAll(fileinputstream, fileoutputstream);
            fileoutputstream.flush();
            fileinputstream.close();
            fileoutputstream.close();
        }
    }

    public static void copyAll(InputStream is, OutputStream os) throws IOException
    {
        byte[] abyte = new byte[1024];

        while (true)
        {
            int i = is.read(abyte);

            if (i < 0)
            {
                return;
            }

            os.write(abyte, 0, i);
        }
    }

    public static void showMessage(String msg)
    {
        JOptionPane.showMessageDialog((Component)null, msg, "OptiFine", 1);
    }

    public static void showErrorMessage(String msg)
    {
        JOptionPane.showMessageDialog((Component)null, msg, "Error", 0);
    }

    public static String readFile(File file) throws IOException
    {
        return readFile(file, "ASCII");
    }

    public static String readFile(File file, String encoding) throws IOException
    {
        FileInputStream fileinputstream = new FileInputStream(file);
        return readText(fileinputstream, encoding);
    }

    public static String readText(InputStream in, String encoding) throws IOException
    {
        InputStreamReader inputstreamreader = new InputStreamReader(in, encoding);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        StringBuffer stringbuffer = new StringBuffer();

        while (true)
        {
            String s = bufferedreader.readLine();

            if (s == null)
            {
                bufferedreader.close();
                inputstreamreader.close();
                in.close();
                return stringbuffer.toString();
            }

            stringbuffer.append(s);
            stringbuffer.append("\n");
        }
    }

    public static String[] readLines(InputStream in, String encoding) throws IOException
    {
        String s = readText(in, encoding);
        String[] astring = tokenize(s, "\n\r");
        return astring;
    }

    public static void centerWindow(Component c, Component par)
    {
        if (c != null)
        {
            Rectangle rectangle = c.getBounds();
            Rectangle rectangle1;

            if (par != null && par.isVisible())
            {
                rectangle1 = par.getBounds();
            }
            else
            {
                Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                rectangle1 = new Rectangle(0, 0, dimension.width, dimension.height);
            }

            int j = rectangle1.x + (rectangle1.width - rectangle.width) / 2;
            int i = rectangle1.y + (rectangle1.height - rectangle.height) / 2;

            if (j < 0)
            {
                j = 0;
            }

            if (i < 0)
            {
                i = 0;
            }

            c.setBounds(j, i, rectangle.width, rectangle.height);
        }
    }

    public static String byteArrayToHexString(byte[] bytes)
    {
        if (bytes == null)
        {
            return "";
        }
        else
        {
            StringBuffer stringbuffer = new StringBuffer();

            for (int i = 0; i < bytes.length; ++i)
            {
                byte b0 = bytes[i];
                stringbuffer.append(hexTable[b0 >> 4 & 15]);
                stringbuffer.append(hexTable[b0 & 15]);
            }

            return stringbuffer.toString();
        }
    }

    public static String arrayToCommaSeparatedString(Object[] arr)
    {
        if (arr == null)
        {
            return "";
        }
        else
        {
            StringBuffer stringbuffer = new StringBuffer();

            for (int i = 0; i < arr.length; ++i)
            {
                Object object = arr[i];

                if (i > 0)
                {
                    stringbuffer.append(", ");
                }

                if (object == null)
                {
                    stringbuffer.append("null");
                }
                else if (!object.getClass().isArray())
                {
                    stringbuffer.append(arr[i]);
                }
                else
                {
                    stringbuffer.append("[");

                    if (object instanceof Object[])
                    {
                        Object[] aobject = (Object[]) object;
                        stringbuffer.append(arrayToCommaSeparatedString(aobject));
                    }
                    else
                    {
                        for (int j = 0; j < Array.getLength(object); ++j)
                        {
                            if (j > 0)
                            {
                                stringbuffer.append(", ");
                            }

                            stringbuffer.append(Array.get(object, j));
                        }
                    }

                    stringbuffer.append("]");
                }
            }

            return stringbuffer.toString();
        }
    }

    public static String removePrefix(String str, String prefix)
    {
        if (str != null && prefix != null)
        {
            if (str.startsWith(prefix))
            {
                str = str.substring(prefix.length());
            }

            return str;
        }
        else
        {
            return str;
        }
    }

    public static String removePrefix(String str, String[] prefixes)
    {
        if (str != null && prefixes != null)
        {
            int i = str.length();

            for (int j = 0; j < prefixes.length; ++j)
            {
                String s = prefixes[j];
                str = removePrefix(str, s);

                if (str.length() != i)
                {
                    break;
                }
            }

            return str;
        }
        else
        {
            return str;
        }
    }

    public static String removeSuffix(String str, String suffix)
    {
        if (str != null && suffix != null)
        {
            if (str.endsWith(suffix))
            {
                str = str.substring(0, str.length() - suffix.length());
            }

            return str;
        }
        else
        {
            return str;
        }
    }

    public static String removeSuffix(String str, String[] suffixes)
    {
        if (str != null && suffixes != null)
        {
            int i = str.length();

            for (int j = 0; j < suffixes.length; ++j)
            {
                String s = suffixes[j];
                str = removeSuffix(str, s);

                if (str.length() != i)
                {
                    break;
                }
            }

            return str;
        }
        else
        {
            return str;
        }
    }

    public static String ensurePrefix(String str, String prefix)
    {
        if (str != null && prefix != null)
        {
            if (!str.startsWith(prefix))
            {
                str = prefix + str;
            }

            return str;
        }
        else
        {
            return str;
        }
    }

    public static boolean equals(Object o1, Object o2)
    {
        if (o1 == o2)
        {
            return true;
        }
        else
        {
            return o1 == null ? false : o1.equals(o2);
        }
    }

    public static int parseInt(String str, int defVal)
    {
        try
        {
            if (str == null)
            {
                return defVal;
            }
            else
            {
                str = str.trim();
                return Integer.parseInt(str);
            }
        }
        catch (NumberFormatException var3)
        {
            return defVal;
        }
    }

    public static boolean equalsMask(String str, String mask, char wildChar)
    {
        if (mask != null && str != null)
        {
            if (mask.indexOf(wildChar) < 0)
            {
                return mask.equals(str);
            }
            else
            {
                List list = new ArrayList();
                String s = "" + wildChar;

                if (mask.startsWith(s))
                {
                    list.add("");
                }

                StringTokenizer stringtokenizer = new StringTokenizer(mask, s);

                while (stringtokenizer.hasMoreElements())
                {
                    list.add(stringtokenizer.nextToken());
                }

                if (mask.endsWith(s))
                {
                    list.add("");
                }

                String s1 = (String)list.get(0);

                if (!str.startsWith(s1))
                {
                    return false;
                }
                else
                {
                    String s2 = (String)list.get(list.size() - 1);

                    if (!str.endsWith(s2))
                    {
                        return false;
                    }
                    else
                    {
                        int i = 0;

                        for (int j = 0; j < list.size(); ++j)
                        {
                            String s3 = (String)list.get(j);

                            if (s3.length() > 0)
                            {
                                int k = str.indexOf(s3, i);

                                if (k < 0)
                                {
                                    return false;
                                }

                                i = k + s3.length();
                            }
                        }

                        return true;
                    }
                }
            }
        }
        else
        {
            return mask == str;
        }
    }

    public static Object[] addObjectToArray(Object[] arr, Object obj)
    {
        if (arr == null)
        {
            throw new NullPointerException("The given array is NULL");
        }
        else
        {
            int i = arr.length;
            int j = i + 1;
            Object[] aobject = (Object[]) Array.newInstance(arr.getClass().getComponentType(), j);
            System.arraycopy(arr, 0, aobject, 0, i);
            aobject[i] = obj;
            return aobject;
        }
    }

    public static Object[] addObjectToArray(Object[] arr, Object obj, int index)
    {
        List list = new ArrayList<>(Arrays.asList(arr));
        list.add(index, obj);
        Object[] aobject = (Object[]) Array.newInstance(arr.getClass().getComponentType(), list.size());
        return list.toArray(aobject);
    }

    public static Object[] addObjectsToArray(Object[] arr, Object[] objs)
    {
        if (arr == null)
        {
            throw new NullPointerException("The given array is NULL");
        }
        else if (objs.length == 0)
        {
            return arr;
        }
        else
        {
            int i = arr.length;
            int j = i + objs.length;
            Object[] aobject = (Object[]) Array.newInstance(arr.getClass().getComponentType(), j);
            System.arraycopy(arr, 0, aobject, 0, i);
            System.arraycopy(objs, 0, aobject, i, objs.length);
            return aobject;
        }
    }

    public static Object[] removeObjectFromArray(Object[] arr, Object obj)
    {
        List list = new ArrayList<>(Arrays.asList(arr));
        list.remove(obj);
        Object[] aobject = collectionToArray(list, arr.getClass().getComponentType());
        return aobject;
    }

    public static Object[] collectionToArray(Collection coll, Class elementClass)
    {
        if (coll == null)
        {
            return null;
        }
        else if (elementClass == null)
        {
            return null;
        }
        else if (elementClass.isPrimitive())
        {
            throw new IllegalArgumentException("Can not make arrays with primitive elements (int, double), element class: " + elementClass);
        }
        else
        {
            Object[] aobject = (Object[]) Array.newInstance(elementClass, coll.size());
            return coll.toArray(aobject);
        }
    }

    public static enum OS
    {
        LINUX,
        SOLARIS,
        WINDOWS,
        MACOS,
        UNKNOWN;
    }
}
