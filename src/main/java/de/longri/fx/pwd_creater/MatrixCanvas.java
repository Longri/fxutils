package de.longri.fx.pwd_creater;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Random;

public class MatrixCanvas extends Canvas {

    public static String color = "#00ff00"; // the color green

    public MatrixCanvas(StackPane stackPane) {
        super(stackPane.getWidth(), stackPane.getWidth());

        // bind the width and height properties when screen is resized.
        this.widthProperty().bind(stackPane.widthProperty());
        this.heightProperty().bind(stackPane.heightProperty());

        init();
        stackPane.getChildren().clear();
        stackPane.getChildren().add(this);
    }

    private void init() {

        Canvas canvas = this;

        final GraphicsContext gc = canvas.getGraphicsContext2D();
        int fontSize = 12; // font size in pixels

        // Animate the matrix effect
        new AnimationTimer() {
            long lastTimerCall = 0;
            final long NANOS_PER_MILLI = 1000000; //nanoseconds in a millisecond
            final long ANIMATION_DELAY = 50 * NANOS_PER_MILLI; // convert 50 ms to ns

            // Capture current dimensions of the Canvas
            int prevWidth = (int) canvas.getWidth();
            int prevHeight = (int) canvas.getHeight();

            // Keeps track of each column's y coordinate for next iteration to draw a character.
            int[] ypos = resize(gc, fontSize);

            // Random generator for characters and symbols
            Random random = new Random();

            @Override
            public void handle(long now) {
                // elapsed time occurred so let's begin drawing on the canvas.
                if (now > lastTimerCall + ANIMATION_DELAY) {
                    lastTimerCall = now;

                    int w = (int) canvas.getWidth();
                    int h = (int) canvas.getHeight();

                    // did resize occur?
                    if (w != prevWidth || h != prevHeight) {
                        // clear canvas and recalculate ypos array.
                        ypos = resize(gc, fontSize);
                        prevWidth = w;
                        prevHeight = h;
                    }

                    // Each frame add a small amount of transparent black to the canvas essentially
                    // creating a fade effect.
                    gc.setFill(Color.web("#0001"));
                    gc.fillRect(0, 0, w, h);

                    // Set an opaque color for the drawn characters.
                    gc.setFill(Color.web(color));
                    gc.setFont(new Font("monospace", fontSize - 5));

                    // Based on the stored y coordinate allows us to draw the character next (beneath the previous)
                    for (int i = 0; i < ypos.length; i++) {
                        // pick a random character (unicode)
                        //char ch = (char) random.ints(12353, 12380) // Japanese
                        //char ch = (char) random.ints(12100, 12200) // Chinese
                        char ch = (char) random.ints(932, 960) // Greek
                                .findFirst()
                                .getAsInt();
                        String text = Character.toString(ch);

                        // x coordinate to draw from left to right (each column).
                        double x = i * fontSize;

                        // y coordinate is based on the value previously stored.
                        int y = ypos[i];

                        // draw a character with an opaque color
                        gc.fillText(text, x, ypos[i]);

                        // The effect similar to dripping paint from the top (y = 0).
                        // If the current y is greater than the random length then reset the y position to zero.
                        if (y > 100 + Math.random() * 10000) {
                            // (restart the drip process from the top)
                            ypos[i] = 0;
                        } else {
                            // otherwise, in the next iteration draw the character beneath this character (continue to drip).
                            ypos[i] = y + fontSize;
                        }
                    }
                }
            }
        }.start();
    }

    /**
     * Fill the entire canvas area with the color black. Returns a resized array of y positions
     * that keeps track of each column's y coordinate position.
     *
     * @param gc
     * @param fontSize
     * @return
     */
    private int[] resize(GraphicsContext gc, int fontSize) {
        // clear by filling the background with black.
        Canvas canvas = gc.getCanvas();
        gc.setFill(Color.web("#000"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // resize ypos array based on the width of the canvas
        int cols = (int) Math.floor(canvas.getWidth() / fontSize) + 1;
        int[] ypos = new int[cols];
        return ypos;
    }

}
