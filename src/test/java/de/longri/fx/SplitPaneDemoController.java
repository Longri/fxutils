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
package de.longri.fx;

import java.net.URL;
import java.util.ResourceBundle;

import de.longri.fx.utils.FontAwesomeIcon;
import de.longri.fx.utils.GlyphsFactory;
import de.longri.fx.utils.SplitPaneDividerSlider;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;

/**
 *
 * @author Jens Deters (www.jensd.de)
 * @version 1.0.0
 * @since 14-10-2014
 */
public class SplitPaneDemoController implements Initializable {

    @FXML
    private ToggleButton bottomToggleButton;

    @FXML
    private SplitPane centerSplitPane;

    @FXML
    private ToggleButton leftToggleButton;

    @FXML
    private SplitPane mainSplitPane;

    @FXML
    private ToggleButton rightToggleButton;

    @FXML
    private ToggleButton topToggleButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        SplitPaneDividerSlider leftSplitPaneDividerSlider = new SplitPaneDividerSlider(centerSplitPane, 0, SplitPaneDividerSlider.Direction.LEFT,50.0);
        SplitPaneDividerSlider rightSplitPaneDividerSlider = new SplitPaneDividerSlider(centerSplitPane, 1, SplitPaneDividerSlider.Direction.RIGHT,40.0);
        SplitPaneDividerSlider topSplitPaneDividerSlider = new SplitPaneDividerSlider(mainSplitPane, 0, SplitPaneDividerSlider.Direction.UP,0.0);
        SplitPaneDividerSlider bottomSplitPaneDividerSlider = new SplitPaneDividerSlider(mainSplitPane, 1, SplitPaneDividerSlider.Direction.DOWN,0.0);

        leftToggleButton.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            leftSplitPaneDividerSlider.setAimContentVisible(t1);
        });
        rightToggleButton.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            rightSplitPaneDividerSlider.setAimContentVisible(t1);
        });
        topToggleButton.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            topSplitPaneDividerSlider.setAimContentVisible(t1);
        });
        bottomToggleButton.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            bottomSplitPaneDividerSlider.setAimContentVisible(t1);
        });

        GlyphsFactory gf = new GlyphsFactory(FontAwesomeIcon.class);
        gf.setIcon(leftToggleButton, FontAwesomeIcon.TOGGLE_LEFT, "2em");
        gf.setIcon(rightToggleButton, FontAwesomeIcon.TOGGLE_RIGHT, "2em");
        gf.setIcon(topToggleButton, FontAwesomeIcon.TOGGLE_UP, "2em");
        gf.setIcon(bottomToggleButton, FontAwesomeIcon.TOGGLE_DOWN, "2em");

        leftToggleButton.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            if (t1) {
                gf.setIcon(leftToggleButton, FontAwesomeIcon.TOGGLE_LEFT, "2em");
            } else {
                gf.setIcon(leftToggleButton, FontAwesomeIcon.TOGGLE_RIGHT, "2.5em");
            }
        });

        rightToggleButton.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            if (t1) {
                gf.setIcon(rightToggleButton, FontAwesomeIcon.TOGGLE_RIGHT, "2em");
            } else {
                gf.setIcon(rightToggleButton, FontAwesomeIcon.TOGGLE_LEFT, "2.5em");
            }
        });

        topToggleButton.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            if (t1) {
                gf.setIcon(topToggleButton, FontAwesomeIcon.TOGGLE_UP, "2em");
            } else {
                gf.setIcon(topToggleButton, FontAwesomeIcon.TOGGLE_DOWN, "2.5em");
            }
        });

        bottomToggleButton.selectedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            if (t1) {
                gf.setIcon(bottomToggleButton, FontAwesomeIcon.TOGGLE_DOWN, "2em");
            } else {
                gf.setIcon(bottomToggleButton, FontAwesomeIcon.TOGGLE_UP, "2.5em");
            }
        });

    }
}
