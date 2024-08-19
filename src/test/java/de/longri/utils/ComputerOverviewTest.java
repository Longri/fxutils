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
package de.longri.utils;

import de.longri.UTILS;
import com.sun.javafx.application.PlatformImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;


import static org.junit.jupiter.api.Assertions.*;

public class ComputerOverviewTest {

    static boolean IGNORE_TEST = false;

    @BeforeAll
    static void init() {
        IGNORE_TEST = SystemType.isLinux() || SystemType.isUnknown();
    }


    @Test
    void testInfo() throws IOException {
        if (IGNORE_TEST) return;
        String IP = null;
        try {
            IP = UTILS.getIP_address();
        } catch (UnknownHostException e) {
            //offline no test posible
            return;
        }

        switch (IP) {
            case "10.3.11.50": //mac longri
                checkMacbook(isHomeOffice());
                return;
            case "192.168.2.20":
                checkParallelsVM(isHomeOffice());
                return;
            case "192.168.200.64":// mac on fritzbox
                checkMacbook(isHomeOffice());
                return;
            case "10.10.6.255":
                //Dell longri lan
                checkDell(isHomeOffice());
                return;
        }

        System.out.println("Unknown System! Can't test it! IP is: " + IP);

//        throw new RuntimeException("Unknown System! Can't test it! IP is: " + IP);
    }

    public static boolean isHomeOffice() throws IOException {
        String IP = UTILS.getIP_address();

        switch (IP) {
            case "10.3.11.50":
            case "10.3.12.36":
            case "10.3.11.212":
                return false;
            case "192.168.2.20":
            case "192.168.2.150":
                return true;
        }
        return false;
    }

    private void checkMacbook(boolean atHomeOffice) throws IOException {

        ComputerOverview overview = new ComputerOverview();
        assertEquals(atHomeOffice ? "Andres-MBP-2.fritz.box" : "MacBook-Pro-von-Andre.local", overview.computerName);
        assertEquals("Y406N7GVXJ", overview.serialNumber);
        assertEquals("32 GB", overview.memory);
        assertEquals("Apple M1 Pro", overview.processor);
        assertEquals("macOS 14.6.1 (23G93)", overview.operatingSystem);
        assertEquals("MacBookPro18,1", overview.model);
        assertEquals("Apple", overview.manufacturer);

        String expectedSystemInfo = "" +
                "Hersteller/Typ     : Apple MacBookPro18,1\n" +
                "Prozessor/Speicher : Apple M1 Pro | 32 GB\n" +
                "Betriebssystem     : macOS 14.6.1 (23G93)\n" +
                "Seriennummer       : Y406N7GVXJ\n" +
                "Name               : #NAME#\n" +
                "IP                 : #IP#";

        expectedSystemInfo = expectedSystemInfo.replace("#IP#", UTILS.getIP_address());
        expectedSystemInfo = expectedSystemInfo.replace("#NAME#", atHomeOffice ? "Andres-MBP-2.fritz.box" : "MacBook-Pro-von-Andre.local");

        assertEquals(expectedSystemInfo, overview.toString());

    }

    private void checkDell(boolean atHomeOffice) throws IOException {
        ComputerOverview overview = new ComputerOverview();
        assertEquals("BM-NB162", overview.computerName);
        assertEquals("JB1TQN2", overview.serialNumber);
        assertEquals("8 GB", overview.memory);
        assertEquals("Intel(R) Core(TM) i5-8250U CPU @ 1.60GHz", overview.processor);
        assertEquals("Microsoft Windows 10 Pro", overview.operatingSystem);
        assertEquals("XPS 13 9370", overview.model);
        assertEquals("Dell Inc.", overview.manufacturer);

        String expectedSystemInfo = "" +
                "Hersteller/Typ     : Dell Inc. XPS 13 9370\n" +
                "Prozessor/Speicher : Intel(R) Core(TM) i5-8250U CPU @ 1.60GHz | 8 GB\n" +
                "Betriebssystem     : Microsoft Windows 10 Pro\n" +
                "Seriennummer       : JB1TQN2\n" +
                "Name               : #NAME#\n" +
                "IP                 : #IP#";

        expectedSystemInfo = expectedSystemInfo.replace("#IP#", UTILS.getIP_address());
        expectedSystemInfo = expectedSystemInfo.replace("#NAME#", atHomeOffice ? "BM-NB162" : "BM-NB162");

        assertEquals(expectedSystemInfo, overview.toString());
    }

    private void checkParallelsVM(boolean atHomeOffice) throws IOException {
        ComputerOverview overview = new ComputerOverview();
        assertEquals("IT-CVM01", overview.computerName);
        assertEquals("Parallels-28 83 37 E7 14 0B 4D E9 B7 82 D0 78 41 7C 4E 53", overview.serialNumber);
        assertEquals("4 GB", overview.memory);
        assertEquals("Intel(R) Core(TM) i7-9750H CPU @ 2.60GHz", overview.processor);
        assertEquals("Microsoft Windows 10 Pro", overview.operatingSystem);
        assertEquals("Parallels Virtual Platform", overview.model);
        assertEquals("Parallels Software International Inc.", overview.manufacturer);

        String expectedSystemInfo = "" +
                "Hersteller/Typ     : Parallels Software International Inc. Parallels Virtual Platform\n" +
                "Prozessor/Speicher : Intel(R) Core(TM) i7-9750H CPU @ 2.60GHz | 4 GB\n" +
                "Betriebssystem     : Microsoft Windows 10 Pro\n" +
                "Seriennummer       : Parallels-28 83 37 E7 14 0B 4D E9 B7 82 D0 78 41 7C 4E 53\n" +
                "Name               : IT-CVM01\n" +
                "IP                 : #IP#";
        expectedSystemInfo = expectedSystemInfo.replace("#IP#", UTILS.getIP_address());
        assertEquals(expectedSystemInfo, overview.toString());
    }

}


//    assertEquals("Andres-MacBook-Pro-2.local", name);
//        assertEquals("Andres-MBP-2.fritz.box", name);
//        assertEquals("BM-NB162", name);