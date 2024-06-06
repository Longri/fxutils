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
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.*;

class Ftp_ConnectionTest {

    private static final boolean IGNORE_TEST = true;

    /**
     * Individual connection for this test!
     *
     * @return
     */
    private static Connection getTestConnection() {

        String address = "127.0.0.1";
        String port = "21";
        String usr = "ftpuser";
        String pw = "testpass";

        Connection con = new Ftp_Connection(address, port, usr, pw).setRootPath("/var/www/upload/sftp");
        return con;
    }

    /*===============================================================================================*/
    /*================= Tests are equals the SFTP_ConnectionTest   ===================================*/
    /*===============================================================================================*/


    @Test
    void getRoot() throws IOException {
        if (IGNORE_TEST) {return;}
        if (!InetAddress.getByName("127.0.0.1").isReachable(3000))
            throw new RuntimeException("Can't test! FTP server not reachable!");

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

    static final String CRLF_LF = ((String) System.getProperties().get("os.name")).toLowerCase().matches(".*win.*") ?
            "\r\n" : "\n";

    static final String LF = "\n";

    @Test
    void readString() throws IOException, GeneralSecurityException {
        if (IGNORE_TEST) {return;}
        if (!InetAddress.getByName("127.0.0.1").isReachable(3000))
            throw new RuntimeException("Can't test! FTP server not reachable!");

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
        String expectedString = "content of testDatei" + CRLF_LF +
                "is coded as UTF-8" + CRLF_LF +
                "" + CRLF_LF +
                "und hat somit umlaute" + CRLF_LF +
                "üÄ...ß";

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

        con.disconnect();
    }
}