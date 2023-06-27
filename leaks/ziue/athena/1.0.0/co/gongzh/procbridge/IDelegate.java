package co.gongzh.procbridge;

import org.jetbrains.annotations.*;

@FunctionalInterface
public interface IDelegate
{
    @Nullable
    Object handleRequest(@Nullable final String p0, @Nullable final Object p1);
}
