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
package de.longri.bench;

import de.longri.serializable.Analyse;
import de.longri.serializable.SerializableArrayList;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Random;


/**
 * Created by Longri on 15.11.15.
 */
public class benchTestWeather {
    WeatherInfo wi1 = new WeatherInfo();
    WeatherInfo wi2 = new WeatherInfo();
    WeatherInfo wi3 = new WeatherInfo();

    @Test
    public void testWeatherInfo() throws Exception {

        wi1.temp = 65;
        wi1.IconId = -6187;
        wi1.testInt = 32000;
        wi1.date = new Date();
        wi1.string = "Successfully authenticated apiuser";

        wi2.temp = 63;
        wi2.IconId = 6187;
        wi3.testInt = 32000;
        wi2.date = new Date();
        wi2.string = "stopped Daemons could not be reused";

        wi3.temp = 55;
        wi3.IconId = 16187;
        wi3.testInt = 32000;
        wi3.date = new Date();
        wi3.string = "verwendet nicht geprüfte oder unsichere Vorgänge";

        SerializableArrayList<WeatherInfo> serializeList = new SerializableArrayList<WeatherInfo>(WeatherInfo.class);

        serializeList.add(wi1);
        serializeList.add(wi2);
        serializeList.add(wi3);


        //try random values

        int length = 32;
        boolean useLetters = true;
        boolean useNumbers = true;
        Random random = new Random();

        int count = 82000;
        for (int i = 0; i < count; i++) {
            WeatherInfo wi = new WeatherInfo();

            wi.temp = random.nextInt();
            wi.IconId = random.nextInt();
            wi.testInt = random.nextInt();
            wi.date = new Date();
            random.nextInt(10, 200);
            wi.string = RandomStringUtils.random(length, useLetters, useNumbers);

            serializeList.add(wi);
        }

        Analyse analyse = new Analyse(serializeList);
        analyse.printAnalyse();

    }


}
