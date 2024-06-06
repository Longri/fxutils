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

import javafx.scene.effect.BlendMode;

public class DialogHtmlContent {

    private boolean isHtmlContent = true;
    private String htmlContent;
    private double prefWidth = 200;
    private double prefHeight = 200;
    private BlendMode blendMode;

    public DialogHtmlContent() {
    }

    public DialogHtmlContent(String content) {
        this.setHtmlContent(content);
    }

    public void setHtmlContent(String content) {
        this.htmlContent = content;
    }

    public void setIsHtmlContent(boolean isHtmlContent) {
        this.isHtmlContent = isHtmlContent;
    }

    public String getHtmlContent() {
        return this.htmlContent;
    }

    public boolean isHtmlContent() {
        return this.isHtmlContent;
    }


    /**
     * Convenience method for setting preferred width and height.
     *
     * @param prefWidth  the preferred width
     * @param prefHeight the preferred height
     */
    public void setPrefSize(double prefWidth, double prefHeight) {
        setPrefWidth(prefWidth);
        setPrefHeight(prefHeight);
    }

    public void setPrefWidth(double prefWidth) {
        this.prefWidth = prefWidth;
    }

    public double getPrefWidth() {
        return prefWidth;
    }

    public void setPrefHeight(double prefHeight) {
        this.prefHeight = prefHeight;
    }

    public double getPrefHeight() {
        return prefHeight;
    }

    public void setBlendMode(BlendMode value) {
        this.blendMode = value;
    }

    public BlendMode getBlendMode() {
        return this.blendMode;
    }
}
