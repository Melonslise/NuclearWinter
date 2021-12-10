package melonslise.nwinter.client.event;

import com.mojang.blaze3d.systems.RenderSystem;

import melonslise.nwinter.NuclearWinter;
import melonslise.nwinter.client.init.NWShaders;
import melonslise.nwinter.client.init.NWTextures;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = NuclearWinter.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class NWClientRegistryHandler
{
	private NWClientRegistryHandler() {}

	@SubscribeEvent
	public static void registerReloadListeners(RegisterClientReloadListenersEvent e)
	{
		e.registerReloadListener(NWTextures.INSTANCE);
		e.registerReloadListener(NWShaders.INSTANCE);
	}
}