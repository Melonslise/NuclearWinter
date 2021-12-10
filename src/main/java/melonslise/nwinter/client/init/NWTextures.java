package melonslise.nwinter.client.init;

import melonslise.nwinter.NuclearWinter;
import melonslise.nwinter.client.renderer.ByteVolume;
import melonslise.nwinter.client.util.ReloadableRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class NWTextures extends ReloadableRegistry<AutoCloseable>
{
	public static final NWTextures INSTANCE = new NWTextures(2);

	public ByteVolume noiseVolume, skylightVolume;

	private NWTextures(int expectedSize)
	{
		super(expectedSize);
	}

	@Override
	protected void load(ResourceManager resourceManager) throws IOException
	{
		this.elements.add(noiseVolume = ByteVolume.read(resourceManager, new ResourceLocation(NuclearWinter.ID, "textures/effect/noise.vol"), GL11.GL_LINEAR, GL14.GL_MIRRORED_REPEAT));
		this.elements.add(skylightVolume = new ByteVolume());
	}
}