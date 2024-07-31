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

import de.longri.utils.iCondition;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public abstract class FxmlConditionScene extends FxmlScene implements iCondition {
    public FxmlConditionScene(FXMLLoader fxmlLoader, String name, boolean resizeable) throws IOException {
        super(fxmlLoader, name, resizeable);
    }

    public FxmlConditionScene(SelfLoading_Controller selfLoadingController, String name, boolean resizeable) {
        super(selfLoadingController, name, resizeable);
    }

    public abstract boolean isShowing();

    public abstract void setIsShowing();
}
