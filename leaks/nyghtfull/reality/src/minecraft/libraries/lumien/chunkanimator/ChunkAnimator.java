package libraries.lumien.chunkanimator;

import libraries.lumien.chunkanimator.handler.AnimationHandler;

public class ChunkAnimator{

	public static ChunkAnimator INSTANCE = new ChunkAnimator();

	public AnimationHandler animationHandler;
	public int mode = 1;

	public ChunkAnimator()
	{
		animationHandler = new AnimationHandler();
	}
}
