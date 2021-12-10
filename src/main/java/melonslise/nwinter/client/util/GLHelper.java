package melonslise.nwinter.client.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class GLHelper
{
	private GLHelper() {}

	public static void bind3d(int id)
	{
		RenderSystem.assertOnRenderThreadOrInit();
		if (id != GlStateManager.TEXTURES[GlStateManager.activeTexture].binding)
		{
			GlStateManager.TEXTURES[GlStateManager.activeTexture].binding = id;
			GL11.glBindTexture(GL12.GL_TEXTURE_3D, id);
		}
	}
}