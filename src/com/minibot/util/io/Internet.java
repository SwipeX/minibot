package com.minibot.util.io;

import com.minibot.util.OS;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tyler Sedlar
 */
public class Internet {

    public static final String QUOTE = "\"";
    public static final String AMPERSAND = "&";

    public static final int BUFFER_SIZE = 8192;

    private static final String DEFAULT_USER_AGENT;

    private static int timeout = -1;

    static {
        DEFAULT_USER_AGENT = getDefaultHttpUserAgent();
    }

    public static void setTimeout(int timeout) {
        Internet.timeout = timeout;
    }

    public static String fixEncoding(String string) {
        return string.replaceAll("&quot;", QUOTE).replaceAll("&amp;", AMPERSAND);
    }

    public static String fileEncoding(String string) {
        return string.replaceAll(QUOTE, "'");
    }

    public static String getDefaultHttpUserAgent() {
        return "Mozilla/5.0 (" + OS.get().getUserAgentPart() + ")" +
                " AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17";
    }

    public static URLConnection mask(URLConnection url) {
        url.setRequestProperty("User-Agent", DEFAULT_USER_AGENT);
        return url;
    }

    public static String post(String url, String... args) {
        try {
            StringBuilder builder = new StringBuilder();
            boolean first = true;
            for (String arg : args) {
                if (first) {
                    builder.append(arg);
                    first = false;
                } else {
                    builder.append("&").append(arg);
                }
            }
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("User-Agent", getDefaultHttpUserAgent());
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(builder.toString());
            writer.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                result.append(line);
            return result.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public static List<String> read(String site, boolean mask) {
        try {
            URL url = new URL(site);
            URLConnection connection = url.openConnection();
            if (mask)
                connection = mask(connection);
            if (timeout != -1)
                connection.setConnectTimeout(timeout);
            try (InputStream stream = connection.getInputStream()) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                List<String> source = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null)
                    source.add(line);
                return source;
            } catch (IOException e) {
                //e.printStackTrace();
                return null;
            }
        } catch (IOException e) {
            //e.printStackTrace();
            return null;
        }
    }

    public static List<String> read(String site) {
        return read(site, true);
    }

    public static String readFully(String site) {
        try (InputStream stream = new URL(site).openStream()) {
            return new String(downloadBinary(stream, null));
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] downloadBinary(InputStream in, InternetCallback manager) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buf = new byte[BUFFER_SIZE];
            int downloaded = 0;
            int n;
            while ((n = in.read(buf, 0, BUFFER_SIZE)) > 0) {
                out.write(buf, 0, n);
                downloaded += n;
                if (manager != null && manager.getLength() != -1)
                    manager.onDownload(((downloaded * 100) / manager.getLength()));
            }
            if (manager != null)
                manager.setLength(-1);
            return out.toByteArray();
        }
    }

    public static File download(String site, String target, InternetCallback manager, boolean mask) {
        try {
            URL url = new URL(site);
            URLConnection connection = url.openConnection();
            if (mask)
                connection = mask(connection);
            if (timeout != -1)
                connection.setConnectTimeout(timeout);
            if (manager != null)
                manager.setLength(connection.getContentLength());
            try (InputStream stream = connection.getInputStream()) {
                File file = new File(target);
                file.getParentFile().mkdirs();
                try (FileOutputStream out = new FileOutputStream(file)) {
                    out.write(downloadBinary(stream, manager));
                    if (manager != null)
                        manager.setLength(-1);
                    return file;
                }
            } catch (IOException e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static File download(String site, String target, InternetCallback manager) {
        return download(site, target, manager, true);
    }

    public static BufferedImage readImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            return null;
        }
    }
}