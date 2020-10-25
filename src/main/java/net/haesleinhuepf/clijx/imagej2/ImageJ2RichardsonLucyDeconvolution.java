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
import org.scijava.plugin.Plugin;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_imageJ2RichardsonLucyDeconvolution")
public class ImageJ2RichardsonLucyDeconvolution extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized, HasClassifiedInputOutput
{

    @Override
    public String getParameterHelpText() {
        return "Image input, Image kernel_input, ByRef Image destination, Number num_iterations";
    }

    @Override
    public boolean executeCL() {
        boolean result = imageJ2RichardsonLucyDeconvolution(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]), (ClearCLBuffer) (args[2]), asInteger(args[3]));
        return result;
    }

    public static boolean imageJ2RichardsonLucyDeconvolution(CLIJ2 clij2, ClearCLBuffer input, ClearCLBuffer kernel_input, ClearCLBuffer output, Integer num_iterations) {
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

        ClearCLBuffer float_kernel_input = kernel_input;
        if (float_kernel_input.getNativeType() != NativeTypeEnum.Float) {
            float_kernel_input = clij2.create(input.getDimensions(), NativeTypeEnum.Float);
            clij2.copy(kernel_input, float_kernel_input);
        }

        // pull image from GPU in Imglib2 type
        RandomAccessibleInterval kenrelInputRAI = clij2.pullRAI(float_kernel_input);
        if (float_kernel_input != kernel_input) {
            float_kernel_input.close();
        }

        // process it using ImageJ Ops
        ImageJ2ServiceSingleton.getOpService().deconvolve().richardsonLucy(outputRAI, inputRAI, kenrelInputRAI, num_iterations);

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
        return "Apply ImageJ2 / ImageJ Ops Richardson Lucy Deconvolution to an image.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

    @Override
    public String getCategories() {
        return "Filter,Deconvolve";
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
