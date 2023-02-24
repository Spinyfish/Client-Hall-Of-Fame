package optifine;

import net.minecraft.client.renderer.OpenGlHelper;

import java.util.concurrent.Callable;

public class CrashReportCpu implements Callable
{
    public Object call() throws Exception
    {
        return OpenGlHelper.func_183029_j();
    }
}
