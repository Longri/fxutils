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

import de.longri.fx.utils.SleepCall;
import de.longri.gdx_utils.Array;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;

public class StringBuilderPrintStream extends PrintStream {

    public interface StringBuilderPrintStreamChange {
        void stringChanged();
    }

    StringBuilder sb = new StringBuilder();
    Array<StringBuilderPrintStreamChange> listeners = new Array<>();


    public StringBuilderPrintStream() throws IOException {
        super(Files.createTempFile("tmp", ".file").toFile());
    }

    public void addChangeListener(StringBuilderPrintStreamChange listener) {
        listeners.add(listener);
    }

    private void fireChange() {
        new SleepCall(10, () -> {
            for (StringBuilderPrintStreamChange listener : listeners) {
                listener.stringChanged();
            }
        });
    }

    @Override
    public void print(String s) {
        synchronized (sb) {
            sb.append(s);
        }
        fireChange();
    }

    @Override
    public void print(Object x) {
        synchronized (sb) {
            sb.append(x.toString());
        }
        fireChange();
    }

    @Override
    public void println(String s) {
        synchronized (sb) {
            sb.append(s).append("\n");
        }
        fireChange();
    }

    @Override
    public void println(Object x) {
        synchronized (sb) {
            sb.append(x.toString()).append("\n");
        }
        fireChange();
    }

    @Override
    public String toString() {
        synchronized (sb) {
            return sb.toString();
        }
    }

    public void clear() {
        synchronized (sb) {
            sb = new StringBuilder();
        }

    }
}
