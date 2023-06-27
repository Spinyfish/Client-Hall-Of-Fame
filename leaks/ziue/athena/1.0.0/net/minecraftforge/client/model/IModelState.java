package net.minecraftforge.client.model;

import com.google.common.base.*;

public interface IModelState
{
    Optional<TRSRTransformation> apply(final Optional<? extends IModelPart> p0);
}
