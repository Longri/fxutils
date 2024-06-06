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
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;

public class PasswordStringProperty extends NamedObjectProperty<String> {

    private String encryptValue;



    protected static final int[] DEFAULT_KEY = new int[]{12, 27, 176, 12, 1};
    private final int[] KEY;

    public PasswordStringProperty() {
        super("");
        KEY = DEFAULT_KEY;
    }

    public PasswordStringProperty(String decryptedValue) throws GeneralSecurityException, UnsupportedEncodingException {
        super("");
        KEY = DEFAULT_KEY;
        this.encryptValue = Crypto.encrypt(decryptedValue, KEY);
    }

    public PasswordStringProperty(String name, String decryptedValue) throws GeneralSecurityException, UnsupportedEncodingException {
        super(name);
        KEY = DEFAULT_KEY;
        this.encryptValue = Crypto.encrypt(decryptedValue, KEY);
    }

    public PasswordStringProperty(int[] key) {
        super("");
        KEY = key;
    }

    public PasswordStringProperty(String decryptedValue, int[] key) throws GeneralSecurityException, UnsupportedEncodingException {
        super("");
        KEY = key;
        this.encryptValue = Crypto.encrypt(decryptedValue, KEY);
    }

    public PasswordStringProperty(String name, String decryptedValue, int[] key) throws GeneralSecurityException, UnsupportedEncodingException {
        super(name);
        KEY = DEFAULT_KEY;
        this.encryptValue = Crypto.encrypt(decryptedValue, KEY);
    }

    @Override
    public boolean isBound() {
        return this.observable != null;
    }

    @Override
    public void bind(ObservableValue<? extends String> var1) {
        if (var1 == null) {
            throw new NullPointerException("Cannot bind to null");
        } else {
            if (!var1.equals(this.observable)) {
                this.unbind();
                this.observable = var1;
                if (this.listener == null) {
                    this.listener = new InvalidationListener() {
                        @Override
                        public void invalidated(Observable observable) {
                            markInvalid();
                        }
                    };
                }

                this.observable.addListener(this.listener);
                this.markInvalid();
            }
        }
    }

    @Override
    public void unbind() {
        if (this.observable != null) {
            try {
                this.encryptValue = Crypto.encrypt((String) this.observable.getValue(), KEY);
            } catch (GeneralSecurityException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            this.observable.removeListener(this.listener);
            this.observable = null;
        }

    }

    @Override
    public String toString() {
        Object var1 = this.getBean();
        String var2 = this.getName();
        StringBuilder var3 = new StringBuilder("PasswordStringProperty [");
        if (var1 != null) {
            var3.append("bean: ").append(var1).append(", ");
        }

        if (var2 != null && !var2.equals("")) {
            var3.append("name: ").append(var2).append(", ");
        }

        if (this.isBound()) {
            var3.append("bound, ");
            if (this.valid) {
                var3.append("value: ").append(this.getDebugString());
            } else {
                var3.append("invalid");
            }
        } else {
            var3.append("value: ").append(this.getDebugString());
        }

        var3.append("]");
        return var3.toString();
    }

    protected void markInvalid() {
        if (this.valid) {
            this.valid = false;
            this.invalidated();
            this.fireValueChangedEvent();
        }
    }

    protected void invalidated() {
        for (InvalidationListener listener : invalidationListenerArrayList) {
            listener.invalidated(this);
        }
    }

    @Override
    public String get() {
        this.valid = true;
        String ret = "";
        if (this.observable == null) {
            if (encryptValue == null) return null;
            try {
                ret = Crypto.decrypt(encryptValue, KEY);
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        } else {
            ret = (String) this.observable.getValue();
        }
        return ret;
    }


    @Override
    public void set(String var1) {
        if (this.isBound()) {
            String var10002 = this.getBean() != null && this.getName() != null ? this.getBean().getClass().getSimpleName() + "." + this.getName() + " : " : "";
            throw new RuntimeException(var10002 + "A bound value cannot be set.");
        } else {
            if (var1 == null) {
                encryptValue = null;
            } else {
                try {
                    encryptValue = Crypto.encrypt(var1, KEY);
                } catch (UnsupportedEncodingException | GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
            this.markInvalid();
        }
    }

    public String getDebugString() {
        StringBuilder sb = new StringBuilder("******");
        String val = this.get();
        if (val.length() < 4) return "***" + val;
        sb.append(val.substring(val.length() - 3));
        return sb.toString();
    }

    public String getEncrypted() throws GeneralSecurityException, UnsupportedEncodingException {

        if (observable == null) {
            return encryptValue;
        } else {
            String ob = observable.getValue();
            return Crypto.encrypt(ob, KEY);
        }

    }

    public void setEncryptValue(String value) {

        if (value != null) {
            try {
                String decryptValue = Crypto.decrypt(value, KEY);
                set(decryptValue);
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            set(null);
        }
    }

    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other instanceof PasswordStringProperty) {
            // KEY and encryptValue must be equals
            PasswordStringProperty o = (PasswordStringProperty) other;
            if (Arrays.equals(this.KEY, o.KEY) && this.encryptValue.equals(o.encryptValue)) return true;
        }
        return false;
    }
}
