package melonslise.nwinter.mixin.client;

import melonslise.nwinter.client.extension.IExtendedCompiledChunk;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ChunkRenderDispatcher.CompiledChunk.class)
public class CompiledChunkMixin implements IExtendedCompiledChunk
{
	@Unique
	private long skylightBufferPtr = MemoryUtil.nmemAlloc(16 * 4 * 16);

	@Override
	public long getSkylightBuffer()
	{
		return this.skylightBufferPtr;
	}

	@Override
	public void freeSkylightBuffer()
	{
		MemoryUtil.nmemFree(this.skylightBufferPtr);
		this.skylightBufferPtr = 0L;
	}
}