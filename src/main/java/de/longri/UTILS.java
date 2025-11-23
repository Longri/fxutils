/*
 * Copyright (C) 2024 Longri
 *
 * This file is part of fxutils.
 *
 * fxutils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * fxutils is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with fxutils. If not, see <https://www.gnu.org/licenses/>.
 */
package de.longri;


import de.longri.gdx_utils.files.DesktopFiles;
import de.longri.gdx_utils.files.Files;
import de.longri.utils.SystemType;
import de.longri.fx.TRANSLATION;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


public class UTILS {

    public final static DateTimeFormatter SDF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final static DateTimeFormatter SDF_DE = DateTimeFormatter.ofPattern("dd. MM. yyyy");

    public static LocalDate DateFromString(String expireString) {
        DateTimeFormatter dtf = TRANSLATION.getLocale() == Locale.GERMAN ? SDF_DE : SDF;
        return LocalDate.parse(expireString, dtf);
    }

    static public String FormatDate(LocalDateTime date) {
        DateTimeFormatter dtf = TRANSLATION.getLocale() == Locale.GERMAN ? SDF_DE : SDF;
        return dtf.format(date);
    }

    public static String FormatDate(LocalDate date) {
        DateTimeFormatter dtf = TRANSLATION.getLocale() == Locale.GERMAN ? SDF_DE : SDF;
        return dtf.format(date);
    }


    public static Files files;
    //    public static ApplicationLogger appLogger;
    private static AtomicBoolean filesInitial = new AtomicBoolean(false);

    public static void initialFilesWithDesktop() {
        synchronized (filesInitial) {
            if (filesInitial.get()) return;

            files = new DesktopFiles();
            filesInitial.set(true);
        }
    }

    public static String execCmd(String cmd) throws IOException {
        if (cmd == null || cmd.isEmpty()) return "";
        Process child = Runtime.getRuntime().exec(cmd);

        InputStream in = child.getInputStream();
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = in.read()) != -1) {
            sb.append((char) c);
        }
        in.close();
        return sb.toString();
    }

    public static void extractResources(Class clazz, String recourse) throws IOException {
        //gets program.exe from inside the JAR file as an input stream
        InputStream is = clazz.getClassLoader().getResourceAsStream(recourse);
        //sets the output stream to a system folder
        OutputStream os = new FileOutputStream(recourse);

        //2048 here is just my preference
        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }

    public static String getIP_address() throws IOException {

        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("google.com", 80));
        return socket.getLocalAddress().toString().substring(1);
//
//
//
//        for (Enumeration<NetworkInterface> netIF = NetworkInterface.getNetworkInterfaces(); netIF.hasMoreElements(); ) {
//            NetworkInterface e = netIF.nextElement();
//            if (e.isUp()) {
//                for (Enumeration<InetAddress> inetAddressEnumeration = e.getInetAddresses(); inetAddressEnumeration.hasMoreElements(); ) {
//                    InetAddress inetAddress = inetAddressEnumeration.nextElement();
//                    if (inetAddress.isSiteLocalAddress())
//                        return inetAddress.toString().substring(1);
//                }
//            }
//        }
//        return "";
    }

    public static boolean isVpnConnected() {
        try {
            for (Enumeration<NetworkInterface> netIF = NetworkInterface.getNetworkInterfaces(); netIF.hasMoreElements(); ) {
                NetworkInterface e = netIF.nextElement();
                if (e.isUp() && e.getName().contains("ppp")) {
                    for (Enumeration<InetAddress> inetAddressEnumeration = e.getInetAddresses(); inetAddressEnumeration.hasMoreElements(); ) {
                        InetAddress inetAddress = inetAddressEnumeration.nextElement();
                        if (inetAddress.isSiteLocalAddress()) {
                            return inetAddress.toString().startsWith("/10.3.");
                        }
//                            return inetAddress.toString().substring(1);
                    }
                }
            }
        } catch (SocketException e) {
            return false;
        }
        return false;
    }

    public static String getComputerName() {

        String inetHostName = "Unknown Computer";
        try {
            inetHostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        String envName;
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME"))
            envName = env.get("COMPUTERNAME");
        else if (env.containsKey("HOSTNAME"))
            envName = env.get("HOSTNAME");
        else
            envName = inetHostName;

        if (envName.equals(inetHostName)) return envName;

        return "env: " + envName + " / host: " + inetHostName;
    }

    public static void takeScreenShotToClipboard() {

        String windowsCommand = "C:\\Windows\\System32\\SnippingTool.exe /clip";
        String macCommand = "screencapture -c -s";

        String cmd = null;
        switch (SystemType.getSystemType()) {

            case MAC:
                cmd = macCommand;
                break;
            case WIN:
                cmd = windowsCommand;
                break;
            case LINUX:
                break;
            case UNKNOWN:
                break;
        }

        try {
            execCmd(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static BufferedImage getClipBoardImage() throws IOException, UnsupportedFlavorException, RuntimeException {
        Transferable content = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (content == null) {
            throw new RuntimeException("error: nothing found in clipboard");
        }
        if (!content.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            throw new RuntimeException("error: no image found in clipbaord");
        }
        return toBufferedImage((Image) content.getTransferData(DataFlavor.imageFlavor));
    }

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public static int copyImageToFile(BufferedImage img, String filename) throws Exception {
        String ext = ext(filename);
        if (ext == null) {
            ext = "png";
            filename += "." + ext;
        }
        File outfile = new File(filename);
        ImageIO.write(img, ext, outfile);
        System.err.println("image copied to: " + outfile.getAbsolutePath());
        return 0;
    }

    static String ext(String filename) {
        int pos = filename.lastIndexOf('.') + 1;
        if (pos == 0 || pos >= filename.length()) return null;
        return filename.substring(pos);
    }

    public static String encodeImageToBase64String(javafx.scene.image.Image fxImage, String type) {
        return encodeImageToBase64String(SwingFXUtils.fromFXImage(fxImage, null), type);
    }

    public static String encodeImageToBase64String(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            imageString = Base64.getEncoder().encodeToString(imageBytes);

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }

    public static HashMap<String, String> getSystemInfo() {
        return de.longri.utils.UTIL.getSystemInfo();
    }
}
