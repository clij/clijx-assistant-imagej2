package net.haesleinhuepf.clijx.imagej2;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.HasClassifiedInputOutput;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.view.Views;
import org.scijava.plugin.Plugin;

/**
 * The ImageJ2Tubeness
 *
 * Author: @haesleinhuepf
 * 6 2019
 */
@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_imageJ2MedianBox")
public class ImageJ2MedianBox extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized, HasClassifiedInputOutput {

    @Override
    public boolean executeCL() {
        return imageJ2MedianBox(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]), asInteger(args[2]) , asInteger(args[3]), asInteger(args[4]));
    }

    public boolean imageJ2MedianBox(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer output, Integer radius_x, Integer radius_y, Integer radius_z) {

        // pull image from GPU in Imglib2 type
        RandomAccessibleInterval inputRAI = clij2.pullRAI(input);
        RandomAccessibleInterval outputRAI = clij2.pullRAI(input);


        if (input.getDimension() == 2 || input.getDepth() == 1) {
            ImageJ2ServiceSingleton.getOpService().filter().median(Views.iterable(outputRAI), inputRAI, ImageJ2Utilities.asRectangle(radius_x, radius_y));
        } else {
            ImageJ2ServiceSingleton.getOpService().filter().median(Views.iterable(outputRAI), inputRAI, ImageJ2Utilities.asCube(radius_x, radius_y, radius_z));
        }
        // push result back
        ClearCLBuffer result = clij2.push(outputRAI);

        // save it in the right place
        clij2.copy(result, output);

        // clean up
        result.close();

        return true;
    }

    @Override
    public String getDescription() {
        return "Apply ImageJ2 / ImageJ Ops Median filter with a Sphere shape to an image.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

    @Override
    public String getParameterHelpText() {
        return "Image source, ByRef Image destination, Number radius_x, Number radius_y, Number radius_z";
    }

    @Override
    public String getCategories() {
        return "Filter,Denoise";
    }

    @Override
    public String getInputType() {
        return "Image";
    }

    @Override
    public String getOutputType() {
        return "Image";
    }
}
