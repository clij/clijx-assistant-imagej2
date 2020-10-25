package net.haesleinhuepf.clijx.imagej2;

import ij.IJ;
import net.imagej.ops.OpService;
import org.scijava.Context;
import org.scijava.command.CommandService;

public class ImageJ2ServiceSingleton {
    private static OpService opService;
    private static Context ctx;
    static {
        // this initializes the SciJava platform.
        // See https://forum.image.sc/t/compatibility-of-imagej-tensorflow-with-imagej1/41295/2
        ctx = (Context) IJ.runPlugIn("org.scijava.Context", "");
        if (ctx == null) ctx = new Context(CommandService.class, OpService.class);
        opService = ctx.getService(OpService.class);
    }

    public static OpService getOpService() {
        return opService;
    }

    public static Context getContext() {
        return ctx;
    }
}
