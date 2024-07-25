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

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TimeZone;

public abstract class NamedObjectProperty<T> extends ObjectProperty<T> {

    protected String NAME;
    protected T value;

    public NamedObjectProperty(String name) {
        this.NAME = name;
    }

    public void set(T value) {
        this.value = value;
    }

    public String getName() {
        return this.NAME;
    }

    public T get() {
        return value;
    }

    public void rename(String newName) {
        this.NAME = newName;
    }

    @Override
    public String toString() {
        String val = value == null ? "NULL" : value.toString();
        return "Property '" + NAME + "' value: '" + val + "'";
    }

    static public class NamedStringProperty extends NamedObjectProperty<String> {
        public NamedStringProperty(String name) {
            super(name);
        }
    }

    static public class NamedBoolProperty extends NamedObjectProperty<Boolean> {
        public NamedBoolProperty(String name) {
            super(name);
            this.value = false;
        }
    }

    static public class NamedIntegerProperty extends NamedObjectProperty<Integer> {
        public NamedIntegerProperty(String name) {
            super(name);
            this.value = 0;
        }
    }

    static public class NamedDateProperty extends NamedObjectProperty<Integer> {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss");

        public NamedDateProperty(String name) {
            super(name);
            this.value = 0;
        }

        public String toString() {
            // convert unix time to readable
            LocalDateTime triggerTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(this.get()),
                    TimeZone.getDefault().toZoneId());
            String valueString = formatter.format(triggerTime);

            return "Property '" + NAME + "' value: '" + valueString + "'";
        }
    }

    static public class NamedLocalDateTimeProperty extends NamedObjectProperty<LocalDateTime> {
        DateTimeFormatter formatterGerman = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        DateTimeFormatter formatterUS = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");


        public NamedLocalDateTimeProperty(String name) {
            super(name);
            this.value = LocalDateTime.MIN;
        }

        public String toString() {
            // convert unix time to readable
            LocalDateTime triggerTime = this.get();
            String valueString = triggerTime == null ? "NULL" : formatterGerman.format(triggerTime);
            return "Property '" + NAME + "' value: '" + valueString + "'";
        }

        public String getAsString() {
            if (this.get() == null) return "NULL";
            if (this.get() == LocalDateTime.MIN) return "LocalDateTime.MIN";
            if (this.get() == LocalDateTime.MAX) return "LocalDateTime.MAX";
            return formatterGerman.format(this.get());
        }

        public void setFromString(String value) {
            if ("NULL".equals(value)) this.set(null);
            else if ("LocalDateTime.MIN".equals(value)) this.set(LocalDateTime.MIN);
            else if ("LocalDateTime.MAX".equals(value)) this.set(LocalDateTime.MAX);
            else {
                try {
                    this.set(LocalDateTime.parse(value, formatterGerman));
                } catch (Exception e) {
                    this.set(LocalDateTime.parse(value, formatterUS));
                }
            }
        }
    }


    protected InvalidationListener listener = null;
    protected ObservableValue<? extends T> observable = null;
    protected final ArrayList<ChangeListener> changeListenerArrayList = new ArrayList<>();
    protected final ArrayList<InvalidationListener> invalidationListenerArrayList = new ArrayList<>();
    protected boolean valid = true;


    @Override
    public boolean isBound() {
        return this.observable != null;
    }

    @Override
    public void bind(ObservableValue<? extends T> var1) {
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
            this.observable.removeListener(this.listener);
            this.observable = null;
        }

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

    protected void fireValueChangedEvent() {
        for (ChangeListener listener : changeListenerArrayList) {
            listener.changed(this, null, null);
        }
    }

    @Override
    public Object getBean() {
        return null;
    }

    @Override
    public void addListener(ChangeListener<? super T> changeListener) {
        this.changeListenerArrayList.add(changeListener);
    }

    @Override
    public void removeListener(ChangeListener<? super T> changeListener) {
        this.changeListenerArrayList.remove(changeListener);
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        this.invalidationListenerArrayList.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        this.invalidationListenerArrayList.remove(invalidationListener);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NamedObjectProperty<?> that = (NamedObjectProperty<?>) obj;
        return NAME.equals(that.NAME) && Objects.equals(value, that.value);
    }


}


