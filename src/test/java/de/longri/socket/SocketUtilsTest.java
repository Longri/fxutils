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
package de.longri.socket;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SocketUtilsTest {

    @Test
    void getSetStringFromByteArray() throws NoSuchAlgorithmException {

        int l = 45;// length of random byte array

        String TEST_STRING = "test String"; // 11 bytes
        byte[] randomBytes = new byte[l];
        SecureRandom.getInstanceStrong().nextBytes(randomBytes);
        byte[] randomBcopy = randomBytes.clone();
        SocketUtils.setStringToByteArray(randomBytes, TEST_STRING, 10);
        String resultString = SocketUtils.getStringFromByteArray(randomBytes, 10);
        assertEquals(TEST_STRING, resultString);
        byte[] bytes = TEST_STRING.getBytes(StandardCharsets.UTF_8);
        int sl = bytes.length;
        assertTrue(Arrays.equals(randomBytes, 10, sl - 1, bytes, 10, sl - 1));

        //chk bytes before not changed
        assertTrue(Arrays.equals(randomBytes, 0, 9, randomBcopy, 0, 9));
        assertEquals(10 + 4, SocketUtils.indexOf(randomBytes, bytes)); // 10 bytes offset + 4 bytes length of String byte array
        //chk bytes after not changed
        assertTrue(Arrays.equals(randomBytes, 10 + 4 + sl, l, randomBcopy, 10 + 4 + sl, l));

        assertEquals(sl, SocketUtils.getIntValueFromByteArray(randomBytes, 10));

    }


    @Test
    void setLongValueToByteArray() throws NoSuchAlgorithmException {
        int l = 40;// length of random byte array

        long TEST_LONG = 287120457123L; // 11 bytes
        byte[] randomBytes = new byte[l];
        SecureRandom.getInstanceStrong().nextBytes(randomBytes);
        byte[] randomBcopy = randomBytes.clone();

        SocketUtils.setLongValueToByteArray(randomBytes, TEST_LONG, 5);
        assertEquals(TEST_LONG, SocketUtils.getLongValueFromByteArray(randomBytes, 5));

        //chk bytes before not changed
        assertTrue(Arrays.equals(randomBytes, 0, 5, randomBcopy, 0, 5));

        //chk bytes after not changed
        assertTrue(Arrays.equals(randomBytes, 13, l, randomBcopy, 13, l));

    }

    @Test
    void getDateFromLong() {
        LocalDateTime now = LocalDateTime.now();
        now = now.truncatedTo(ChronoUnit.SECONDS);
        long l = SocketUtils.getLongFromDateTime(now);

        LocalDateTime localDateTime = SocketUtils.getDateFromLong(l);

        assertEquals(now, localDateTime);
    }


    @Test
    void setIntValueToByteArray() throws NoSuchAlgorithmException {
        int l = 20;// length of random byte array

        int TEST_INT = 287120; // 11 bytes
        byte[] randomBytes = new byte[l];
        SecureRandom.getInstanceStrong().nextBytes(randomBytes);
        byte[] randomBcopy = randomBytes.clone();

        SocketUtils.setIntValueToByteArray(randomBytes, TEST_INT, 5);
        assertEquals(TEST_INT, SocketUtils.getIntValueFromByteArray(randomBytes, 5));

        //chk bytes before not changed
        assertTrue(Arrays.equals(randomBytes, 0, 5, randomBcopy, 0, 5));

        //chk bytes after not changed
        assertTrue(Arrays.equals(randomBytes, 9, l, randomBcopy, 9, l));

    }

}