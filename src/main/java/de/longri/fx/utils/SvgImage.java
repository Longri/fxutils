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

import de.longri.fx.svg.BufferedImageTranscoder;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import static java.awt.Image.SCALE_SMOOTH;
import static org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_HEIGHT;
import static org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_WIDTH;

public class SvgImage extends Image {
    /**
     * Constructs an {@code Image} with content loaded from the specified
     * input stream.
     *
     * @param is the stream from which to load the image
     * @throws NullPointerException if input stream is null
     */
    public SvgImage(InputStream is) throws TranscoderException, IOException {
        super(getPngInputStream(is, -1, -1));
    }

    /**
     * Constructs an {@code Image} with content loaded from the specified
     * input stream.
     *
     * @param is the stream from which to load the image
     * @throws NullPointerException if input stream is null
     */
    public SvgImage(InputStream is, int width, int height) throws TranscoderException, IOException {
        super(getPngInputStream(is, width, height));
    }

    private static InputStream getPngInputStream(InputStream inputStream, int width, int height) throws TranscoderException, IOException {
        WritableImage targetBufferredImage = null;
        if (width > 0 && height > 0) targetBufferredImage = new WritableImage(width, height);
        BufferedImageTranscoder transcoder = new BufferedImageTranscoder();


        if (width > 0) transcoder.addTranscodingHint(KEY_WIDTH, Float.valueOf(width));
        if (height > 0) transcoder.addTranscodingHint(KEY_HEIGHT, Float.valueOf(height));


        TranscoderInput transIn = new TranscoderInput(inputStream);
        transcoder.transcode(transIn, (TranscoderOutput) null);


        Image img = SwingFXUtils.toFXImage(transcoder.getBufferedImage(), targetBufferredImage);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", outputStream);
        byte[] bytes = outputStream.toByteArray();
        ByteArrayInputStream returnStream = new ByteArrayInputStream(bytes);
        return returnStream;
    }

}
