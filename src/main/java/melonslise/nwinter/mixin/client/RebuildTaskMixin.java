package melonslise.nwinter.mixin.client;

import melonslise.nwinter.client.extension.IExtendedCompiledChunk;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Set;

@Mixin(ChunkRenderDispatcher.RenderChunk.RebuildTask.class)
public class RebuildTaskMixin
{
	@Unique
	private BlockPos originTemp;

	@Unique
	private IExtendedCompiledChunk compiledChunkTemp;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;offset(III)Lnet/minecraft/core/BlockPos;"), method = "compile", locals = LocalCapture.CAPTURE_FAILHARD)
	private void injectCompileBeforeOffset(float cameraX, float cameraY, float cameraZ, ChunkRenderDispatcher.CompiledChunk compiledChunk, ChunkBufferBuilderPack bufferBuilderPack, CallbackInfoReturnable<Set<BlockEntity>> c, int i, BlockPos origin)
	{
		this.compiledChunkTemp = (IExtendedCompiledChunk) compiledChunk;
		this.compiledChunkTemp.getSkylightBuffer().clear();
		this.originTemp = origin;
	}

	/*
	@Redirect(method = "compile", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
	private BlockState redirectCompileGetBlockState(RenderChunkRegion region, BlockPos pos)
	{
		ByteBuffer buf = this.compiledChunkTemp.getSkylightBuffer();

		int xOff = pos.getX() - this.originTemp.getX();
		int yOffFull = pos.getY() - this.originTemp.getY();
		int zOff = pos.getZ() - this.originTemp.getZ();

		// Because we're packing 4 blocks vertically into 1 pixel
		int yOff = yOffFull / 4;
		int yOff2 = yOffFull % 4;

		buf.position(xOff + yOff * 16 + zOff * 4 * 16);

		int skylight = region.getBrightness(LightLayer.SKY, pos);

		if(skylight == 0)
		{
			return region.getBlockState(pos);
		}

		int value = yOff2 == 0 ? 0 : buf.get(buf.position()); // get without increment

		value |= skylight << yOff2;
		buf.put((byte) value);

		return region.getBlockState(pos);
	}
	 */
}