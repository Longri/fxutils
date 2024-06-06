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
package de.longri;

import org.apache.commons.validator.routines.InetAddressValidator;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.*;

class UTILSTest {

    @Test
    void getIP_address() throws Exception {
        String ipAddress = null;
        try {
            ipAddress = UTILS.getIP_address();
        } catch (UnknownHostException e) {
            //offline no test possible
            return;
        }
        System.out.println("IP = " + ipAddress);
        assertTrue(InetAddressValidator.getInstance().isValidInet4Address(ipAddress));
        UTILS.isVpnConnected();
    }

    @Test
    void getBase64String() throws IOException {
        String resource = "itIcon16.png";
        File file = new File(getClass().getClassLoader().getResource(resource).getFile());

        BufferedImage image = ImageIO.read(file);
        String base64 = UTILS.encodeImageToBase64String(image, "png");

        String expected = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAABKUlEQVR4XuWTP0vDQBjG+4Ecnd11c7I0UT+Bs4Org5PgRxBxFBFcBDdNkxRFrZiqS1Fa/4BWrKmmtubne72mSWwcRDcfeAh533t+l8vd5XJ/JdNlxnApyvPYcChnWvdcw2Y8FZbGnAR9MZGnv3G/7xccJpKAc9VYv4KgCzt3cN0iU09tWDgFyewOAKaDpwD1Vz2o/QHbN+lgUlv1HuAo/gKHigKsVuFRZtisgfcMrU4cCkP9XpNJ5su9ZZwMAao+FB/g9g1mS3q93VADXjrx/+l7GKBmj/QPAWc/BaS2UQp7qqi2SEkdpujUNd917T74ArATBynvMmmWCBY9OGjAymU8cPkCDqW2VEkBmlMWIwOAUsFiTJayoS6U2M6yBC0Zs5bfZzQV/o0+ATM0h0kBs3dLAAAAAElFTkSuQmCC";

        assertEquals(expected, base64);

    }
}