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


import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.*;

class SMB_FileTransferHandleTest {


    // test on Docker Server

    static String SHARENAME = "public";
    static String SERVER = "127.0.0.1";
    static String USER = "smbusr";
    static String PASSWORT = "smbpass";
    static String DOMAIN = "";

    @Test
    void read() throws GeneralSecurityException, IOException {

        //create credential
        Credentials credentials = new Credentials(
                SERVER, "",SHARENAME, USER, DOMAIN, PASSWORT
        );

        String expected = "content of testDatei\n" +
                "is coded as UTF-8\n" +
                "\n" +
                "und hat somit umlaute\n" +
                "üÄ...ß";


        SMB_FileTransferHandle handle = new SMB_FileTransferHandle(credentials, "TestFolder", "testDatei.txt");
        String redetString = handle.readString("utf-8");

        assertEquals(expected, redetString);
    }

    @Test
    void writeListDelete() throws GeneralSecurityException, IOException {

        String READ_TEST_1_EXPECTED = "content of testDatei\n" +
                "is coded as UTF-8\n" +
                "\n" +
                "und hat somit umlaute\n" +
                "üÄ...ß";

        String WRITE = "It sounds like the HTPC device is running in a different IP subnet to the NAS and there are no routing rules (or gateway) \n" +
                "to handle routing data between the two subnets; hence the \"no route to host\" message. If you added a WIFI router to an \n" +
                "existing network (a hunch) it will be easier to configure it as a wireless bridge so it simply provides a wireless extension to \n" +
                "the existing network instead of creating a second (routed) network for wireless devices. \n";

        String WRITE2 = "If you use it as a router (which will also result in NAT being used) you will need to configure \n" +
                "routing rules (either manually on each host or pushed via DHCP) so devices in each subnet know how to route data to each other.";

        //create credential
        Credentials credentials = new Credentials(
                SERVER, "",SHARENAME, USER, DOMAIN, PASSWORT
        );

        SMB_FileTransferHandle handle = new SMB_FileTransferHandle(credentials, "TestFolder", "writeTest.txt");

        handle.writeString(WRITE, false, "utf-8");
        String redetString = handle.readString("utf-8");
        assertEquals(WRITE, redetString);

        handle.writeString(WRITE2, true, "utf-8");
        redetString = handle.readString("utf-8");
        assertEquals(WRITE + WRITE2, redetString);

        handle.writeString(WRITE2, false, "utf-8");
        redetString = handle.readString("utf-8");
        assertEquals(WRITE2, redetString);

        SMB_FileTransferHandle folder = new SMB_FileTransferHandle(credentials, "TestFolder");

        FileTransferHandle[] FHs = folder.list(".txt");
        assertEquals(2, FHs.length);

        for (FileTransferHandle fh : FHs) {
            if (fh.name().equals("testDatei.txt")) {
                redetString = fh.readString("utf-8");
                assertEquals(READ_TEST_1_EXPECTED, redetString);
            } else if (fh.name().equals("writeTest.txt")) {
                redetString = fh.readString("utf-8");
                assertEquals(WRITE2, redetString);
            } else {
                fail("wrong file name:" + fh.name());
            }
        }


    }


    @Test
    void DirAndParentTests() throws GeneralSecurityException, IOException {


        if(true)return;

        //create credential
        Credentials credentials = new Credentials(
                SERVER, "",SHARENAME, USER, DOMAIN, PASSWORT
        );

        SMB_FileTransferHandle handle = new SMB_FileTransferHandle(credentials, "TestFolder/Folder2/Folder3");

        if (handle.exists()) {
            assertTrue(handle.isDirectory());
            assertTrue((new SMB_FileTransferHandle(credentials, "TestFolder/Folder2")).delete());
        }

        assertFalse(handle.exists());
        boolean thrown = false;
        FileTransferHandle testFile = handle.child("testFile.txt");

        try {
            testFile.writeString("WRITE Test", false, "utf-8");
        } catch (RuntimeException e) {
            thrown = true;
        }
        assertTrue(thrown, "Can't create file on not existing folder");

        testFile.mkdirs();
        testFile.writeString("WRITE Test", false, "utf-8");
        assertEquals("WRITE Test", testFile.readString());

        thrown = false;
        try {
            testFile.child("mustThrownException.txt");
        } catch (RuntimeException e) {
            thrown = true;
        }
        assertTrue(thrown, "Can't create child from file");

        FileTransferHandle folder3 = testFile.parent();
        assertTrue(folder3.exists() && folder3.isDirectory() && folder3.path().equals("TestFolder/Folder2/Folder3"));

        FileTransferHandle folder2 = folder3.parent();
        assertTrue(folder2.exists() && folder2.isDirectory() && folder2.path().equals("TestFolder/Folder2"));

    }

    @Test
    void writeByteArrayTest() throws GeneralSecurityException, IOException {
        //create credential
        Credentials credentials = new Credentials(
                SERVER, "",SHARENAME, USER, DOMAIN, PASSWORT
        );

        SMB_FileTransferHandle handle = new SMB_FileTransferHandle(credentials, "TestFolder/Folder2/Folder4");

        if (handle.exists()) {
            assertTrue(handle.isDirectory());
            assertTrue((new SMB_FileTransferHandle(credentials, "TestFolder/Folder2/Folder4")).delete());
        }

        assertFalse(handle.exists());
        FileTransferHandle testFile = handle.child("testFile4.txt");
        testFile.mkdirs();

        byte[] bytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};

        OutputStream outStream = testFile.write(false);
        outStream.write(bytes);
        outStream.flush();
        outStream.close();

        byte[] readBytes = IOUtils.toByteArray(testFile.read());

        assertArrayEquals(bytes, readBytes);

    }


    //tests for DirectoryWalker from utils

    @Test
    void pathTest() throws GeneralSecurityException, IOException {

        //create credential
        Credentials credentials = new Credentials(
                SERVER, "",SHARENAME, USER, DOMAIN, PASSWORT
        );
        SMB_FileTransferHandle smb = new SMB_FileTransferHandle(credentials, "TestFolder", "readtest1.txt");

        assertEquals("TestFolder/readtest1.txt", smb.path());
        assertFalse(smb.isDirectory());
        assertTrue(smb.parent().isDirectory());
    }

    @Test
    void getRootTest() throws GeneralSecurityException, IOException {

        //create credential
        Credentials credentials = new Credentials(
                SERVER, "",SHARENAME, USER, DOMAIN, PASSWORT
        );
        SMB_FileTransferHandle smb = new SMB_FileTransferHandle(credentials, "TestFolder", "readtest1.txt");


        FileTransferHandle root = smb.getRoot();
        assertEquals("/", root.path());


        FileTransferHandle[] dirs = root.listDirs();
        assertEquals(2, dirs.length);

        String[] fileDirs = new String[]{"/TestFolder", "/StoreValues"};

        for (String f : fileDirs) {
            boolean exist = false;
            for (FileTransferHandle ft : dirs) {
                if (ft.path().equals(f)) {
                    exist = true;
                    break;
                }
            }
            assertTrue(exist);
        }
    }

}