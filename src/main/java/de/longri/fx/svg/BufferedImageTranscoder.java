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
package de.longri.fx.svg;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import java.awt.image.BufferedImage;
import java.io.InputStream;

public class BufferedImageTranscoder extends ImageTranscoder {

    public static void replaceImageViewWithSvg(ImageView imageView, InputStream inputStream, double fitWidth,double fitHeight,boolean preserveRatio) {
        BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
        try {
            TranscoderInput transIn = new TranscoderInput(inputStream);
            try {
                transcoder.transcode(transIn, null);
                Image img = SwingFXUtils.toFXImage(transcoder.getBufferedImage(), null);
                imageView.setImage(img);
                imageView.setFitHeight(fitHeight);
                imageView.setFitWidth(fitWidth);
                imageView.setPreserveRatio(preserveRatio);
            } catch (TranscoderException ex) {
                ex.printStackTrace();
            }
        } catch (Exception io) {
            io.printStackTrace();
        }
    }


    private BufferedImage img = null;

    @Override
    public BufferedImage createImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void writeImage(BufferedImage img, TranscoderOutput to) throws TranscoderException {
        this.img = img;
    }

    public BufferedImage getBufferedImage() {
        return img;
    }
}
