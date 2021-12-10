package melonslise.nwinter.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import melonslise.nwinter.client.init.NWShaders;
import net.minecraft.client.renderer.LevelRenderer;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin
{
	@Inject(at = @At("TAIL"), method = "resize")
	private void injectResizeTail(int width, int height, CallbackInfo c)
	{
		NWShaders.INSTANCE.resize(width, height);
	}

	/*
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderDebug(Lnet/minecraft/client/Camera;)V"), method = "renderLevel")
	private void injectRenderLevelPreRenderDebug(PoseStack mtx, float frameTime, long nanoTime, boolean renderOutline, Camera camera, GameRenderer gameRenderer, LightTexture light, Matrix4f projMat, CallbackInfo c)
	{

	}
	*/
}