package melonslise.nwinter.client.renderer.shader;

import java.io.IOException;
import java.util.function.Consumer;

import com.google.gson.JsonSyntaxException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ExtendedPostChain extends PostChain
{
	public ExtendedPostChain(String domain, String name) throws IOException, JsonSyntaxException
	{
		super(Minecraft.getInstance().getTextureManager(), Minecraft.getInstance().getResourceManager(), Minecraft.getInstance().getMainRenderTarget(), new ResourceLocation(domain, "shaders/post/" + name + ".json"));
		this.resize(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
	}

	public void upload(Consumer<EffectInstance> action)
	{
		for(PostPass pass : this.passes)
		{
			action.accept(pass.getEffect());
		}
	}

	@Override
	public void process(float frameTime)
	{
		super.process(frameTime);
		Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
	}
}