package net.weever.rotp_harvest.client.render;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.github.standobyte.jojo.client.ClientUtil;
import com.github.standobyte.jojo.client.standskin.StandSkin;
import com.github.standobyte.jojo.client.standskin.StandSkinsManager;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasArm;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.HandSide;
import net.weever.rotp_harvest.entity.stand.harvest.HarvestMainEntity;

public class HarvestMainModel extends EntityModel<HarvestMainEntity> implements IHasArm {
    private final ModelRenderer Torso;
    private final ModelRenderer rightArm;
    private final ModelRenderer rightArm2;
    private final ModelRenderer leftArm;
    private final ModelRenderer leftArm2;
    private final ModelRenderer RightLeg;
    private final ModelRenderer LeftLeg;
    private final ModelRenderer tail;

	private final ModelRenderer rabbit;
	private final ModelRenderer head;
	private final ModelRenderer ear_r1;
	private final ModelRenderer ear_r2;
	private final ModelRenderer body;
	private final ModelRenderer upperPart;
	private final ModelRenderer torso;
	private final ModelRenderer leftArmRabbit;
	private final ModelRenderer leftArmJoint;
	private final ModelRenderer leftForeArm;
	private final ModelRenderer rightArmRabbit;
	private final ModelRenderer rightArmJoint;
	private final ModelRenderer rightForeArm;
	private final ModelRenderer leftLeg;
	private final ModelRenderer leftLegJoint;
	private final ModelRenderer leftLowerLeg;
	private final ModelRenderer rightLeg;
	private final ModelRenderer rightLegJoint;
	private final ModelRenderer rightLowerLeg;

	private final ModelRenderer MainSkin;
	private final ModelRenderer skinRightArm;
	private final ModelRenderer skinRightArm2;
	private final ModelRenderer skinLeftArm;
	private final ModelRenderer skinLeftArm2;
	private final ModelRenderer skinRightLeg;
	private final ModelRenderer skinLeftLeg;

    public HarvestMainModel() {
        texWidth = 128;
        texHeight = 128;

        Torso = new ModelRenderer(this);
        Torso.setPos(0.0F, 0.0F, 0.0F);
        Torso.texOffs(0, 0).addBox(-9.0F, -1.0F, -6.0F, 18.0F, 11.0F, 12.0F, 0.0F, false);
        Torso.texOffs(0, 44).addBox(-7.0F, 10.0F, -5.0F, 14.0F, 6.0F, 10.0F, 0.0F, false);
        Torso.texOffs(50, 38).addBox(-9.0F, -3.0F, -6.0F, 8.0F, 2.0F, 1.0F, 0.0F, false);
        Torso.texOffs(68, 38).addBox(3.0F, -5.0F, -6.0F, 6.0F, 2.0F, 1.0F, 0.0F, false);
        Torso.texOffs(74, 18).addBox(5.0F, -7.0F, -6.0F, 4.0F, 2.0F, 1.0F, 0.0F, false);
        Torso.texOffs(68, 41).addBox(-9.0F, -7.0F, -6.0F, 4.0F, 2.0F, 1.0F, 0.0F, false);
        Torso.texOffs(60, 18).addBox(-9.0F, -5.0F, -6.0F, 6.0F, 2.0F, 1.0F, 0.0F, false);
        Torso.texOffs(50, 41).addBox(1.0F, -3.0F, -6.0F, 8.0F, 2.0F, 1.0F, 0.0F, false);
        Torso.texOffs(60, 0).addBox(-9.0F, -8.0F, -5.0F, 1.0F, 7.0F, 11.0F, 0.0F, false);
        Torso.texOffs(0, 70).addBox(8.0F, -8.0F, -5.0F, 1.0F, 7.0F, 11.0F, 0.0F, false);
        Torso.texOffs(47, 43).addBox(-8.0F, -3.0F, -4.0F, 16.0F, 2.0F, 10.0F, 0.0F, false);
        Torso.texOffs(67, 110).addBox(-7.0F, -6.0F, -4.0F, 14.0F, 3.0F, 10.0F, 0.0F, false);
        Torso.texOffs(77, 86).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 2.0F, 10.0F, 0.0F, false);
        Torso.texOffs(37, 28).addBox(-1.0F, 1.5F, -11.0F, 2.0F, 2.0F, 5.0F, 0.0F, false);

        rightArm = new ModelRenderer(this);
        rightArm.setPos(-10.0F, 9.0F, 0.0F);
        Torso.addChild(rightArm);
        rightArm.texOffs(24, 77).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);
        rightArm.texOffs(19, 116).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.25F, false);

        rightArm2 = new ModelRenderer(this);
        rightArm2.setPos(-10.0F, -1.0F, 0.0F);
        Torso.addChild(rightArm2);
        rightArm2.texOffs(40, 86).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);
        rightArm2.texOffs(19, 102).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.25F, false);

        leftArm = new ModelRenderer(this);
        leftArm.setPos(10.0F, 9.0F, 0.0F);
        Torso.addChild(leftArm);
        leftArm.texOffs(84, 0).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);
        leftArm.texOffs(38, 102).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.25F, false);

        leftArm2 = new ModelRenderer(this);
        leftArm2.setPos(10.0F, -1.0F, 0.0F);
        Torso.addChild(leftArm2);
        leftArm2.texOffs(56, 86).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);
        leftArm2.texOffs(38, 116).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.25F, false);

        RightLeg = new ModelRenderer(this);
        RightLeg.setPos(-6.9F, 12.0F, 0.0F);
        Torso.addChild(RightLeg);
        RightLeg.texOffs(54, 70).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
        RightLeg.texOffs(0, 112).addBox(-2.05F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, false);

        LeftLeg = new ModelRenderer(this);
        LeftLeg.setPos(6.9F, 12.0F, 0.0F);
        Torso.addChild(LeftLeg);
        LeftLeg.texOffs(70, 70).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
        LeftLeg.texOffs(0, 92).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, false);

        tail = new ModelRenderer(this);
        tail.setPos(0.0F, 14.0F, 6.0F);
        Torso.addChild(tail);
        tail.texOffs(84, 12).addBox(-3.0F, -2.0F, 14.0F, 6.0F, 4.0F, 3.0F, 0.0F, false);
        tail.texOffs(48, 55).addBox(-4.0F, 2.0F, -1.0F, 8.0F, 2.0F, 13.0F, 0.0F, false);
        tail.texOffs(0, 23).addBox(-5.0F, -4.0F, -1.0F, 10.0F, 6.0F, 15.0F, 0.0F, false);
        tail.texOffs(50, 23).addBox(-4.0F, -6.0F, -1.0F, 8.0F, 2.0F, 13.0F, 0.0F, false);
        
        // Rabbit skin

		rabbit = new ModelRenderer(this);
		rabbit.setPos(0.0F, 24.0F, 0.0F);
		

		head = new ModelRenderer(this);
		head.setPos(0.0F, -21.0F, 0.0F);
		rabbit.addChild(head);
		head.texOffs(48, 34).addBox(-4.0F, 5.0F, -4.0F, 8.0F, 2.0F, 8.0F, 0.0F, false);
		head.texOffs(46, 47).addBox(-4.5F, -3.0F, -4.5F, 9.0F, 8.0F, 9.0F, 0.0F, false);
		head.texOffs(46, 47).addBox(-5.0F, 0.0F, -6.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		head.texOffs(74, 47).addBox(3.0F, 0.0F, -6.0F, 2.0F, 2.0F, 2.0F, 0.0F, true);

		ear_r1 = new ModelRenderer(this);
		ear_r1.setPos(-3.0F, -3.25F, 0.0F);
		head.addChild(ear_r1);
		ClientUtil.setRotationAngle(ear_r1, 0.0F, 0.0F, -0.2182F);
		ear_r1.texOffs(73, 27).addBox(-2.75F, -8.75F, -0.5F, 4.0F, 7.0F, 1.0F, 0.0F, false);
		ear_r1.texOffs(74, 24).addBox(-2.25F, -9.75F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
		ear_r1.texOffs(46, 37).addBox(-2.25F, -1.75F, -0.5F, 3.0F, 2.0F, 1.0F, 0.0F, false);

		ear_r2 = new ModelRenderer(this);
		ear_r2.setPos(3.0F, -3.25F, 0.0F);
		head.addChild(ear_r2);
		ClientUtil.setRotationAngle(ear_r2, 0.0F, 0.0F, 0.2182F);
		ear_r2.texOffs(74, 37).addBox(-0.75F, -1.75F, -0.5F, 3.0F, 2.0F, 1.0F, 0.0F, true);
		ear_r2.texOffs(45, 27).addBox(-1.25F, -8.75F, -0.5F, 4.0F, 7.0F, 1.0F, 0.0F, true);
		ear_r2.texOffs(46, 24).addBox(-0.75F, -9.75F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, true);

		body = new ModelRenderer(this);
		body.setPos(0.0F, -15.0F, 0.0F);
		rabbit.addChild(body);
		

		upperPart = new ModelRenderer(this);
		upperPart.setPos(0.0F, 12.0F, 0.0F);
		body.addChild(upperPart);
		

		torso = new ModelRenderer(this);
		torso.setPos(0.0F, -12.0F, 0.0F);
		upperPart.addChild(torso);
		torso.texOffs(52, 68).addBox(-4.0F, 1.0F, -2.0F, 8.0F, 2.0F, 4.0F, 0.0F, false);
		torso.texOffs(50, 77).addBox(-4.5F, 3.0F, -3.0F, 9.0F, 7.0F, 5.0F, 0.0F, false);

		leftArmRabbit = new ModelRenderer(this);
		leftArmRabbit.setPos(6.0F, -10.0F, 0.0F);
		upperPart.addChild(leftArmRabbit);
		leftArmRabbit.texOffs(78, 68).addBox(-2.0F, -0.5F, -1.0F, 3.0F, 8.0F, 3.0F, 0.0F, false);

		leftArmJoint = new ModelRenderer(this);
		leftArmJoint.setPos(0.0F, 4.0F, 0.0F);
		leftArm.addChild(leftArmJoint);
		

		leftForeArm = new ModelRenderer(this);
		leftForeArm.setPos(0.0F, 4.0F, 0.0F);
		leftArm.addChild(leftForeArm);
		

		rightArmRabbit = new ModelRenderer(this);
		rightArmRabbit.setPos(-6.0F, -10.0F, 0.0F);
		upperPart.addChild(rightArmRabbit);
		rightArmRabbit.texOffs(38, 68).addBox(-1.0F, -0.5F, -1.0F, 3.0F, 8.0F, 3.0F, 0.0F, false);

		rightArmJoint = new ModelRenderer(this);
		rightArmJoint.setPos(0.0F, 4.0F, 0.0F);
		rightArm.addChild(rightArmJoint);
		

		rightForeArm = new ModelRenderer(this);
		rightForeArm.setPos(0.0F, 4.0F, 0.0F);
		rightArm.addChild(rightForeArm);
		

		leftLeg = new ModelRenderer(this);
		leftLeg.setPos(1.9F, 12.0F, 0.0F);
		body.addChild(leftLeg);
		leftLeg.texOffs(65, 91).addBox(-1.65F, -2.0F, -2.0F, 4.0F, 5.0F, 4.0F, 0.0F, false);

		leftLegJoint = new ModelRenderer(this);
		leftLegJoint.setPos(0.0F, 6.0F, 0.0F);
		leftLeg.addChild(leftLegJoint);
		

		leftLowerLeg = new ModelRenderer(this);
		leftLowerLeg.setPos(0.0F, 6.0F, 0.0F);
		leftLeg.addChild(leftLowerLeg);
		

		rightLeg = new ModelRenderer(this);
		rightLeg.setPos(-1.9F, 12.0F, 0.0F);
		body.addChild(rightLeg);
		rightLeg.texOffs(47, 91).addBox(-2.35F, -2.0F, -2.0F, 4.0F, 5.0F, 4.0F, 0.0F, false);

		rightLegJoint = new ModelRenderer(this);
		rightLegJoint.setPos(0.0F, 6.0F, 0.0F);
		rightLeg.addChild(rightLegJoint);
		

		rightLowerLeg = new ModelRenderer(this);
		rightLowerLeg.setPos(0.0F, 6.0F, 0.0F);
		rightLeg.addChild(rightLowerLeg);

		MainSkin = new ModelRenderer(this);
		MainSkin.setPos(0.0F, 0.0F, 0.0F);
		MainSkin.texOffs(0, 0).addBox(-9.0F, -8.0F, -6.0F, 18.0F, 18.0F, 12.0F, 0.0F, false);
		MainSkin.texOffs(0, 30).addBox(-9.0F, -8.0F, -6.0F, 18.0F, 18.0F, 12.0F, 0.25F, false);
		MainSkin.texOffs(0, 60).addBox(-8.0F, 10.0F, -5.0F, 16.0F, 6.0F, 10.0F, 0.0F, false);

		skinRightArm = new ModelRenderer(this);
		skinRightArm.setPos(-10.0F, 9.0F, 0.0F);
		MainSkin.addChild(skinRightArm);
		skinRightArm.texOffs(52, 64).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);
		skinRightArm.texOffs(68, 64).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.25F, false);

		skinRightArm2 = new ModelRenderer(this);
		skinRightArm2.setPos(-10.0F, -1.0F, 0.0F);
		MainSkin.addChild(skinRightArm2);
		skinRightArm2.texOffs(0, 76).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);
		skinRightArm2.texOffs(76, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.25F, false);

		skinLeftArm = new ModelRenderer(this);
		skinLeftArm.setPos(10.0F, 9.0F, 0.0F);
		MainSkin.addChild(skinLeftArm);
		skinLeftArm.texOffs(76, 12).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);
		skinLeftArm.texOffs(16, 76).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.25F, false);

		skinLeftArm2 = new ModelRenderer(this);
		skinLeftArm2.setPos(10.0F, -1.0F, 0.0F);
		MainSkin.addChild(skinLeftArm2);
		skinLeftArm2.texOffs(76, 24).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.0F, false);
		skinLeftArm2.texOffs(32, 76).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.25F, false);

		skinRightLeg = new ModelRenderer(this);
		skinRightLeg.setPos(-6.9F, 12.0F, 0.0F);
		MainSkin.addChild(skinRightLeg);
		skinRightLeg.texOffs(60, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
		skinRightLeg.texOffs(60, 48).addBox(-2.05F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, false);

		skinLeftLeg = new ModelRenderer(this);
		skinLeftLeg.setPos(6.9F, 12.0F, 0.0F);
		MainSkin.addChild(skinLeftLeg);
		skinLeftLeg.texOffs(60, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.0F, false);
		skinLeftLeg.texOffs(60, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, 0.25F, false);
    }

    @Override
    public void setupAnim(@NotNull HarvestMainEntity harvestMainEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.RightLeg.xRot = this.rightLeg.xRot = this.skinRightLeg.xRot = (float) Math.cos(limbSwing) * 1.4F * limbSwingAmount;
        this.LeftLeg.xRot = this.leftLeg.xRot = this.skinLeftLeg.xRot = (float) Math.cos(limbSwing + Math.PI) * 1.4F * limbSwingAmount;

        this.rightArm.xRot = this.rightArmRabbit.xRot = this.skinRightArm.xRot = this.skinRightArm2.xRot = this.rightArm2.xRot = (float) Math.cos(limbSwing + Math.PI) * 1.4F * limbSwingAmount;
        this.leftArm.xRot = this.leftArmRabbit.xRot = this.skinLeftArm.xRot = this.skinLeftArm2.xRot = this.leftArm2.xRot = (float) Math.cos(limbSwing) * 1.4F * limbSwingAmount;

        this.tail.yRot = (float) Math.sin(ageInTicks * 0.1F) * 0.1F;

		this.rightArmRabbit.xRot = (float) Math.cos(limbSwing + Math.PI) * 1.4F * limbSwingAmount;
		this.leftArmRabbit.xRot = (float) Math.cos(limbSwing) * 1.4F * limbSwingAmount;
        
        IStandPower power = harvestMainEntity.getOwnerPower();
        if (power != null && StandSkinsManager.getInstance() != null && power.getStandInstance().isPresent()) {
        	Optional<StandSkin> recSkin = StandSkinsManager.getInstance().getStandSkin(power.getStandInstance().get());
        	if (recSkin.isPresent() && !recSkin.get().defaultSkin) {
				this.Torso.visible = false;
				switch (recSkin.get().resLoc.getPath()) {
					case "rabbit":
						this.rabbit.visible = true;
						break;
					case "ryomen_sukuna":
					case "sukuna_itadori":
					case "sukuna_megumi":
						this.rabbit.visible = false;
						this.MainSkin.visible = true;
						break;
					case "purple":
					case "blue":
					case "red":
						this.rabbit.visible = false;
						this.Torso.visible = true;
						this.MainSkin.visible = false;
						break;
					default:
						break;
				}
        	} else {
        		this.rabbit.visible = false;
        		this.Torso.visible = true;
				this.MainSkin.visible = false;
        	}
        }
    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, @NotNull IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        matrixStack.pushPose();
		matrixStack.translate(0, 1.2500, 0);
        matrixStack.scale(.1500F, .1500F, .1500F);
        Torso.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		matrixStack.popPose();

		matrixStack.pushPose();
		matrixStack.translate(0, 1.15, 0);
		matrixStack.scale(.2222F,.2222F,.2222F);
        rabbit.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		matrixStack.popPose();

		matrixStack.pushPose();
		matrixStack.translate(0, 1.2500, 0);
        matrixStack.scale(.1500F, .1500F, .1500F);
		MainSkin.render(matrixStack, buffer, packedLight, packedOverlay);
		matrixStack.popPose();
    }

    @Override
    public void translateToHand(HandSide p_225599_1_, MatrixStack p_225599_2_) {
        this.getArm(p_225599_1_).translateAndRotate(p_225599_2_);
    }

    private ModelRenderer getArm(HandSide p_191216_1_) {
        return p_191216_1_ == HandSide.LEFT ? this.leftArm2 : this.rightArm2;
    }
}