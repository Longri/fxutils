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

import java.io.DataInputStream;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SocketUtils {


    public static final  int BUFFER_MAX = 512000;


    public static byte[] readAllFromDataInputStream(DataInputStream dis) throws IOException {
        byte[] data = new byte[BUFFER_MAX];
        int idx = 0;
        while (dis.available() > 0) {
            data[idx++] = dis.readByte();
        }
        int count = idx;
        byte[] read = new byte[count];
        for (int i = 0; i <= count - 1; i++)
            read[i] = data[i];
        return read;
    }


    static public int indexOf(byte[] outerArray, byte[] smallerArray) {
        for (int i = 0; i < outerArray.length - smallerArray.length + 1; ++i) {
            boolean found = true;
            for (int j = 0; j < smallerArray.length; ++j) {
                if (outerArray[i + j] != smallerArray[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }


    public static void setStringToByteArray(byte[] array, String str, int offset) {
        byte[] strBytes = str.getBytes(UTF_8);
        int length = strBytes.length;
        setIntValueToByteArray(array, length, offset);
        System.arraycopy(strBytes, 0, array, offset + 4, strBytes.length);
    }


    public static String getStringFromByteArray(byte[] array, int offset) {
        int length = getIntValueFromByteArray(array, offset);
        return new String(array, offset + 4, length, UTF_8);
    }

    public static void setLongValueToByteArray(byte[] array, long value, int offset) {
        int index = offset;
        array[index++] = (byte) (value >> 56);
        array[index++] = (byte) (value >> 48);
        array[index++] = (byte) (value >> 40);
        array[index++] = (byte) (value >> 32);
        array[index++] = (byte) (value >> 24);
        array[index++] = (byte) (value >> 16);
        array[index++] = (byte) (value >> 8);
        array[index] = (byte) (value >> 0);
    }

    public static long getLongValueFromByteArray(byte[] array, int offset) {
        int readIndex = offset;
        return (array[readIndex++] & 0xffL) << 56 | (array[readIndex++] & 0xffL) << 48 | (array[readIndex++] & 0xffL) << 40
                | (array[readIndex++] & 0xffL) << 32 | (array[readIndex++] & 0xffL) << 24
                | (array[readIndex++] & 0xffL) << 16 | (array[readIndex++] & 0xffL) << 8 | (array[readIndex] & 0xffL);
    }

    public static LocalDateTime getDateFromLong(long timestamp) {
        try {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);
        } catch (DateTimeException tdException) {
            throw new RuntimeException("can't create DateTime from Long value");
        }
    }

    public static long getLongFromDateTime(LocalDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.SECONDS).atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    public static void setIntValueToByteArray(byte[] array, int value, int offset) {
        int index = offset;
        array[index++] = (byte) (value >> 24);
        array[index++] = (byte) (value >> 16);
        array[index++] = (byte) (value >> 8);
        array[index] = (byte) (value >> 0);
    }

    public static int getIntValueFromByteArray(byte[] array, int offset) {
        int index = offset;
        return array[index++] << 24 | (array[index++] & 0xff) << 16 | (array[index++] & 0xff) << 8
                | (array[index] & 0xff);
    }


}
