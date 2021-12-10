package melonslise.nwinter.mixin.client;

import melonslise.nwinter.client.extension.IExtendedCompiledChunk;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.lwjgl.BufferUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.nio.ByteBuffer;

@Mixin(ChunkRenderDispatcher.CompiledChunk.class)
public class CompiledChunkMixin implements IExtendedCompiledChunk
{
	@Unique
	private final ByteBuffer skylightBuffer = BufferUtils.createByteBuffer(16 * 4 * 16);

	@Override
	public ByteBuffer getSkylightBuffer()
	{
		return this.skylightBuffer;
	}
}