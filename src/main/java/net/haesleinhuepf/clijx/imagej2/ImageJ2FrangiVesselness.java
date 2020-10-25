package net.haesleinhuepf.clijx.imagej2;

import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.coremem.enums.NativeTypeEnum;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.HasClassifiedInputOutput;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import org.scijava.plugin.Plugin;

/**
 * The ImageJ2Tubeness
 *
 * Author: @haesleinhuepf
 * 6 2019
 */
@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_imageJ2FrangiVesselness")
public class ImageJ2FrangiVesselness extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized, HasClassifiedInputOutput {

    @Override
    public boolean executeCL() {
        return imageJ2FrangiVesselness(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]), asInteger(args[2]), asFloat(args[3]), asFloat(args[4]), asFloat(args[5]));
    }

    public boolean imageJ2FrangiVesselness(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer output, Integer scale, Float calibration_x, Float calibration_y, Float calibration_z) {

        // pull image from GPU in Imglib2 type
        ClearCLBuffer float_input = input;
        if (float_input.getNativeType() != NativeTypeEnum.Float) {
            float_input = clij2.create(input.getDimensions(), NativeTypeEnum.Float);
            clij2.copy(input, float_input);
        }

        // pull image from GPU in Imglib2 type
        RandomAccessibleInterval inputRAI = clij2.pullRAI(float_input);
        RandomAccessibleInterval outputRAI = clij2.pullRAI(float_input);
        if (float_input != input) {
            float_input.close();
        }

        if (input.getDimension() == 2 || input.getDepth() == 1) {
            ImageJ2ServiceSingleton.getOpService().filter().frangiVesselness(outputRAI, inputRAI, new double[]{calibration_x, calibration_y}, scale);
        } else {
            ImageJ2ServiceSingleton.getOpService().filter().frangiVesselness(outputRAI, inputRAI, new double[]{calibration_x, calibration_y, calibration_z}, scale);
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
    public ClearCLBuffer createOutputBufferFromSource(ClearCLBuffer input) {
        return getCLIJ2().create(input.getDimensions(), NativeTypeEnum.Float);
    }


    @Override
    public String getDescription() {
        return "Apply ImageJ2 / ImageJ Ops Frangi Vesselness filter to an image.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

    @Override
    public String getParameterHelpText() {
        return "Image source, ByRef Image destination, Number scale, Number calibrationX, Number calibrationY, Number calibrationZ";
    }

    @Override
    public String getCategories() {
        return "Filter,Measurement";
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
