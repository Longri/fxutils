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
package de.longri.fx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MultiResourceBundle extends ResourceBundle {

    private final static Logger log = LoggerFactory.getLogger(MultiResourceBundle.class);

    private final HashMap<Locale, HashMap<String, String>> LOCALE_HASH_MAP = new HashMap<>();
    private final ArrayList<String> KEYS = new ArrayList<>();
    private final ArrayList<String> loadedBundleNames = new ArrayList<>();

    private Locale actSetLocale = Locale.ENGLISH;

    public void loadBundle(String name, Locale... locales) {

        if (name == null || name.isEmpty()) return;

        if (loadedBundleNames.contains(name)) {
            log.debug("bundle name '{}' is already loaded", name);
            return;
        }

        log.debug("load bundle '{}'", name);

        if (locales == null || locales.length == 0) {
            // set locales to ENG amd GER, for backwards compatible
            locales = new Locale[]{Locale.ENGLISH, Locale.GERMAN};
        }

        for (Locale locale : locales) {
            HashMap<String, String> map = LOCALE_HASH_MAP.get(locale);

            if (map == null) {
                // create new and add
                map = new HashMap<>();
                LOCALE_HASH_MAP.put(locale, map);
            }

            //read all keys and strings and add to map
            ResourceBundle rb = ResourceBundle.getBundle(name, locale);
            for (String key : rb.keySet()) {
                String value = rb.getString(key);
                map.put(key, value);
                if (!KEYS.contains(key)) KEYS.add(key);
            }
        }
    }

    public void setLocale(Locale locale) {
        this.actSetLocale = locale;
    }


    /**
     * Gets an object for the given key from this resource bundle.
     * Returns null if this resource bundle does not contain an
     * object for the given key.
     *
     * @param key the key for the desired object
     * @return the object for the given key, or null
     * @throws NullPointerException if {@code key} is {@code null}
     */
    @Override
    protected Object handleGetObject(String key) {
        HashMap<String, String> map = LOCALE_HASH_MAP.get(actSetLocale);
        return map.get(key);
    }

    /**
     * Returns an enumeration of the keys.
     *
     * @return an {@code Enumeration} of the keys contained in
     * this {@code ResourceBundle} and its parent bundles.
     */
    @Override
    public Enumeration<String> getKeys() {
        return new Enumeration<String>() {

            int enumPos = 0;

            @Override
            public boolean hasMoreElements() {
                return KEYS.size() > enumPos;
            }

            @Override
            public String nextElement() {
                return KEYS.get(enumPos++);
            }
        };
    }
}
