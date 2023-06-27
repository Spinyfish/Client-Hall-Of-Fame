package optifine;

import java.util.concurrent.*;
import net.minecraft.client.renderer.*;

public class CrashReportCpu implements Callable
{
    @Override
    public Object call() throws Exception {
        return OpenGlHelper.func_183029_j();
    }
}
