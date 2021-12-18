package melonslise.nwinter.client.init;

import java.io.IOException;

import com.google.gson.JsonSyntaxException;

import melonslise.nwinter.NuclearWinter;
import melonslise.nwinter.client.renderer.shader.ExtendedPostChain;
import melonslise.nwinter.client.util.ReloadableResourceRegistry;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class NWShaders extends ReloadableResourceRegistry<PostChain>
{
	public static final NWShaders INSTANCE = new NWShaders(2);

	public ExtendedPostChain fog;

	private NWShaders(int expectedSize)
	{
		super(expectedSize);
	}

	public void load(ResourceManager resourceManager) throws JsonSyntaxException, IOException
	{
		this.elements.add(this.fog = new ExtendedPostChain(NuclearWinter.ID, "fog"));
	}

	public void resize(int width, int height)
	{
		this.elements.forEach(p -> p.resize(width, height));
	}
}