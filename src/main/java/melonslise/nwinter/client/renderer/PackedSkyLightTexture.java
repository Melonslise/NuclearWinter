package melonslise.nwinter.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PackedSkyLightTexture implements AutoCloseable
{
	private final DynamicTexture texture;
	private final ResourceLocation location;

	private int lastDistance;
	private final BlockPos.MutableBlockPos lastPos = new BlockPos.MutableBlockPos();

	public PackedSkyLightTexture()
	{
		this.texture = new DynamicTexture(1, 1, false);
		this.location = Minecraft.getInstance().getTextureManager().register("packed_skylight_map", this.texture);
	}

	public void tick()
	{
		final Minecraft mc = Minecraft.getInstance();

		final int distance = mc.options.getEffectiveRenderDistance();
		if(distance != this.lastDistance)
		{
			this.lastDistance = distance;
			this.texture.setPixels(new NativeImage(distance * 32 + 1, distance * 32 + 1, false));
		}

		final BlockPos pos = mc.player.blockPosition();
		if(pos.equals(this.lastPos))
		{
			return;
		}

		this.lastPos.set(pos);

		this.texture.getPixels().getPixelRGBA(distance, distance);
	}

	@Override
	public void close() throws Exception
	{
		// TODO Auto-generated method stub
		
	}
}