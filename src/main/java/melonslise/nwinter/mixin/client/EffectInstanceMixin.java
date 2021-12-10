package melonslise.nwinter.mixin.client;

import java.util.function.IntSupplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.systems.RenderSystem;

import melonslise.nwinter.client.util.GLHelper;
import net.minecraft.client.renderer.EffectInstance;

@Mixin(EffectInstance.class)
public class EffectInstanceMixin
{
	@Inject(at = @At(value = "invoke", target = "Lcom/mojang/blaze3d/systems/RenderSystem;bindTexture(I)V"), method = "apply", locals = LocalCapture.CAPTURE_FAILHARD)
	private void injectApplyBeforeBindTexture(CallbackInfo c, int i, String s, IntSupplier texId, int j)
	{
		if(s.endsWith("Volume"))
		{
			GLHelper.bind3d(j);
		}
		else
		{
			RenderSystem.bindTexture(j);
		}
	}

	@Redirect(at = @At(value = "invoke", target = "Lcom/mojang/blaze3d/systems/RenderSystem;bindTexture(I)V"), method = "apply")
	private void redirectApplyBindTexture(int texId)
	{
		
	}
}