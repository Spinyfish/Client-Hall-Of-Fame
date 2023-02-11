package Realioty.server.utile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MyBufferedReader extends BufferedReader {
    public MyBufferedReader(Reader in, int sz) {
        super(in, sz);
    }

    public MyBufferedReader(Reader in) {
        super(in);
    }

    @Override
    public String readLine() throws IOException {
        try {
            String msg = super.readLine();
            msg = this.cleanStr(msg);

            Base64.Decoder decoder = Base64.getDecoder();
            msg = new String(decoder.decode(msg), StandardCharsets.UTF_8);

            msg = this.cleanStr(msg);
            return msg;
        } catch (Exception e) {

        }
        return null;
    }

    public String cleanStr(String str) {
        try {
            str = str.replaceAll("\n", "");
            str = str.replaceAll("\r", "");
            str = str.replaceAll("\t", "");
        } catch (Exception e) {
        }
        return str;
    }
}