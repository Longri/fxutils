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
package de.longri.filetransfer;

import de.longri.rolling_file.RollingFileHandle;
import de.longri.serializable.NotImplementedException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class Local_FileTransferHandleTest {

    static final String CRLF_LF = SystemUtils.IS_OS_WINDOWS ? "\r\n" : "\n";

    @Test
    void zipTo() throws IOException {

        String expected = "Execution optimizations have been disabled for 1 invalid unit(s) of work during this build to ensure correctness." + CRLF_LF +
                "Please consult deprecation warnings for more details." + CRLF_LF +
                "BUILD SUCCESSFUL in 381ms" + CRLF_LF;


        Local_FileTransferHandle lfth = new Local_FileTransferHandle("./Test/zipTest/testFile.txt");
        assertTrue(lfth.exists());
        assertEquals(expected, lfth.readString("UTF-8"));

        Local_FileTransferHandle zfth = new Local_FileTransferHandle("./Test/zipTest/testFile.zip");
        zfth.delete();
        assertFalse(zfth.exists());
        lfth.zipTo(zfth);
        assertTrue(zfth.exists());

        //extract and check

        Local_FileTransferHandle extractFolder = new Local_FileTransferHandle("./Test/zipTest/extract/");
        Local_FileTransferHandle extractFile = new Local_FileTransferHandle("./Test/zipTest/extract/testFile.txt");
        extractFolder.deleteDirectory();
        assertFalse(extractFile.exists());
        unzip(zfth, extractFolder);
        assertTrue(extractFile.exists());
        assertEquals(expected, extractFile.readString());
    }

    public static void unzip(Local_FileTransferHandle zipFilePath, Local_FileTransferHandle destDirFile) {
        String destDir = destDirFile.path();
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if (!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath.FILE);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to " + newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void binaryEqualsTest() throws IOException, NotImplementedException {

        int byteCount = 1000;

        File f1 = new File("./Test/f1.bin");
        File f2 = new File("./Test/f2.bin");

        Local_FileTransferHandle lf1 = new Local_FileTransferHandle(f1);
        Local_FileTransferHandle lf2 = new Local_FileTransferHandle(f2);

        lf1.delete();
        lf2.delete();

        assertFalse(lf1.exists());
        assertFalse(lf2.exists());

        byte[] ba = new byte[byteCount];
        Random rd = new Random();
        rd.nextBytes(ba);

        FileUtils.writeByteArrayToFile(f1, ba);
        FileUtils.writeByteArrayToFile(f2, ba);

        assertTrue(lf1.binaryEquals(lf2));
        assertTrue(lf2.binaryEquals(lf1));

        RandomAccessFile ra1 = new RandomAccessFile(f2, "rw");
        ra1.seek(byteCount / 2);
        ra1.writeByte(ra1.readByte() + 12);

        assertFalse(lf1.binaryEquals(lf2));
        assertFalse(lf2.binaryEquals(lf1));
    }

    @Test
    void binaryCopyEqualsTest() throws IOException, NotImplementedException {

        int byteCount = 1000;

        File f1 = new File("./Test/fc1.bin");
        File f2 = new File("./Test/fc2.bin");

        Local_FileTransferHandle lf1 = new Local_FileTransferHandle(f1);
        Local_FileTransferHandle lf2 = new Local_FileTransferHandle(f2);

        lf1.delete();
        lf2.delete();

        assertFalse(lf1.exists());
        assertFalse(lf2.exists());

        byte[] ba = new byte[byteCount];
        Random rd = new Random();
        rd.nextBytes(ba);

        FileUtils.writeByteArrayToFile(f1, ba);
        lf1.copyTo(lf2);

        assertTrue(lf1.binaryEquals(lf2));
        assertTrue(lf2.binaryEquals(lf1));

        RandomAccessFile ra1 = new RandomAccessFile(f2, "rw");
        ra1.seek(byteCount / 2);
        ra1.writeByte(ra1.readByte() + 12);

        assertFalse(lf1.binaryEquals(lf2));
        assertFalse(lf2.binaryEquals(lf1));
    }


    @Test
    void binaryGigaEqualsTest() throws IOException, NotImplementedException {

        long byteCount = (long) (1024L * 1024L * 1024L * 2.7);

        File f1 = new File("./Test/fg1.bin");
        File f2 = new File("./Test/fg2.bin");

        RollingFileHandle lf1 = new RollingFileHandle(new Local_FileTransferHandle(new File("./Test")), "fg1.bin");
        Local_FileTransferHandle lf2 = new Local_FileTransferHandle(f2);

        lf1.delete();
        lf2.delete();

        assertFalse(lf1.exists());
        assertFalse(lf2.exists());

        int GIGA = 1024 * 1024 * 37;
        byte[] ba = new byte[GIGA];
        Random rd = new Random();
        rd.nextBytes(ba);
        RandomAccessFile ra1 = new RandomAccessFile(f1, "rw");
        RandomAccessFile ra2 = new RandomAccessFile(f2, "rw");

        for (long i = 0; i < GIGA * 50; i = i + GIGA) {
            ra1.seek(i);
            ra1.write(ba);
            ra2.seek(i);
            ra2.write(ba);
        }

        assertTrue(lf1.binaryEquals(lf2));
        assertTrue(lf2.binaryEquals(lf1));


        RandomAccessFile ra3 = new RandomAccessFile(f2, "rw");
        ra1.seek(ra3.length() - 375);
        ra1.writeByte(ra3.readByte() + 12);

        assertFalse(lf1.binaryEquals(lf2));
        assertFalse(lf2.binaryEquals(lf1));
    }


}