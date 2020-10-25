package net.haesleinhuepf.clijx.imagej2;

import net.imglib2.algorithm.neighborhood.CenteredRectangleShape;
import net.imglib2.algorithm.neighborhood.HyperSphereShape;
import net.imglib2.algorithm.neighborhood.Shape;

import static net.haesleinhuepf.clij.macro.AbstractCLIJPlugin.asInteger;

public class ImageJ2Utilities {
    public static Shape asCube(int radius_x, int radius_y, int radius_z) {
        CenteredRectangleShape crs = new CenteredRectangleShape(new int[]{radius_x, radius_y, radius_z}, false);
        return crs;
    }

    public static Shape asRectangle(int radius_x, int radius_y) {
        CenteredRectangleShape crs = new CenteredRectangleShape(new int[]{radius_x, radius_y}, false);
        return crs;
    }

    public static Shape asSphere(long radius) {
        HyperSphereShape hss = new HyperSphereShape(radius);
        return hss;
    }
}
