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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.*;

class Resource_FileTransferTest {


    static final String CRLF_LF = SystemUtils.IS_OS_WINDOWS ? "\r\n" : "\n";


    @Test
    void readString() throws IOException, GeneralSecurityException, URISyntaxException {


        FileTransferHandle cild = new ResourceTransferHandler("ResourceTestFolder");
        assertTrue(cild.exists());


        FileTransferHandle[] fths = cild.list();
        assertEquals(1, fths.length);

        FileTransferHandle testFile = null;
        for (FileTransferHandle fth2 : fths) {
            assertTrue(fth2.exists());
            if (fth2.name().equals("text.txt")) {
                testFile = fth2;
                break;
            }
        }
        assertNotNull(testFile);

        String readString;
        String expectedString = "Kaiserdamm" + CRLF_LF +
                "Spandauer Damm" + CRLF_LF +
                "Dreieck Charlottenburg" + CRLF_LF +
                "Dreieck Funkturm" + CRLF_LF +
                "Saatwinkler Damm";

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read = 0;
        InputStream inputStream = testFile.read();
        while ((read = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, read);
            if (read < buffer.length) break;
        }
        testFile.closeInputStream(inputStream);

        readString = result.toString();
        assertEquals(expectedString, readString);

        readString = testFile.readString();
        assertEquals(expectedString, readString);

        readString = testFile.readString("UTF-8");
        assertEquals(expectedString, readString);

    }

    @Test
    void writeStringTest() throws IOException {

        boolean exceptionThrown = false;

        File file = new File("./tests/testFile.txt");

        if (file.exists()) file.delete();

        String WRITE_STR = "Execution optimizations have been disabled for 1 invalid unit(s) of work during this build to ensure correctness.\n" +
                "Please consult deprecation warnings for more details.\n" +
                "BUILD SUCCESSFUL in 381ms\n";

        byte[] bytes = WRITE_STR.getBytes("utf-8");

        Local_FileTransferHandle fth = new ResourceTransferHandler(file);
        try {
            fth.mkdirs();
        } catch (RuntimeException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);

        exceptionThrown = false;
        try {
            fth.write(false).write(bytes);
        } catch (RuntimeException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
}