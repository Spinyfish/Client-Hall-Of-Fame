// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.text.jdk8;

import org.jetbrains.annotations.Nullable;
import kotlin.SinceKotlin;
import kotlin.text.MatchNamedGroupCollection;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.MatchGroup;
import org.jetbrains.annotations.NotNull;
import kotlin.text.MatchGroupCollection;
import kotlin.jvm.JvmName;
import kotlin.Metadata;

@Metadata(mv = { 1, 7, 1 }, k = 2, xi = 48, d1 = { "\u0000\u0012\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u001a\u0017\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0087\u0002¨\u0006\u0005" }, d2 = { "get", "Lkotlin/text/MatchGroup;", "Lkotlin/text/MatchGroupCollection;", "name", "", "kotlin-stdlib-jdk8" }, pn = "kotlin.text")
@JvmName(name = "RegexExtensionsJDK8Kt")
public final class RegexExtensionsJDK8Kt
{
    @SinceKotlin(version = "1.2")
    @Nullable
    public static final MatchGroup get(@NotNull final MatchGroupCollection $this$get, @NotNull final String name) {
        Intrinsics.checkNotNullParameter($this$get, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        final MatchNamedGroupCollection collection = ($this$get instanceof MatchNamedGroupCollection) ? ((MatchNamedGroupCollection)$this$get) : null;
        if (collection == null) {
            throw new UnsupportedOperationException("Retrieving groups by name is not supported on this platform.");
        }
        final MatchNamedGroupCollection namedGroups = collection;
        return namedGroups.get(name);
    }
}
