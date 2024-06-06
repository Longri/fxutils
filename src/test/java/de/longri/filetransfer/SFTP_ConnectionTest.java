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

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import static de.longri.filetransfer.Ftp_ConnectionTest.CRLF_LF;
import static de.longri.filetransfer.Ftp_ConnectionTest.LF;
import static org.junit.jupiter.api.Assertions.*;

class SFTP_ConnectionTest {

    /**
     * Individual connection for this test!
     *
     * @return
     */
    private static Connection getTestConnection() {

        String address = "127.0.0.1";
        String port = "2222";
        String usr = "sftpuser";
        String pw = "testpass";

        Connection con = new SFTP_Connection(address, port, usr, pw).setRootPath("/var/www/upload/sftp");
        return con;
    }

    /*===============================================================================================*/
    /*================= Tests are equals the Ftp_ConnectionTest   ===================================*/
    /*===============================================================================================*/


    @Test
    void getRoot() throws IOException {

        if (!InetAddress.getByName("127.0.0.1").isReachable(3000))
            throw new RuntimeException("Can't test! SFTP server not reachable!");


        Connection con = getTestConnection();
        con.connect();
        FileTransferHandle fth = con.getRoot();

        FileTransferHandle cild = fth.child("ResourceTestFolder");
        assertTrue(cild.exists());

        cild = fth.child("no path");
        assertFalse(cild.exists());

        assertNotNull(fth);
        FileTransferHandle[] fths = fth.list();
        assertEquals(2, fths.length);

        for (FileTransferHandle fth2 : fths) {
            assertTrue(fth2.exists());
            if (fth2.isDirectory()) {
                assertEquals("ResourceTestFolder", fth2.name());
                assertEquals("ResourceTestFolder", fth2.nameWithoutExtension());
                assertEquals("", fth2.extension());
            } else {
                assertEquals("Log_dp_2021-09-21.csv", fth2.name());
                assertEquals("Log_dp_2021-09-21", fth2.nameWithoutExtension());
                assertEquals("csv", fth2.extension());
            }
        }
        con.disconnect();
    }

    @Test
    void readString() throws IOException, GeneralSecurityException {

        if (!InetAddress.getByName("127.0.0.1").isReachable(3000))
            throw new RuntimeException("Can't test! SFTP server not reachable!");


        Connection con = getTestConnection();
        con.connect();
        FileTransferHandle fth = con.getRoot();

        FileTransferHandle cild = fth.child("ResourceTestFolder");
        assertTrue(cild.exists());


        FileTransferHandle[] fths = cild.list();
        assertEquals(2, fths.length);

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
        String expectedString = "content of testDatei" + LF +
                "is coded as UTF-8" + LF +
                "" +LF +
                "und hat somit umlaute" + LF +
                "üÄ...ß";
        expectedString = new String(expectedString.getBytes(), StandardCharsets.UTF_8);


        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read = 0;
        InputStream inputStream = testFile.read();
        while ((read = testFile.read().read(buffer)) != -1) {
            result.write(buffer, 0, read);
            if (read < buffer.length) break;
        }
        inputStream.close();
        readString = result.toString("UTF-8");
        assertEquals(expectedString, readString);

        readString = testFile.readString();
        assertEquals(expectedString, readString);

        readString = testFile.readString("UTF-8");
        assertEquals(expectedString, readString);

        con.disconnect();
    }
}