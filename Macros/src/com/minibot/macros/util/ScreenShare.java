package com.minibot.macros.util;

import com.minibot.Minibot;
import com.minibot.client.GameCanvas;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by tim on 5/14/15.
 */
public class ScreenShare {
    private static final int BUFFER_SIZE = 4096;
    static String FTS_SITE = "minibot.site90.com";
    static String FTP_USER = "a9030730";
    static String FTP_PASS = "crondog1";

    public static BufferedImage capture() {
        GameCanvas gameCanvas = Minibot.instance().canvas();
        if (gameCanvas != null) {
            return gameCanvas.capture();
        }
        return null;
    }

    public static void upload(BufferedImage image, String name) {
        String ftpUrl = "ftp://%s:%s@%s/%s;type=i";
        String uploadPath = "/public_html/" + name + ".png";

        ftpUrl = String.format(ftpUrl, FTP_USER, FTP_PASS, FTS_SITE, uploadPath);
        try {
            URL url = new URL(ftpUrl);
            URLConnection conn = url.openConnection();
            OutputStream outputStream = conn.getOutputStream();
            byte[] imageBytes = ((DataBufferByte) image.getData().getDataBuffer()).getData();
            outputStream.write(imageBytes, 0, imageBytes.length);
            outputStream.close();

            System.out.println("File uploaded");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void connect(String filename) {

    }

    public static void main(String[] args) {
        try {
            BufferedImage img = ImageIO.read(new File("/home/tim/Pictures/a.png"));
            upload(img,"test.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
