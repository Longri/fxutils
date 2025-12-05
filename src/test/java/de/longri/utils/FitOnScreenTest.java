package de.longri.utils;

import javafx.geometry.Rectangle2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FitOnScreenTest {

    // Screen definitions based on user request
    Rectangle2D screen1 = new Rectangle2D(0.0, 0.0, 1728.0, 1117.0);
    Rectangle2D screen2 = new Rectangle2D(-3440.0, -42.0, 3440.0, 1440.0); // Largest Area (approx 4.9M pixels)
    Rectangle2D screen3 = new Rectangle2D(-4880.0, -1659.0, 1440.0, 3440.0); // Also large, approx 4.9M pixels, but let's say Screen 2 is slightly main logic target if areas equal or just purely strictly larger check.
    // Area S1: ~1.9M
    // Area S2: 4,953,600
    // Area S3: 4,953,600
    // Note: S2 and S3 have identical area size. Usually the first one found or based on sort stability wins if logic is just >.

    Rectangle2D[] screens = new Rectangle2D[]{screen1, screen2, screen3};

    @Test
    void testFitsCompletely() {
        // A rectangle completely inside Screen 1
        double x = 100;
        double y = 100;
        double w = 500;
        double h = 400;

        Rectangle2D result = FitOnScreen.fitOnScreen(true, x, y, w, h, screens);

        assertEquals(new Rectangle2D(x, y, w, h), result, "Should return original rect as it fits in Screen 1");
    }

    @Test
    void testFitsCompletelyInsideScreen2() {
        // A rectangle completely inside Screen 2
        double x = -3000;
        double y = 100;
        double w = 200;
        double h = 200;

        Rectangle2D result = FitOnScreen.fitOnScreen(false, x, y, w, h, screens);

        assertEquals(new Rectangle2D(x, y, w, h), result, "Should return original rect as it fits in Screen 2");
    }

    @Test
    void testOutsideResizable() {
        // Rectangle completely off-screen (e.g. way positive)
        // Should return the full screen of the largest screen (Screen 2 or 3).
        // Assuming the logic picks the first max found or robust max.
        // Let's test with Screen 2 being clearly largest just to be safe, or handle the equality.
        // In our data S2 and S3 are equal area. Let's assume implementation picks one.

        double x = 10000;
        double y = 10000;
        double w = 500;
        double h = 500;

        Rectangle2D result = FitOnScreen.fitOnScreen(true, x, y, w, h, screens);

        // Expecting full screen of one of the largest screens.
        // Since stream().max often returns the first one if equal (implementation detail of Spliterator), or stable sort logic.
        // Let's accept either S2 or S3.
        boolean isScreen2 = result.equals(screen2);
        boolean isScreen3 = result.equals(screen3);

        assert(isScreen2 || isScreen3) : "Should be full screen of one of the largest screens";
    }

    @Test
    void testOutsideNotResizableCentersOnLargest() {
        // Rectangle completely off-screen
        // Should center on largest screen (S2 or S3)
        double x = 10000;
        double y = 10000;
        double w = 200;
        double h = 100;

        Rectangle2D result = FitOnScreen.fitOnScreen(false, x, y, w, h, screens);

        // Center of Screen 2:
        // MinX=-3440, W=3440 -> CenterX = -1720
        // MinY=-42, H=1440 -> CenterY = -42 + 720 = 678
        // Expected Rect X = -1720 - 100 = -1820
        // Expected Rect Y = 678 - 50 = 628

        Rectangle2D expectedOnS2 = new Rectangle2D(-1820, 628, 200, 100);

        // Center of Screen 3:
        // MinX=-4880, W=1440 -> CenterX = -4880 + 720 = -4160
        // MinY=-1659, H=3440 -> CenterY = -1659 + 1720 = 61
        // Expected Rect X = -4160 - 100 = -4260
        // Expected Rect Y = 61 - 50 = 11
        Rectangle2D expectedOnS3 = new Rectangle2D(-4260, 11, 200, 100);

        boolean matchS2 = result.getMinX() == expectedOnS2.getMinX() && result.getMinY() == expectedOnS2.getMinY();
        boolean matchS3 = result.getMinX() == expectedOnS3.getMinX() && result.getMinY() == expectedOnS3.getMinY();

        assert(matchS2 || matchS3) : "Should be centered on one of the largest screens";
        assertEquals(w, result.getWidth());
        assertEquals(h, result.getHeight());
    }

    @Test
    void testPartiallyOnScreenNotResizable() {
        // Rectangle partially on Screen 1, but mostly off.
        // But wait, logic says: "Screen mit dem meisten Anteil".
        // Let's put a rect that is 80% on Screen 1 and 20% outside.
        // It doesn't fit completely, so logic kicks in.
        // !resizable -> center on screen with most overlap (Screen 1).

        double w = 1000;
        double h = 1000;
        // Screen 1 is 0,0 to 1728,1117
        // Place rect at 1500, 500.
        // X overlap: 1500 to 1728 = 228 width.
        // Y overlap: 500 to 1117 = full height of rect (it's 1000) -> 617 overlap actually? No maxY is 1500.
        // Let's make it simpler.

        // Screen 1 center is approx 864, 558.
        double x = 1000; // Overlap width = 1728 - 1000 = 728.
        double y = 100;  // Overlap height = 1000 (full).
        // Area overlap on S1 = 728 * 1000 = 728,000.

        // Overlap on S2 (MinX -3440 to 0). Rect starts at 1000. No overlap.
        // Overlap on S3. No overlap.

        Rectangle2D result = FitOnScreen.fitOnScreen(false, x, y, w, h, screens);

        // Should be centered on Screen 1.
        double expectedCenterX = 0 + 1728.0 / 2; // 864
        double expectedCenterY = 0 + 1117.0 / 2; // 558.5

        double expectedX = expectedCenterX - (w/2); // 864 - 500 = 364
        double expectedY = expectedCenterY - (h/2); // 558.5 - 500 = 58.5

        assertEquals(new Rectangle2D(expectedX, expectedY, w, h), result, "Should be centered on Screen 1 because it had the most overlap");
    }



}