package dev.eternal.client.render.engine.dispatch;

import dev.eternal.client.render.engine.renderable.Renderable;
import lombok.Builder;

@Builder
public record DispatcherMetaData(
    boolean attributes,
    DispatcherMode renderMode,
    Class<? extends Renderable> allowed
) {

}
