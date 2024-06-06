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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StackTest {

    @Test
    void push() {
        Stack<Integer> stack = new Stack();

        stack.push(1);
        stack.push(2);
        stack.push(5);

        assertEquals(1, stack.get(2));
        assertEquals(2, stack.get(1));
        assertEquals(5, stack.get(0));

        assertEquals(3, stack.size());
    }

    @Test
    void add() {
        Stack<Integer> stack = new Stack();

        stack.add(1);
        stack.add(2);
        stack.add(5);

        assertEquals(1, stack.get(0));
        assertEquals(2, stack.get(1));
        assertEquals(5, stack.get(2));

        assertEquals(3, stack.size());
    }

    @Test
    void pop() {
        Stack<Integer> stack = new Stack();

        stack.add(1);
        stack.add(2);
        stack.add(5);
        assertEquals(3, stack.size());
        assertEquals(1, stack.pop());
        assertEquals(2, stack.size());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.size());
        assertEquals(5, stack.pop());
        assertEquals(0, stack.size());
    }

    @Test
    void peek() {
        Stack<Integer> stack = new Stack();

        stack.add(1);
        stack.add(2);
        stack.add(5);
        assertEquals(3, stack.size());
        assertEquals(5, stack.peek());
        assertEquals(2, stack.size());
        assertEquals(2, stack.peek());
        assertEquals(1, stack.size());
        assertEquals(1, stack.peek());
        assertEquals(0, stack.size());
    }


    @Test
    void testToString() {
        Stack<Integer> stack = new Stack();

        stack.add(1);
        stack.add(2);
        stack.add(5);

        assertEquals("Stack of java.lang.Integer  > 1  > 2  > 5", stack.toString());
    }

    @Test
    void clear() {
        Stack<Integer> stack = new Stack();

        stack.add(1);
        stack.add(2);
        stack.add(5);
        assertEquals(3, stack.size());
        stack.clear();
        assertEquals(0, stack.size());
        assertNull(stack.get(0));

    }
}