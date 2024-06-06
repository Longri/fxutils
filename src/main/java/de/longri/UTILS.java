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

    public static HashMap<String, String> getSystemInfo() {

        HashMap<String, String> map = new HashMap<>();

        if (SystemType.getSystemType() == SystemType.MAC) {

            /**
             *Hardware:
             *
             *     Hardware Overview:
             *
             *       Model Name: MacBook Pro
             *       Model Identifier: MacBookPro16,1
             *       Processor Name: 6-Core Intel Core i7
             *       Processor Speed: 2,6 GHz
             *       Number of Processors: 1
             *       Total Number of Cores: 6
             *       L2 Cache (per Core): 256 KB
             *       L3 Cache: 12 MB
             *       Hyper-Threading Technology: Enabled
             *       Memory: 16 GB
             *       Boot ROM Version: 1037.147.4.0.0 (iBridge: 17.16.16610.0.0,0)
             *       Serial Number (system): C02CX3K3MD6R
             *       Hardware UUID: BF367C4B-1CF8-5101-8E90-7CDA5E046A6D
             *       Activation Lock Status: Enabled
             */
            try {
                String result = UTILS.execCmd("system_profiler SPHardwareDataType");

                String[] lines = result.split("\n");

                boolean ser = false;
                boolean mem = false;
                boolean pro = false;
                boolean mod = false;

                for (String line : lines) {
                    if (!ser && line.contains("Serial Number")) {
                        int pos = line.indexOf(':') + 1;
                        map.put("serialNumber", line.substring(pos).trim());
                        ser = true;
                    } else if (!mem && line.contains("Memory")) {
                        int pos = line.indexOf(':') + 1;
                        map.put("memory", line.substring(pos).trim());
                        mem = true;
                    } else if (!pro && line.contains("Processor Name")) {
                        int pos = line.indexOf(':') + 1;
                        map.put("processor", line.substring(pos).trim());
                        pro = true;
                    } else if (!pro && line.contains("Chip:")) {
                        int pos = line.indexOf(':') + 1;
                        map.put("processor", line.substring(pos).trim());
                        pro = true;
                    } else if (!mod && line.contains("Model Identifier")) {
                        int pos = line.indexOf(':') + 1;
                        map.put("model", line.substring(pos).trim());
                        mod = true;
                    }
                }


                result = UTILS.execCmd("system_profiler SPSoftwareDataType");

                lines = result.split("\n");

                boolean os = false;


                for (String line : lines) {
                    if (!os && line.contains("System Version")) {
                        int pos = line.indexOf(':') + 1;
                        map.put("operatingSystem", line.substring(pos).trim());
                        os = true;
                    }
                }

                map.put("manufacturer", "Apple");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (SystemType.getSystemType() == SystemType.WIN) {

            /** WMIC COMPUTERSYSTEM get Manufacturer,Model,Caption,TotalPhysicalMemory
             * Caption   Manufacturer  Model        TotalPhysicalMemory
             * BM-NB162  Dell Inc.     XPS 13 9370  8388562944
             */

            /**  WMIC OS get Caption
             * Caption
             * Microsoft Windows 10 Pro
             */

            /** WMIC BIOS get SerialNumber
             * SerialNumber
             * JB1TQN2
             */

            /** WMIC CPU get Name
             * Name
             * Intel(R) Core(TM) i5-8250U CPU @ 1.60GHz
             */
            try {
                String result = UTILS.execCmd("WMIC COMPUTERSYSTEM get Manufacturer");
                String[] lines = result.split("\r\n");
                map.put("manufacturer", lines[1].trim());

                result = UTILS.execCmd("WMIC COMPUTERSYSTEM get Model");
                lines = result.split("\r\n");
                map.put("model", lines[1].trim());

                result = UTILS.execCmd("WMIC BIOS get SerialNumber");
                lines = result.split("\r\n");
                map.put("serialNumber", lines[1].trim());

                result = UTILS.execCmd("wmic memorychip get capacity");
                lines = result.split("\r\n");
                long bytes = Long.parseLong(lines[1].trim());
                if (lines.length > 2 && !lines[2].trim().isEmpty()) bytes += Long.parseLong(lines[2].trim());
                if (lines.length > 3 && !lines[3].trim().isEmpty()) bytes += Long.parseLong(lines[3].trim());
                if (lines.length > 4 && !lines[4].trim().isEmpty()) bytes += Long.parseLong(lines[4].trim());
                if (lines.length > 5 && !lines[5].trim().isEmpty()) bytes += Long.parseLong(lines[5].trim());
                map.put("memory", humanReadableByteCount(bytes));

                result = UTILS.execCmd("WMIC CPU get Name");
                lines = result.split("\r\n");
                map.put("processor", lines[1].trim());

                result = UTILS.execCmd("WMIC OS get Caption");
                lines = result.split("\r\n");
                map.put("operatingSystem", lines[1].trim());

                result = UTILS.execCmd("WMIC COMPUTERSYSTEM get Model");
                lines = result.split("\r\n");
                map.put("model", lines[1].trim());


            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return map;
    }

    public static String humanReadableByteCount(final long bytes) {
        long kilobyte = 1024;
        long megabyte = kilobyte * 1024;
        long gigabyte = megabyte * 1024;
        long terabyte = gigabyte * 1024;

        if ((bytes >= 0) && (bytes < kilobyte)) {
            return bytes + " B";

        } else if ((bytes >= kilobyte) && (bytes < megabyte)) {
            return (bytes / kilobyte) + " KB";

        } else if ((bytes >= megabyte) && (bytes < gigabyte)) {
            return (bytes / megabyte) + " MB";

        } else if ((bytes >= gigabyte) && (bytes < terabyte)) {
            return (bytes / gigabyte) + " GB";

        } else if (bytes >= terabyte) {
            return (bytes / terabyte) + " TB";

        } else {
            return bytes + " Bytes";
        }
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


}
