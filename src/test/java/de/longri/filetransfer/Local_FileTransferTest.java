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


import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.GeneralSecurityException;

import static de.longri.filetransfer.Ftp_ConnectionTest.LF;
import static org.junit.jupiter.api.Assertions.*;

class Local_FileTransferTest {


    static final String CRLF_LF = SystemUtils.IS_OS_WINDOWS ? "\r\n" : "\n";


    @Test
    void readString() throws IOException, GeneralSecurityException {
        FileTransferHandle cild = new Local_FileTransferHandle(new File("./Test/TestDocker/SMB-Shared/TestFolder"));
        assertTrue(cild.exists());

        FileTransferHandle[] fths = cild.list();
        assertEquals(3, fths.length);

        FileTransferHandle testFile = null;
        for (FileTransferHandle fth2 : fths) {
            assertTrue(fth2.exists());
            if (fth2.name().equals("testDatei.txt")) {
                testFile = fth2;
                break;
            }
        }
        assertNotNull(testFile);

        String readString;
        String expectedString = "content of testDatei" + CRLF_LF +
                "is coded as UTF-8" + CRLF_LF +
                "" + CRLF_LF +
                "und hat somit umlaute" + CRLF_LF +
                "üÄ...ß";
        expectedString = new String(expectedString.getBytes(), StandardCharsets.UTF_8);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read = 0;
        InputStream inputStream = testFile.read();
        while ((read = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, read);
            if (read < buffer.length) break;
        }
        testFile.closeInputStream(inputStream);

        readString = result.toString("UTF-8");
        assertEquals(expectedString, readString);

        readString = testFile.readString();
        assertEquals(expectedString, readString);

        readString = testFile.readString("UTF-8");
        assertEquals(expectedString, readString);

    }

    @Test
    void writeStringTest() throws IOException {

        File file = new File("./Test/tests/testFile.txt");

        if (file.exists()) file.delete();

        String WRITE_STR = "Execution optimizations have been disabled for 1 invalid unit(s) of work during this build to ensure correctness." + CRLF_LF +
                "Please consult deprecation warnings for more details." + CRLF_LF +
                "BUILD SUCCESSFUL in 381ms" + CRLF_LF;

        byte[] bytes = WRITE_STR.getBytes("utf-8");

        Local_FileTransferHandle fth = new Local_FileTransferHandle(file);
        fth.mkdirs();
        fth.write(false).write(bytes);

        String readString = fth.readString("UTF-8");
        assertEquals(WRITE_STR, readString);

    }

    //tests for DirectoryWalker from utils

    @Test
    void pathTest() {
        File file = new File("./Test/tests/testFile.txt");
        Local_FileTransferHandle fth = new Local_FileTransferHandle(file);
        assertEquals(file.getAbsolutePath(), fth.path());
        assertFalse(fth.isDirectory());
        assertTrue(fth.parent().isDirectory());
    }

    @Test
    void getRootTest() {
        File file = (new File("/")).getAbsoluteFile();
        Local_FileTransferHandle fth = new Local_FileTransferHandle("./Test/tests/testFile.txt");
        FileTransferHandle root = fth.getRoot();
        assertEquals(file.getAbsolutePath(), root.path());

        File[] fileDirs = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() && pathname.exists() && (SystemUtils.IS_OS_WINDOWS || !pathname.isHidden());
            }
        });
        FileTransferHandle[] dirs = root.listDirs();
        assertEquals(fileDirs.length, dirs.length);

        for (File f : fileDirs) {
            boolean exist = false;
            for (FileTransferHandle ft : dirs) {
                if (ft.path().equals(f.getPath())) {
                    exist = true;
                    break;
                }
            }
            assertTrue(exist);
        }
    }

    @Test
    void writeAppendTest() throws IOException {
        String WRITE_STR = "Execution optimizations have been disabled for 1 invalid unit(s) of work during this build to ensure correctness." + CRLF_LF +
                "Please consult deprecation warnings for more details." + CRLF_LF +
                "BUILD SUCCESSFUL in 381ms" + CRLF_LF;

        Local_FileTransferHandle fth = new Local_FileTransferHandle("./Test/tests/testAppendFile.txt");
        fth.mkdirs();
        fth.writeString(WRITE_STR, false, "UTF-8");

        String readString = fth.readString("UTF-8");
        assertEquals(WRITE_STR, readString);

        String WRITE_SHORT = "Short write";
        fth.writeString(WRITE_SHORT, false, "UTF-8");
        readString = fth.readString("UTF-8");
        assertEquals(WRITE_SHORT, readString);
    }
}