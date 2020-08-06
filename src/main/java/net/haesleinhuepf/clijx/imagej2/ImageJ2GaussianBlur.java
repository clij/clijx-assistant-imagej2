package net.haesleinhuepf.clijx.imagej2;


import ij.IJ;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.HasAuthor;
import net.haesleinhuepf.clij2.utilities.HasLicense;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.labeling.ConnectedComponents;
import net.imglib2.roi.labeling.ImgLabeling;
import org.scijava.Context;
import org.scijava.command.CommandService;
import org.scijava.plugin.Plugin;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_imageJ2GaussianBlur")
public class ImageJ2GaussianBlur extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized, HasAuthor, HasLicense
{
    private static OpService opService;
    private static Context ctx;
    static {
        // this initializes the SciJava platform.
        // See https://forum.image.sc/t/compatibility-of-imagej-tensorflow-with-imagej1/41295/2
        ctx = (Context) IJ.runPlugIn("org.scijava.Context", "");
        if (ctx == null) ctx = new Context(CommandService.class, OpService.class);
        opService = ctx.getService(OpService.class);
    }

    public ImageJ2GaussianBlur() {
        super();
    }

    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination, Number sigma_x, Number sigma_y, Number sigma_z";
    }

    @Override
    public boolean executeCL() {
        boolean result = imageJOpsConnectedComponentsAnalysis(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]), asFloat(args[2]), asFloat(args[3]), asFloat(args[4]));
        return result;
    }

    public static boolean imageJOpsConnectedComponentsAnalysis(CLIJ2 clij2, ClearCLBuffer input1, ClearCLBuffer output, Float sigma_x, Float sigma_y, Float sigma_z) {
        // pull image from GPU in Imglib2 type
        RandomAccessibleInterval inputRAI = clij2.pullRAI(input1);

        // process it using ImageJ Ops
        RandomAccessibleInterval outputRAI = opService.filter().gauss(inputRAI, sigma_x, sigma_y, sigma_z);

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
        return "Apply ImageJ2 / ImageJ Ops Gaussian Blur to an image.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

    @Override
    public String getCategories() {
        return "Filter";
    }

    @Override
    public String getAuthorName() {
        return "Put your name here.";
    }

    @Override
    public String getLicense() {
        return "Public domain";
    }
}
