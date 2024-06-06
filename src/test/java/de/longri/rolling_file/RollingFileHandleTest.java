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
package de.longri.rolling_file;

import de.longri.filetransfer.FileTransferHandle;
import de.longri.filetransfer.Local_FileTransferHandle;
import de.longri.filetransfer.Local_FileTransferHandleTest;
import de.longri.serializable.NotImplementedException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RollingFileHandleTest {

    private static final String TEST_BASE_PATH = "./Test/rollingTest/";


    @Test
    void constructorTest() {

        boolean exceptionThrown = false;
        RollingFileHandle rfh = null;

        try {
            rfh = new RollingFileHandle(null, "");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("baseDir can't be NULL"))
                exceptionThrown = true;
        }
        assertTrue(exceptionThrown, "Exception must thrown");
        assertNull(rfh, "object must be NULL");
        exceptionThrown = false;

        try {
            rfh = new RollingFileHandle(new Local_FileTransferHandle(TEST_BASE_PATH), null);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("filename can't be NULL"))
                exceptionThrown = true;
        }
        assertTrue(exceptionThrown, "Exception must thrown");
        assertNull(rfh, "object must be NULL");
        exceptionThrown = false;

        try {
            rfh = new RollingFileHandle(new Local_FileTransferHandle(TEST_BASE_PATH), "");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("filename can't be empty"))
                exceptionThrown = true;
        }
        assertTrue(exceptionThrown, "Exception must thrown");
        assertNull(rfh, "object must be NULL");
        exceptionThrown = false;

        try {
            rfh = new RollingFileHandle(new Local_FileTransferHandle(TEST_BASE_PATH + "testFolder/testDatei.txt"), "testDatei.txt");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Base dir can't be a file"))
                exceptionThrown = true;
        }
        assertTrue(exceptionThrown, "Exception must thrown");
        assertNull(rfh, "object must be NULL");
        exceptionThrown = false;

        try {
            rfh = new RollingFileHandle(new Local_FileTransferHandle(TEST_BASE_PATH + "testFolder"), "testDatei.txt");
        } catch (RuntimeException e) {
            if (e.getMessage().contains("filename can't be empty"))
                exceptionThrown = true;
        }
        assertFalse(exceptionThrown, "Exception must not be thrown");
        assertNotNull(rfh, "object must not be NULL");

    }

    @Test
    void rolling() throws IOException {

        String expected = "\n" +
                "> Task :compileJava UP-TO-DATE\n" +
                "> Task :copyChangeTxt UP-TO-DATE\n" +
                "> Task :createVersionTxt\n" +
                "> Task :processResources\n" +
                "Execution optimizations have been disabled for task ':processResources' to ensure correctness due to the following reasons:\n" +
                "  - Gradle detected a problem with the following location: '/Users/Longri/@Work/Longri-Serializable/src/main/resources'. Reason: Task ':processResources' uses this output of task ':copyChangeTxt' without declaring an explicit or implicit dependency. This can lead to incorrect results being produced, depending on what order the tasks are executed. Please refer to https://docs.gradle.org/7.6-rc-1/userguide/validation_problems.html#implicit_dependency for more details about this problem.\n" +
                "> Task :classes\n" +
                "> Task :compileTestJava UP-TO-DATE\n" +
                "> Task :processTestResources UP-TO-DATE\n" +
                "> Task :testClasses UP-TO-DATE\n";


        String dateformatStr = "yyyy-MM-dd";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateformatStr);
        String rollingFileNamePattern = "rollingTestFile%d{" + dateformatStr + "}.txt";

        FileTransferHandle baseDir = new Local_FileTransferHandle(TEST_BASE_PATH + "rollingTest");
        baseDir.deleteDirectory();
        baseDir.delete();
        assertFalse(baseDir.exists());


        Local_FileTransferHandle oldFile1 = createRollingTestFile(rollingFileNamePattern, baseDir, -2);
        Local_FileTransferHandle oldFile2 = createRollingTestFile(rollingFileNamePattern, baseDir, -3);
        Local_FileTransferHandle oldFile3 = createRollingTestFile(rollingFileNamePattern, baseDir, -4);


        RollingFileHandle rfh = new RollingFileHandle(baseDir, "rollingTestFile.txt");
        assertNotNull(rfh, "object must not be NULL");
        assertTrue(baseDir.exists());

        assertFalse(rfh.rolling(dateformatStr, 1, 3)); // no file for rolling


        File file = new File(TEST_BASE_PATH + "rollingTest/rollingTestFile.txt");
        assertTrue(file.createNewFile());
        Local_FileTransferHandle testFile = new Local_FileTransferHandle(file);
        testFile.writeString(expected, false);

        addDaysCreationTime(file, -1);

        String nowStr = dtf.format(LocalDate.now().minusDays(1));
        String fileNowName = "rollingTestFile" + nowStr + ".txt";

        assertTrue(rfh.rolling(rollingFileNamePattern, 1, 3));
        assertFalse(file.exists(), "File must moved and no more exist");

        File movedFile = new File(TEST_BASE_PATH + "rollingTest/" + fileNowName);
        assertTrue(movedFile.exists());


        try {
            Thread.sleep(500); //wait for deleting
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertEquals(3, baseDir.list().length);
        assertFalse(oldFile3.exists(), "old file 3 is rolling over and must not exist");
        assertTrue(oldFile2.exists());
        assertTrue(oldFile1.exists());
    }

    @Test
    void rollingZip() throws IOException {

        String expected = "\n" +
                "> Task :compileJava UP-TO-DATE\n" +
                "> Task :copyChangeTxt UP-TO-DATE\n" +
                "> Task :createVersionTxt\n" +
                "> Task :processResources\n" +
                "Execution optimizations have been disabled for task ':processResources' to ensure correctness due to the following reasons:\n" +
                "  - Gradle detected a problem with the following location: '/Users/Longri/@Work/Longri-Serializable/src/main/resources'. Reason: Task ':processResources' uses this output of task ':copyChangeTxt' without declaring an explicit or implicit dependency. This can lead to incorrect results being produced, depending on what order the tasks are executed. Please refer to https://docs.gradle.org/7.6-rc-1/userguide/validation_problems.html#implicit_dependency for more details about this problem.\n" +
                "> Task :classes\n" +
                "> Task :compileTestJava UP-TO-DATE\n" +
                "> Task :processTestResources UP-TO-DATE\n" +
                "> Task :testClasses UP-TO-DATE\n";


        String dateformatStr = "yyyy-MM-dd";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateformatStr);
        String rollingFileNamePattern = "rollingTestFile%d{" + dateformatStr + "}.txt.zip";

        FileTransferHandle baseDir = new Local_FileTransferHandle(TEST_BASE_PATH + "rollingZipTest");
        baseDir.deleteDirectory();
        baseDir.delete();
        assertFalse(baseDir.exists());


        Local_FileTransferHandle oldFile1 = createRollingTestFile(rollingFileNamePattern, baseDir, -2);
        Local_FileTransferHandle oldFile2 = createRollingTestFile(rollingFileNamePattern, baseDir, -3);
        Local_FileTransferHandle oldFile3 = createRollingTestFile(rollingFileNamePattern, baseDir, -4);


        RollingFileHandle rfh = new RollingFileHandle(baseDir, "rollingTestFile.txt");
        assertNotNull(rfh, "object must not be NULL");
        assertTrue(baseDir.exists());

        assertFalse(rfh.rolling(dateformatStr, 1, 3)); // no file for rolling


        File file = new File(TEST_BASE_PATH + "rollingZipTest/rollingTestFile.txt");
        assertTrue(file.createNewFile());
        Local_FileTransferHandle testFile = new Local_FileTransferHandle(file);
        testFile.writeString(expected, false);

        addDaysCreationTime(file, -1);

        String nowStr = dtf.format(LocalDate.now().minusDays(1));
        String fileNowName = "rollingTestFile" + nowStr + ".txt.zip";

        assertTrue(rfh.rolling(rollingFileNamePattern, 1, 3));
        assertFalse(file.exists(), "File must moved and no more exist");

        File movedFile = new File(TEST_BASE_PATH + "rollingZipTest/" + fileNowName);
        assertTrue(movedFile.exists());


        try {
            Thread.sleep(500); //wait for deleting
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        assertEquals(3, baseDir.list().length);
        assertFalse(oldFile3.exists(), "old file 3 is rolling over and must not exist");
        assertTrue(oldFile2.exists());
        assertTrue(oldFile1.exists());


        //extract and check

        Local_FileTransferHandle extractFolder = new Local_FileTransferHandle(TEST_BASE_PATH + "rollingZipTest/extract/");
        Local_FileTransferHandle extractFile = new Local_FileTransferHandle(TEST_BASE_PATH + "rollingZipTest/extract/rollingTestFile.txt");
        extractFolder.deleteDirectory();
        assertFalse(extractFile.exists());
        Local_FileTransferHandleTest.unzip(new Local_FileTransferHandle(movedFile), extractFolder);
        assertTrue(extractFile.exists());
        assertEquals(expected, extractFile.readString());
    }


    private Local_FileTransferHandle createRollingTestFile(String pattern, FileTransferHandle baseDir, int days) {
        int posStart = pattern.indexOf("%d{");
        int posEnd = pattern.indexOf("}", posStart) + 1;
        if (posStart >= 0 && posEnd > posStart) {
            String dateFormatSubStr = pattern.substring(posStart + 3, posEnd - 1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormatSubStr);
            LocalDateTime localDateTime = LocalDateTime.now().plusDays(days);
            String dateStr = formatter.format(localDateTime);
            pattern = pattern.replace("%d{" + dateFormatSubStr + "}", dateStr);
            Local_FileTransferHandle fileTransferHandle = (Local_FileTransferHandle) baseDir.child(pattern);
            fileTransferHandle.createNewFile();
            try {
                addDaysCreationTime(fileTransferHandle.getFile(), days);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return fileTransferHandle;
        }
        return null;
    }

    private void addDaysCreationTime(File file, int days) throws IOException {
        //set creation time for run Test => https://stackoverflow.com/questions/9198184/setting-file-creation-timestamp-in-java
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, days);

        BasicFileAttributeView attributes = Files.getFileAttributeView(file.toPath(), BasicFileAttributeView.class);
        FileTime fileTime = FileTime.fromMillis(c.getTimeInMillis());

        attributes.setTimes(fileTime, fileTime, fileTime);
    }

    @Test
    void getFileNameFromPattern() {
        String NOW = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now().minusDays(1));
        assertEquals("test" + NOW + ".txt", RollingFileHandle.getFileNameFromPattern("test%d{yyyy-MM-dd}.txt"));
    }
}