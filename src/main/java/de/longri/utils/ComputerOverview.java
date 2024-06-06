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

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;

public final class ComputerOverview {
    public final String manufacturer;
    public final String model;
    public final String operatingSystem;
    public final String processor;
    public final String memory;
    public final String serialNumber;
    public final String computerName;
    public final String ip;

    public ComputerOverview() {
        String sIP;
        try {
            sIP = UTILS.getIP_address();
        } catch (IOException e) {
            sIP = "?.?.?.?";
        }
        ip = sIP;
        computerName = UTILS.getComputerName();

        HashMap<String, String> info = UTILS.getSystemInfo();

        serialNumber = info.get("serialNumber");
        memory = info.get("memory");
        processor = info.get("processor");
        operatingSystem = info.get("operatingSystem");
        model = info.get("model");
        manufacturer = info.get("manufacturer");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
//        "Hersteller/Typ     : Apple MacBookPro16,1\n"
//        "Prozessor/Speicher : 6-Core Intel Core i7 | 16 GB\n"
//        "Betriebssystem     : macOS 10.15.7 (19H15)\n"
//        "Seriennummer       : C02CX3K3MD6R\n"
//        "Name               : Andres-MacBook-Pro-2.local\n"
//        "IP                 : 10.3.12.115";

        sb.append("Hersteller/Typ     : ").append(manufacturer).append(" ").append(model).append("\n");
        sb.append("Prozessor/Speicher : ").append(processor).append(" | ").append(memory).append("\n");
        sb.append("Betriebssystem     : ").append(operatingSystem).append("\n");
        sb.append("Seriennummer       : ").append(serialNumber).append("\n");
        sb.append("Name               : ").append(computerName).append("\n");
        sb.append("IP                 : ").append(ip);

        return sb.toString();
    }
}
