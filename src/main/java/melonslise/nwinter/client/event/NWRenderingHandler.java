package melonslise.nwinter.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Matrix4f;
import melonslise.nwinter.NuclearWinter;
import melonslise.nwinter.client.extension.IExtendedCompiledChunk;
import melonslise.nwinter.client.init.NWShaders;
import melonslise.nwinter.client.init.NWTextures;
import melonslise.nwinter.client.renderer.ByteVolume;
import melonslise.nwinter.client.renderer.shader.ExtendedPostChain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.nio.ByteBuffer;

@Mod.EventBusSubscriber(modid = NuclearWinter.ID, value = Dist.CLIENT)
public final class NWRenderingHandler
{
	private static final Matrix4f PROJECTION_INVERSE = new Matrix4f();
	private static final Matrix4f VIEW_INVERSE = new Matrix4f();

	private static void updateSkylightVolume()
	{
		var chunks = Minecraft.getInstance().levelRenderer.renderChunksInFrustum;

		if(chunks.size() <= 0)
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
			if(x > maxX)
			{
				maxX = x;
			}
			else if(x < minX)
			{
				minX = x;
			}
			if(y > maxY)
			{
				maxY = y;
			}
			else if(y < minY)
			{
				minY = y;
			}
			if(z > maxZ)
			{
				maxZ = z;
			}
			else if(z < minZ)
			{
				minZ = z;
			}
		}

		maxX += 16;
		maxY += 16;
		maxZ += 16;

		// divide by 4 because we shrink 8 bits of skylight (0-15) into just 2 bits (0-3) so we can fit in 4 blocks per single pixel
		ByteVolume volume = NWTextures.INSTANCE.skylightVolume;
		volume.set(ByteVolume.Format.RED, maxX - minX, (maxY - minY) / 4, maxZ - minZ, GL11.GL_NEAREST, GL12.GL_CLAMP_TO_EDGE);

		ByteBuffer buf = BufferUtils.createByteBuffer(volume.getWidth() * volume.getHeight() * volume.getDepth());
		volume.upload(0, 0, 0, volume.getWidth(), volume.getHeight(), volume.getDepth(), buf);

		/*
		for (LevelRenderer.RenderChunkInfo chunk : chunks)
		{
			ByteBuffer buf = ((IExtendedCompiledChunk) chunk.chunk.compiled.get()).getSkylightBuffer();

			pos = chunk.chunk.getOrigin();
			int xOff = pos.getX() - minX;
			int yOff = (pos.getY() - minY) / 4;
			int zOff = pos.getZ() - minZ;

			buf.rewind();

			volume.upload(xOff, yOff, zOff, 16, 4, 16, buf);
		}
		 */
	}

	/*
	@SubscribeEvent
	public static void clientTickEnd(TickEvent.ClientTickEvent e)
	{
		if(e.phase != TickEvent.Phase.END)
		{
			return;
		}

		updateSkylightVolume();
	}
	*/

	@SubscribeEvent
	public static void renderLevel(RenderLevelLastEvent e)
	{
		updateSkylightVolume();

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
			effect.setSampler("NoiseVolume", () -> NWTextures.INSTANCE.noiseVolume.getId());
			effect.setSampler("SkylightVolume", () -> NWTextures.INSTANCE.skylightVolume.getId());
		});

		postChain.process(e.getPartialTick());
	}
}