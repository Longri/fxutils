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
package de.longri.fx.utils;

import javafx.scene.Node;

public class FxFocusNode {

    final Node focus;

    Node UP;
    Node DOWN;
    Node LEFT;
    Node RIGHT;
    Node NEXT;
    Node PREVIOUS;

    public FxFocusNode(Node node, Node next, Node previous) {
        this.focus = node;
        this.NEXT = this.RIGHT = next;
        this.PREVIOUS = this.LEFT = previous;
    }

    public void setFocus(FxFocusDirection direction) {
        switch (direction) {
            case DOWN:
                if (DOWN != null) {
                    DOWN.requestFocus();
                    break;
                }
            case RIGHT:
                if (RIGHT != null) {
                    RIGHT.requestFocus();
                    break;
                }
            case NEXT:
                if (NEXT != null) {
                    NEXT.requestFocus();
                    break;
                }
                break;
            case LEFT:
                if (LEFT != null) {
                    LEFT.requestFocus();
                    break;
                }
            case UP:
                if (UP != null) {
                    UP.requestFocus();
                    break;
                }
            case PREVIOUS:
                if (PREVIOUS != null) {
                    PREVIOUS.requestFocus();
                    break;
                }
        }
    }

    public void setUP(Node UP) {
        this.UP = UP;
    }

    public void setDOWN(Node DOWN) {
        this.DOWN = DOWN;
    }

    public void setLEFT(Node LEFT) {
        this.LEFT = LEFT;
    }

    public void setRIGHT(Node RIGHT) {
        this.RIGHT = RIGHT;
    }

    public void setNEXT(Node NEXT) {
        this.NEXT = NEXT;
    }

    public void setPREVIOUS(Node PREVIOUS) {
        this.PREVIOUS = PREVIOUS;
    }
}
