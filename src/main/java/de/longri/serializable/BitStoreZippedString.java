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
package de.longri.serializable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by Longri on 15.11.15.
 */
public class BitStoreZippedString extends BitStore {


    public BitStoreZippedString(byte[] array) {
        super(array);
    }

    public BitStoreZippedString() {
        super();
    }

    public BitStoreZippedString(String base64) {
        super(base64);
    }

    @Override
    protected void _write(String s) throws NotImplementedException {

        byte[] bytes = compress(s.getBytes(UTF8_CHARSET));
        _write(bytes.length);

        for (byte b : bytes) {
            write(b);
        }
    }

    @Override
    public String readString() throws NotImplementedException {

        int length = readInt();

        byte[] bytes = new byte[length];

        for (int i = 0; i < length; i++) {
            bytes[i] = readByte();
        }

        byte[] stringBytes = decompress(bytes);

        return new String(stringBytes, 0, stringBytes.length, StandardCharsets.UTF_8);


    }

    public static byte[] compress(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);

        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] output = outputStream.toByteArray();

        deflater.end();

        return output;
    }

    public static byte[] decompress(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = 0;
            try {
                count = inflater.inflate(buffer);
            } catch (DataFormatException e) {
                e.printStackTrace();
            }
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] output = outputStream.toByteArray();

        inflater.end();
        return output;
    }
}
