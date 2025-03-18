package net.weever.rotp_harvest.client.render.layer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;
import net.weever.rotp_harvest.client.render.HarvestMainModel;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;

public class HarvestHeldItemLayer<M extends HarvestMainModel> extends HeldItemLayer<HarvestMainEntity, M> {

    public HarvestHeldItemLayer(IEntityRenderer<HarvestMainEntity, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, HarvestMainEntity entityIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack leftHandItem = entityIn.getItemInHand(Hand.MAIN_HAND);
        ItemStack rightHandItem = entityIn.getItemInHand(Hand.OFF_HAND);

        if (!rightHandItem.isEmpty() || !leftHandItem.isEmpty()) {
            matrixStackIn.pushPose();

            if (!rightHandItem.isEmpty()) {
                translateToHand(entityIn, HandSide.RIGHT, matrixStackIn);

                matrixStackIn.translate(0.0, 0.125, -0.5);
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                matrixStackIn.scale(0.625F, 0.625F, 0.625F);

                Minecraft.getInstance().getItemRenderer().renderStatic(
                        rightHandItem,
                        ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                        packedLightIn,
                        OverlayTexture.NO_OVERLAY,
                        matrixStackIn,
                        bufferIn
                );
            }

            if (!leftHandItem.isEmpty()) {
                matrixStackIn.pushPose();
                translateToHand(entityIn, HandSide.LEFT, matrixStackIn);

                matrixStackIn.translate(0.0, 0.125, -0.5); 
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                matrixStackIn.scale(0.625F, 0.625F, 0.625F);

                Minecraft.getInstance().getItemRenderer().renderStatic(
                        leftHandItem,
                        ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
                        packedLightIn,
                        OverlayTexture.NO_OVERLAY,
                        matrixStackIn,
                        bufferIn
                );
                matrixStackIn.popPose();
            }

            matrixStackIn.popPose();
        }
    }

    protected void translateToHand(HarvestMainEntity entity, HandSide hand, MatrixStack matrixStack) {
        ((HarvestMainModel) this.getParentModel()).translateToHand(hand, matrixStack);
    }
}