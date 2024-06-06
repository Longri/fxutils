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

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import static org.junit.jupiter.api.Assertions.assertEquals;


class PasswordStringPropertyTest {

    @Test
    void testToString() throws GeneralSecurityException, UnsupportedEncodingException {
        PasswordStringProperty psp = new PasswordStringProperty("value");
        assertEquals("PasswordStringProperty [value: ******lue]", psp.toString());


        SimpleStringProperty simpleStringProperty = new SimpleStringProperty("simple");
        psp.bind(simpleStringProperty);
        assertEquals("PasswordStringProperty [bound, invalid]", psp.toString());

        try {
            psp.set("newValue");
        } catch (RuntimeException e) {
            assertEquals("A bound value cannot be set.", e.getMessage());
        }

        assertEquals("simple", psp.get());
        assertEquals("PasswordStringProperty [bound, value: ******ple]", psp.toString());

        psp.unbind();
        assertEquals("simple", psp.get());
        assertEquals("PasswordStringProperty [value: ******ple]", psp.toString());
    }

    @Test
    void fireValueChangedEvent() throws GeneralSecurityException, UnsupportedEncodingException {
        PasswordStringProperty psp = new PasswordStringProperty("val");
        SimpleStringProperty spi = new SimpleStringProperty("notChanged");
        SimpleStringProperty spc = new SimpleStringProperty("notChanged");

        InvalidationListener invalidationListener = new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                spi.set(((PasswordStringProperty) observable).get());
            }
        };
        ChangeListener changedListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object o, Object t1) {
                spc.set(((PasswordStringProperty) observable).get());
            }
        };

        assertEquals("val", psp.get());
        assertEquals("notChanged", spc.get());
        assertEquals("notChanged", spi.get());

        psp.set("new1");
        assertEquals("new1", psp.get());
        assertEquals("notChanged", spc.get());
        assertEquals("notChanged", spi.get());

        psp.addListener(changedListener);
        psp.set("new2");
        assertEquals("new2", psp.get());
        assertEquals("new2", spc.get());
        assertEquals("notChanged", spi.get());

        psp.addListener(invalidationListener);
        psp.set("new3");
        assertEquals("new3", psp.get());
        assertEquals("new3", spc.get());
        assertEquals("new3", spi.get());

        psp.removeListener(changedListener);
        psp.set("new4");
        assertEquals("new4", psp.get());
        assertEquals("new3", spc.get());
        assertEquals("new4", spi.get());

        psp.removeListener(invalidationListener);
        psp.set("new5");
        assertEquals("new5", psp.get());
        assertEquals("new3", spc.get());
        assertEquals("new4", spi.get());
    }

    @Test
    void getDebugString() throws GeneralSecurityException, UnsupportedEncodingException {
        PasswordStringProperty psp = new PasswordStringProperty("val");
        assertEquals("***val", psp.getDebugString());
        psp.set("encryptedValue");
        assertEquals("******lue", psp.getDebugString());
    }

    @Test
    void bindingTest() throws GeneralSecurityException, IOException {

        StringProperty stringProperty = new SimpleStringProperty("string");
        PasswordStringProperty passwordStringProperty = new PasswordStringProperty();
        passwordStringProperty.setEncryptValue("9jWDGPTAPek=");

        assertEquals("string", stringProperty.get());
        stringProperty.bindBidirectional(passwordStringProperty);
        assertEquals("******ord", passwordStringProperty.getDebugString());
        assertEquals("password", stringProperty.get());
        assertEquals("password", passwordStringProperty.get());
        assertEquals("9jWDGPTAPek=", passwordStringProperty.getEncrypted());
        assertEquals("password", Crypto.decrypt(passwordStringProperty.getEncrypted(), PasswordStringProperty.DEFAULT_KEY));

        stringProperty.set("newSetValue");
        assertEquals("newSetValue", passwordStringProperty.get());

        stringProperty.set("password");
        assertEquals("password", passwordStringProperty.get());

        stringProperty.unbindBidirectional(passwordStringProperty);
        stringProperty.set("string");
        assertEquals("string", stringProperty.get());
        assertEquals("******ord", passwordStringProperty.getDebugString());


        passwordStringProperty.bindBidirectional(stringProperty);
        stringProperty.set("password");
        assertEquals("password", stringProperty.get());
        assertEquals("password", passwordStringProperty.get());
        assertEquals("9jWDGPTAPek=", passwordStringProperty.getEncrypted());
        assertEquals("password", Crypto.decrypt(passwordStringProperty.getEncrypted(), PasswordStringProperty.DEFAULT_KEY));

        passwordStringProperty.setEncryptValue(null);
        assertEquals(null, stringProperty.get());
        assertEquals(null, passwordStringProperty.get());
    }

    @Test
    void bindingDifKeyTest() throws GeneralSecurityException, IOException {

        int[] key = new int[]{1892, 456, 1, 198};

        StringProperty stringProperty = new SimpleStringProperty("string");
        PasswordStringProperty passwordStringProperty = new PasswordStringProperty(key);
        passwordStringProperty.set("password");

        assertEquals("string", stringProperty.get());
        stringProperty.bindBidirectional(passwordStringProperty);
        assertEquals("******ord", passwordStringProperty.getDebugString());
        assertEquals("password", stringProperty.get());
        assertEquals("password", passwordStringProperty.get());
        assertEquals("F6kPN+aGGN4=", passwordStringProperty.getEncrypted());
        assertEquals("password", Crypto.decrypt(passwordStringProperty.getEncrypted(), key));

        stringProperty.unbindBidirectional(passwordStringProperty);
        stringProperty.set("string");
        assertEquals("string", stringProperty.get());
        assertEquals("******ord", passwordStringProperty.getDebugString());


        passwordStringProperty.bindBidirectional(stringProperty);
        stringProperty.set("password");
        assertEquals("password", stringProperty.get());
        assertEquals("password", passwordStringProperty.get());
        assertEquals("F6kPN+aGGN4=", passwordStringProperty.getEncrypted());
        assertEquals("password", Crypto.decrypt(passwordStringProperty.getEncrypted(), key));


    }

    @Test
    void equalsTest() throws GeneralSecurityException, UnsupportedEncodingException {
        PasswordStringProperty psp = new PasswordStringProperty("value");
        PasswordStringProperty psp2 = new PasswordStringProperty("value");

        assertEquals(psp, psp2);
    }

}