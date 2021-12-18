package melonslise.nwinter.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Matrix4f;
import melonslise.nwinter.NuclearWinter;
import melonslise.nwinter.client.init.NWShaders;
import melonslise.nwinter.client.init.NWTextures;
import melonslise.nwinter.client.renderer.shader.ExtendedPostChain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL12;
import org.lwjgl.system.MemoryUtil;

@Mod.EventBusSubscriber(modid = NuclearWinter.ID, value = Dist.CLIENT)
public final class NWRenderingHandler
{
	private static final Matrix4f PROJECTION_INVERSE = new Matrix4f();
	private static final Matrix4f VIEW_INVERSE = new Matrix4f();

	private static int cornerX, cornerZ;

	/*
	private static void updateSkylightVolume()
	{
		Minecraft mc = Minecraft.getInstance();

		if (mc.isPaused())
		{
			return;
		}

		var chunks = mc.levelRenderer.renderChunksInFrustum;

		if (chunks.size() <= 0)
		{
			return;
		}

		BlockPos pos = chunks.get(0).chunk.getOrigin();

		int minX = pos.getX();
		int minY = pos.getY();
		int minZ = pos.getZ();
		int maxX = minX;
		int maxY = minY;
		int maxZ = minZ;

		for (LevelRenderer.RenderChunkInfo chunk : chunks)
		{
			pos = chunk.chunk.getOrigin();
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			if (x > maxX)
			{
				maxX = x;
			} else if (x < minX)
			{
				minX = x;
			}
			if (y > maxY)
			{
				maxY = y;
			} else if (y < minY)
			{
				minY = y;
			}
			if (z > maxZ)
			{
				maxZ = z;
			} else if (z < minZ)
			{
				minZ = z;
			}
		}

		maxX += 16;
		maxY += 16;
		maxZ += 16;

		int deltaX = maxX - minX;
		int deltaY = (maxY - minY) / 4;
		int deltaZ = maxZ - minZ;

		// divide by 4 because we shrink 8 bits of skylight (0-15) into just 2 bits (0-3) so we can fit in 4 blocks per single pixel
		ByteVolume volume = NWTextures.INSTANCE.skylightVolume;
		volume.set(ByteVolume.Format.RED, deltaX, deltaY, deltaZ, GL11.GL_NEAREST, GL12.GL_CLAMP_TO_EDGE);

		// long ptr = MemoryUtil.nmemAlloc(deltaX * deltaY * deltaZ);
		ByteBuffer buf = BufferUtils.createByteBuffer(deltaX * deltaY * deltaZ);

		for (LevelRenderer.RenderChunkInfo chunk : chunks)
		{
			long skyPtr = ((IExtendedCompiledChunk) chunk.chunk.compiled.get()).getSkylightBuffer();

			pos = chunk.chunk.getOrigin();
			int xOff = pos.getX() - minX;
			int yOff = (pos.getY() - minY) / 4;
			int zOff = pos.getZ() - minZ;

			int ptrOff = xOff + yOff * deltaX + zOff * deltaX * deltaY;

			for(int z = 0; z < 16; ++z)
			{
				for (int y = 0; y < 4; ++y)
				{
					int skyPtrOff = y * 16 + z * 16 * 4;

					// MemoryUtil.memCopy(skyPtr + skyPtrOff, ptr + ptrOff, 16);
				}
			}
		}

		volume.upload(0, 0, 0, volume.getWidth(), volume.getHeight(), volume.getDepth(), buf);
		// MemoryUtil.nmemFree(ptr);

		for (LevelRenderer.RenderChunkInfo chunk : chunks)
		{
			pos = chunk.chunk.getOrigin();
			int xOff = pos.getX() - minX;
			int yOff = (pos.getY() - minY) / 4;
			int zOff = pos.getZ() - minZ;

			volume.upload(xOff, yOff, zOff, 16, 4, 16, ((IExtendedCompiledChunk) chunk.chunk.compiled.get()).getSkylightBuffer());
		}
	}
	 */

	public static void updateHeightmapTexture()
	{
		Minecraft mc = Minecraft.getInstance();

		if (mc.isPaused())
		{
			return;
		}

		var chunks = mc.levelRenderer.renderChunksInFrustum;

		if (chunks.size() <= 0)
		{
			return;
		}

		BlockPos pos = chunks.get(0).chunk.getOrigin();

		int minX = pos.getX();
		int minZ = pos.getZ();
		int maxX = minX;
		int maxZ = minZ;

		for (LevelRenderer.RenderChunkInfo chunk : chunks)
		{
			pos = chunk.chunk.getOrigin();
			int x = pos.getX();
			int z = pos.getZ();
			if (x > maxX)
			{
				maxX = x;
			}
			else if (x < minX)
			{
				minX = x;
			}
			if (z > maxZ)
			{
				maxZ = z;
			}
			else if (z < minZ)
			{
				minZ = z;
			}
		}

		maxX += 16;
		maxZ += 16;

		int deltaX = maxX - minX;
		int deltaZ = maxZ - minZ;

		long ptr = MemoryUtil.nmemAlloc(deltaX * deltaZ * 2); // 2 bytes per short

		for(int z = 0; z < deltaZ; ++z)
		{
			for(int x = 0; x < deltaX; ++x)
			{
				// ptr offset * 2 because short = 2 bytes
				MemoryUtil.memPutShort(ptr + (x + z * deltaX) * 2, (short) mc.level.getHeight(Heightmap.Types.MOTION_BLOCKING, minX + x, minZ + z));
			}
		}

		NWTextures.INSTANCE.heightmapTexture.set(deltaX, deltaZ, GL12.GL_CLAMP_TO_EDGE);
		NWTextures.INSTANCE.heightmapTexture.upload(0, 0, deltaX, deltaZ, ptr);

		MemoryUtil.nmemFree(ptr);

		cornerX = minX;
		cornerZ = minZ;
	}

	@SubscribeEvent
	public static void clientTickEnd(TickEvent.ClientTickEvent e)
	{
		if(e.phase != TickEvent.Phase.END)
		{
			return;
		}

		updateHeightmapTexture();
	}

	@SubscribeEvent
	public static void renderLevel(RenderLevelLastEvent e)
	{
		PROJECTION_INVERSE.load(RenderSystem.getProjectionMatrix()); // FIXME e.getProjectionMatrix???
		PROJECTION_INVERSE.invert();

		VIEW_INVERSE.load(e.getPoseStack().last().pose());
		VIEW_INVERSE.invert();

		Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

		ExtendedPostChain postChain = NWShaders.INSTANCE.fog;

		if(postChain == null)
		{
			return;
		}

		postChain.upload(effect ->
		{
			effect.safeGetUniform("ProjInverseMat").set(PROJECTION_INVERSE);
			effect.safeGetUniform("ViewInverseMat").set(VIEW_INVERSE);
			effect.safeGetUniform("CameraPosition").set((float) camPos.x, (float) camPos.y, (float) camPos.z);
			effect.safeGetUniform("GameTime").set(RenderSystem.getShaderGameTime());
			effect.safeGetUniform("HeightmapCorner").set(cornerX, cornerZ);
			effect.setSampler("NoiseVolume", () -> NWTextures.INSTANCE.noiseVolume.getId());
			effect.setSampler("HeightmapTexture", () -> NWTextures.INSTANCE.heightmapTexture.getId());
		});

		postChain.process(e.getPartialTick());
	}
}