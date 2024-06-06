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

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FxFocusOrderHelper implements EventHandler<KeyEvent> {

    ArrayList<FxFocusNode> orderList = new ArrayList<>();



    public FxFocusOrderHelper(){
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    boolean pause = false;

    public void addFocusNode(FxFocusNode node) {
        node.focus.addEventHandler(KeyEvent.KEY_PRESSED, this);
        if (orderList.size() == 0) {
            if (!this.pause) {
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(node.focus::requestFocus);
                    }
                };
                timer.schedule(task, 200);
            }
        }
        orderList.add(node);
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        if (pause) return;
        if (keyEvent.getEventType() != KeyEvent.KEY_PRESSED) return;
        KeyCode code = keyEvent.getCode();
        FxFocusDirection dir = null;
        if (code == KeyCode.TAB) {
            if (keyEvent.isShiftDown()) {
                dir = FxFocusDirection.PREVIOUS;
            } else {
                dir = FxFocusDirection.NEXT;
            }
        } else if (code == KeyCode.LEFT) {
            dir = FxFocusDirection.LEFT;
        } else if (code == KeyCode.RIGHT) {
            dir = FxFocusDirection.RIGHT;
        } else if (code == KeyCode.UP) {
            dir = FxFocusDirection.UP;
        } else if (code == KeyCode.DOWN) {
            dir = FxFocusDirection.DOWN;
        }

        if (dir != null) {
            EventTarget target = keyEvent.getTarget();
            if (target instanceof Node) {
                FxFocusNode focusNode = null;
                for (FxFocusNode fn : orderList) {
                    if (fn.focus == target) {
                        focusNode = fn;
                        break;
                    }
                }
                if (focusNode != null) {
                    keyEvent.consume();
                    focusNode.setFocus(dir);
                }
            }
        }
    }
}
