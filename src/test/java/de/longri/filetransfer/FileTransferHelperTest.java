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

import de.longri.serializable.BitStore;
import de.longri.serializable.NotImplementedException;
import de.longri.serializable.StoreBase;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import static de.longri.filetransfer.Ftp_ConnectionTest.CRLF_LF;
import static de.longri.filetransfer.Ftp_ConnectionTest.LF;
import static org.junit.jupiter.api.Assertions.*;

class FileTransferHelperTest {
    static final String CRLF_LF = ((String) System.getProperties().get("os.name")).toLowerCase().matches(".*win.*") ?
            "\r\n" : "\n";

    @Test
    void serializeLocal() throws GeneralSecurityException, IOException, NotImplementedException {
        FileTransferHandle testFile = new Local_FileTransferHandle(new File("./Test/TestDocker/SMB-Shared/TestFolder/testDatei.txt"));
        String expectedString = "content of testDatei" + CRLF_LF +
                "is coded as UTF-8" + CRLF_LF +
                "" + CRLF_LF +
                "und hat somit umlaute" + CRLF_LF +
                "üÄ...ß";
        expectedString = new String(expectedString.getBytes(), StandardCharsets.UTF_8);
        String readString = testFile.readString("UTF-8");
        assertEquals(expectedString, readString);

        //Serialize to String and back

        StoreBase store = new BitStore();
        FileTransferHelper.serialize(store, testFile);
        String base64 = store.getBase64String();

        StoreBase s = new BitStore(base64);
        FileTransferHandle fileTransferHandle = FileTransferHelper.getFileTransferHandle(s);

        assertInstanceOf(Local_FileTransferHandle.class, fileTransferHandle);
        readString = fileTransferHandle.readString("UTF-8");
        assertEquals(expectedString, readString);
    }

    @Test
    void serializeSMB() throws GeneralSecurityException, IOException, NotImplementedException {

        String SHARENAME = "public";
        String SERVER = "127.0.0.1";
        String USER = "smbusr";
        String PASSWORT = "smbpass";
        String DOMAIN = "";

        Credentials credentials = new Credentials(
                SERVER, "", SHARENAME, USER, DOMAIN, PASSWORT
        );

        String expected = "content of testDatei\n" +
                "is coded as UTF-8\n" +
                "\n" +
                "und hat somit umlaute\n" +
                "üÄ...ß";


        SMB_FileTransferHandle testFile = new SMB_FileTransferHandle(credentials, "TestFolder", "testDatei.txt");
        String readString = testFile.readString("utf-8");

        assertEquals(expected, readString);

        //Serialize to String and back

        StoreBase store = new BitStore();
        FileTransferHelper.serialize(store, testFile);
        String base64 = store.getBase64String();

        StoreBase s = new BitStore(base64);
        FileTransferHandle fileTransferHandle = FileTransferHelper.getFileTransferHandle(s);

        assertInstanceOf(SMB_FileTransferHandle.class, fileTransferHandle);
        readString = fileTransferHandle.readString("UTF-8");
        assertEquals(expected, readString);
    }

    @Test
    void serializeSFTP() throws GeneralSecurityException, IOException, NotImplementedException {

        String address = "127.0.0.1";
        String port = "2222";
        String usr = "sftpuser";
        String pw = "testpass";

        Connection con = new SFTP_Connection(address, port, usr, pw).setRootPath("/var/www/upload/sftp");
        con.connect();
        FileTransferHandle fth = con.getRoot();

        FileTransferHandle cild = fth.child("ResourceTestFolder/testDatei.txt");
        assertTrue(cild.exists());


        String readString;
        String expected = "content of testDatei" + LF +
                "is coded as UTF-8" + LF +
                "" + LF +
                "und hat somit umlaute" + LF +
                "üÄ...ß";
        expected = new String(expected.getBytes(), StandardCharsets.UTF_8);

        readString = cild.readString("UTF-8");


        assertEquals(expected, readString);

        //Serialize to String and back

        StoreBase store = new BitStore();
        FileTransferHelper.serialize(store, cild);
        String base64 = store.getBase64String();

        StoreBase s = new BitStore(base64);
        FileTransferHandle fileTransferHandle = FileTransferHelper.getFileTransferHandle(s);

        assertInstanceOf(SFTP_FileTransferHandle.class, fileTransferHandle);
        readString = fileTransferHandle.readString("UTF-8");
        assertEquals(expected, readString);

        con.disconnect();
    }
}