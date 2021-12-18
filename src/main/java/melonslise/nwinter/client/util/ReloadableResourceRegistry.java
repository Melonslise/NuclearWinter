package melonslise.nwinter.client.util;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class ReloadableResourceRegistry<T extends AutoCloseable> implements ResourceManagerReloadListener
{
	protected final List<T> elements;

	protected ReloadableResourceRegistry(int expectedSize)
	{
		this.elements = new ArrayList<>(expectedSize);
	}

	protected abstract void load(ResourceManager resourceManager) throws Exception;

	protected void clear() throws Exception
	{
		for (T element : this.elements)
		{
			element.close();
		}
		this.elements.clear();
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager)
	{
		try
		{
			this.clear();
			this.load(resourceManager);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}