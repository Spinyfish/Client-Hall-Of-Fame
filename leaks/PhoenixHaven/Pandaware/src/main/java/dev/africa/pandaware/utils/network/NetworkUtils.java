package dev.africa.pandaware.utils.network;

import dev.africa.pandaware.utils.client.Printer;
import lombok.experimental.UtilityClass;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import static dev.africa.pandaware.api.interfaces.MinecraftInstance.mc;

@UtilityClass
public class NetworkUtils {
    public final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36";

    public boolean uploadFile(String url, String path, File file) throws Exception {
        String charset = "UTF-8";
        String param = "value";
        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";

        URLConnection connection = new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (OutputStream output = connection.getOutputStream()) {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);

            writer.append("--").append(boundary).append(CRLF);

            writer.append("Content-Disposition: form-data; name=\"")
                    .append(path).append("\"; filename=\"").append(file.getName()).append("\"").append(CRLF);
            writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(file.getName())).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);

            writer.append(CRLF).flush();
            Files.copy(file.toPath(), output);
            output.flush();
            writer.append(CRLF).flush();

            writer.append("--").append(boundary).append("--").append(CRLF).flush();
        }

        return ((HttpURLConnection) connection).getResponseCode() == 200;
    }

    public String getFromURL(String url, Map<String, String> headers, boolean newLine) throws IOException {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();

        connection.setRequestProperty("User-Agent", USER_AGENT);

        if (headers != null) {
            headers.forEach(connection::setRequestProperty);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);

            if (newLine) {
                response.append("\n");
            }
        }

        in.close();

        return response.toString();
    }

    public String getIpAddress() {
        String ip = "";
        try {
            URL ama = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(ama.openStream()));
            ip = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ip;
    }

    public String createHasteBin(String text, boolean raw) {
        String response = null;

        try {
            byte[] postData = text.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            String requestURL = "https://hastebin.com/documents";
            URL url = new URL(requestURL);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Hastebin Java Api");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);

            DataOutputStream wr;
            try {
                wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                response = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response != null && response.contains("\"key\"")) {
                response = response.substring(response.indexOf(":") + 2, response.length() - 2);

                response = (raw ? "https://hastebin.com/raw/" : "https://hastebin.com/") + response;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public boolean isResourcePackValid(NetworkManager networkManager, String url, String hash) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();

            if (!scheme.equals("http") && !scheme.equals("https") && !scheme.equals("level")) {
                networkManager.sendPacket(new C19PacketResourcePackStatus(hash,
                        C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));

                throw new URISyntaxException(url, "Wrong protocol");
            }

            url = URLDecoder.decode(url.substring("level://".length()), StandardCharsets.UTF_8.toString());

            if (scheme.equals("level") && (url.contains("..") || !url.endsWith("/resources.zip"))) {
                if (mc.getCurrentServerData() != null && (!mc.getCurrentServerData().serverIP.endsWith("mineland.net") ||
                        !mc.getCurrentServerData().serverIP.endsWith("join-ml.com")) &&
                        !(mc.currentScreen instanceof GuiMultiplayer)) { // We do this bc mineland likes touching files more than tenebrous likes touching kids
                    Printer.chat("§c§lThe current server has attempted to access §7" + url);
                }

                throw new URISyntaxException(url, "Invalid levelstorage resourcepack path");
            }

            return true;
        } catch (URISyntaxException e) {
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return false;
    }
}
