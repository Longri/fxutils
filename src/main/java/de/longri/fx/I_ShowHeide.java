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

import javafx.fxml.Initializable;
import javafx.stage.Stage;

public interface I_ShowHeide extends Initializable {

    void show(Object data, Stage stage);

    void beforeHide();

    void hide();

    default double getMinWidth() {
        return 0.0;
    }

    default double getMinHeight() {
        return 0.0;
    }

    default double getMaxWidth() {
        return Double.MAX_VALUE;
    }

    default double getMaxHeight() {
        return Double.MAX_VALUE;
    }

    void fxStyleChanged(FxStyles style);

    String getTitle();

    default void firstShow() {
    }
}
