package de.longri.utils;

import de.longri.gdx_utils.Array;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import java.util.Arrays;
import java.util.Comparator;

public class FitOnScreen {

    /**
     * Returns a rectangle that fits on screen
     * @param screenRects
     * @return
     */
    public static Rectangle2D fitOnScreen(boolean resizable, double x, double y, double width, double height, Rectangle2D... screenRects) {
        Rectangle2D candidate = new Rectangle2D(x, y, width, height);

        // 1. Check if it fits completely in any screen
        for (Rectangle2D screen : screenRects) {
            if (screen.contains(candidate)) {
                return candidate;
            }
        }

        // It does not fit completely.
        // Logic depends on resizable flag.

        if (resizable) {
            // 2. Resizable: Return full screen of the screen with the largest area
            return Arrays.stream(screenRects)
                    .max(Comparator.comparingDouble(s -> s.getWidth() * s.getHeight()))
                    .orElse(candidate); // Fallback if no screens provided
        } else {
            // 3. Not resizable: Center on the screen with the most overlap (intersection area)
            // If completely outside, we pick the screen with the largest area as a fallback to center on.

            Rectangle2D bestScreen = Arrays.stream(screenRects)
                    .max(Comparator.comparingDouble(s -> {
                        Rectangle2D intersection = intersect(s, candidate);
                        return intersection.getWidth() * intersection.getHeight();
                    }))
                    .orElse(null);

            // If we have absolutely no overlap with any screen (bestScreen area was -1 or all were equal),
            // fallback to the largest screen to show the window.
            if (bestScreen == null || intersect(bestScreen, candidate).getWidth() == 0) {
                bestScreen = Arrays.stream(screenRects)
                        .max(Comparator.comparingDouble(s -> s.getWidth() * s.getHeight()))
                        .orElse(screenRects[0]);
            }

            // Center the candidate on the best screen
            double centerX = bestScreen.getMinX() + (bestScreen.getWidth() / 2.0);
            double centerY = bestScreen.getMinY() + (bestScreen.getHeight() / 2.0);

            double newX = centerX - (width / 2.0);
            double newY = centerY - (height / 2.0);

            return new Rectangle2D(newX, newY, width, height);
        }
    }

    /**
     * Helper to calculate intersection of two rectangles manually since JavaFX Rectangle2D
     * does not handle negative dimensions resulting from non-intersection gracefully in all versions
     * or we just want strict control.
     */
    private static Rectangle2D intersect(Rectangle2D r1, Rectangle2D r2) {
        double x1 = Math.max(r1.getMinX(), r2.getMinX());
        double y1 = Math.max(r1.getMinY(), r2.getMinY());
        double x2 = Math.min(r1.getMaxX(), r2.getMaxX());
        double y2 = Math.min(r1.getMaxY(), r2.getMaxY());

        if (x2 < x1 || y2 < y1) {
            // No intersection
            return new Rectangle2D(0, 0, 0, 0);
        }
        return new Rectangle2D(x1, y1, x2 - x1, y2 - y1);
    }

    public static Rectangle2D fitOnScreen(boolean isResizeable, double x, double y, double w, double h, ObservableList<Screen> screens) {

        Array<Rectangle2D> screensArray = new Array<>();

        for (Screen screen : screens) {
            screensArray.add(screen.getBounds());
        }

        return fitOnScreen(isResizeable, x, y, w, h, screensArray.toArray(Rectangle2D.class));
    }
}
