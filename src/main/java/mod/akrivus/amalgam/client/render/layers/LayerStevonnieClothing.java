package mod.akrivus.amalgam.client.render.layers;

import mod.akrivus.amalgam.gem.EntityStevonnie;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

public class LayerStevonnieClothing implements LayerRenderer<EntityStevonnie> {
	private final RenderLivingBase<?> gemRenderer;
	private final ModelBase gemModel;
	
	public LayerStevonnieClothing(RenderLivingBase<?> gemRenderer) {
		this.gemRenderer = gemRenderer;
		this.gemModel = gemRenderer.getMainModel();
	}
	
	@Override
	public void doRenderLayer(EntityStevonnie gem, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (!gem.isInvisible()) {
			GlStateManager.color(1.0F, 1.0F, 1.0F);
			this.gemRenderer.bindTexture(this.getTexture(gem));
			this.gemModel.setModelAttributes(this.gemRenderer.getMainModel());
	        this.gemModel.render(gem, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			GlStateManager.disableBlend();
		}
	}
	
	public ResourceLocation getTexture(EntityStevonnie gem) {
		if (gem.isWearingCoat()) {
			return new ResourceLocation("amalgam:textures/entities/stevonnie/coat.png");
		}
		return new ResourceLocation("amalgam:textures/entities/stevonnie/clothes_" + gem.getClothing() + ".png");
	}
	
	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}