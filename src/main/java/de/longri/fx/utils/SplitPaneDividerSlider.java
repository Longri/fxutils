/*
 * Copyright 2014 Jens Deters.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.longri.fx.utils;

import javafx.animation.Transition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * found on https://bitbucket.org/Jerady/shichimifx/src/master/
 *
 * @author Jens Deters (www.jensd.de)
 * @version 1.0.0
 * @since 14-10-2014
 */
public class SplitPaneDividerSlider {

    private static final Logger log = LoggerFactory.getLogger(SplitPaneDividerSlider.class);

    public enum Direction {
        UP, DOWN, LEFT, RIGHT;
    }

    private final Direction direction;
    private final SplitPane splitPane;
    private final int dividerIndex;
    private BooleanProperty aimContentVisibleProperty;
    private DoubleProperty lastDividerPositionProperty;
    private DoubleProperty currentDividerPositionProperty;
    private Region content;
    private double contentInitialMinWidth;
    private double contentInitialMinHeight;
    private Transition slideTransition;
    private Duration cycleDuration;
    private SplitPane.Divider dividerToMove;
    private final double minSize;
    private final double expandSize;

    public SplitPaneDividerSlider(SplitPane splitPane, int dividerIndex, Direction direction, double minSize) {
        this(splitPane, dividerIndex, direction, Duration.millis(7000.0), minSize);
    }

    public SplitPaneDividerSlider(SplitPane splitPane,
                                  int dividerIndex,
                                  Direction direction,
                                  Duration cycleDuration,
                                  double minSize) {
        this.direction = direction;
        this.splitPane = splitPane;
        this.dividerIndex = dividerIndex;
        this.cycleDuration = cycleDuration;
        this.minSize = minSize;
        expandSize = 100;
        init();
    }

    public void init() {
        slideTransition = new SlideTransition(cycleDuration);

        // figure out right splitpane content
        switch (direction) {
            case LEFT:
            case UP:
                content = (Region) splitPane.getItems().get(dividerIndex);
                break;
            case RIGHT:
            case DOWN:
                content = (Region) splitPane.getItems().get(dividerIndex + 1);
                break;
        }
        contentInitialMinHeight = content.getMinHeight();
        contentInitialMinWidth = content.getMinWidth();
        dividerToMove = splitPane.getDividers().get(dividerIndex);


        aimContentVisibleProperty().addListener((ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) -> {
            if (!newValue) {
                // store divider position before transition:
                setLastDividerPosition(splitPane.getDividers().get(dividerIndex).getPosition());
                // "arm" current divider position before transition:
                setCurrentDividerPosition(getLastDividerPosition());
            }

            switch (direction) {
                case LEFT:
                case RIGHT:
                    content.setMinSize(this.minSize, 0.0);
                    break;
                case UP:
                case DOWN:
                    content.setMinSize(0.0, this.minSize);
                    break;
            }
            slideTransition.play();
        });

        switch (direction) {
            case LEFT:
            case UP:
                splitPane.widthProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        ((SlideTransition) slideTransition).contentSize = newValue.doubleValue();
                    }
                });
                break;
            case RIGHT:
            case DOWN:
                content = (Region) splitPane.getItems().get(dividerIndex + 1);
                break;
        }

    }


    public SplitPane.Divider getDividerToMove() {
        return dividerToMove;
    }

    private void restoreContentSize() {
        content.setMinHeight(contentInitialMinHeight);
        content.setMinWidth(contentInitialMinWidth);
        setCurrentDividerPosition(getLastDividerPosition());
    }

    public BooleanProperty aimContentVisibleProperty() {
        if (aimContentVisibleProperty == null) {
            aimContentVisibleProperty = new SimpleBooleanProperty(true);
        }
        return aimContentVisibleProperty;
    }

    public void setAimContentVisible(boolean aimContentVisible) {
        aimContentVisibleProperty().set(aimContentVisible);
    }

    public boolean isAimContentVisible() {
        return aimContentVisibleProperty().get();
    }

    public DoubleProperty lastDividerPositionProperty() {
        if (lastDividerPositionProperty == null) {
            lastDividerPositionProperty = new SimpleDoubleProperty();
        }
        return lastDividerPositionProperty;
    }

    public double getLastDividerPosition() {
        return lastDividerPositionProperty().get();
    }

    public void setLastDividerPosition(double lastDividerPosition) {
        lastDividerPositionProperty().set(lastDividerPosition);
    }

    public DoubleProperty currentDividerPositionProperty() {
        if (currentDividerPositionProperty == null) {
            currentDividerPositionProperty = new SimpleDoubleProperty();
        }
        return currentDividerPositionProperty;
    }

    public double getCurrentDividerPosition() {
        return currentDividerPositionProperty().get();
    }

    public void setCurrentDividerPosition(double currentDividerPosition) {
        currentDividerPositionProperty().set(currentDividerPosition);
        dividerToMove.setPosition(currentDividerPosition);
    }

    private class SlideTransition extends Transition {

        private double contentSize = -1;
        private double minContentSIZE;

        public SlideTransition(final Duration cycleDuration) {
            setCycleDuration(cycleDuration);
        }

        @Override
        protected void interpolate(double d) {

            double slideMinPos = 0.0;
            if (contentSize > 0) {
                slideMinPos = minContentSIZE / contentSize;
            }


            switch (direction) {
                case LEFT:
                case UP:
                    // intent to slide in content:  
                    if (isAimContentVisible()) {
                        if ((getCurrentDividerPosition() + d) <= getLastDividerPosition()) {
                            setCurrentDividerPosition(getCurrentDividerPosition() + d);
                        } else { //DONE
                            restoreContentSize();
                            stop();
                        }
                    } // intent to slide out content:  
                    else {
                        if (getCurrentDividerPosition() > slideMinPos) {
                            setCurrentDividerPosition(getCurrentDividerPosition() - d);
                        } else { //DONE
                            setCurrentDividerPosition(slideMinPos);
                            stop();
                        }
                    }
                    break;
                case RIGHT:
                case DOWN:
                    // intent to slide in content:  
                    if (isAimContentVisible()) {
                        if ((getCurrentDividerPosition() - d) >= getLastDividerPosition()) {
                            setCurrentDividerPosition(getCurrentDividerPosition() - d);
                        } else { //DONE
                            restoreContentSize();
                            stop();
                        }
                    } // intent to slide out content:  
                    else {
                        if (getCurrentDividerPosition() < 1.0) {
                            setCurrentDividerPosition(getCurrentDividerPosition() + d);
                        } else {//DONE
                            setCurrentDividerPosition(1.0);
                            stop();
                        }
                    }
                    break;
            }
        }
    }
}