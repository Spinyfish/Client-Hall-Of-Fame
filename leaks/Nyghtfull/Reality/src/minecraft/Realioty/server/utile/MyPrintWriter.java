package Realioty.server.utile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MyPrintWriter extends PrintWriter {
    public MyPrintWriter(Writer out) {
        super(out);
    }

    public MyPrintWriter(Writer out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public MyPrintWriter(OutputStream out) {
        super(out);
    }

    public MyPrintWriter(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public MyPrintWriter(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public MyPrintWriter(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    public MyPrintWriter(File file) throws FileNotFoundException {
        super(file);
    }

    public MyPrintWriter(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }

    @Override
    public void println(String x) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] msgByte = x.getBytes(StandardCharsets.UTF_8);

        x = encoder.encodeToString(msgByte);

        super.println(x);
    }
}
