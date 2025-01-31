package com.nettakrim.signed_paintings.rendering;

import com.nettakrim.signed_paintings.util.ImageManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector3f;

public class PaintingRenderer {
    public PaintingRenderer() {

    }

    public void renderPainting(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Model model, PaintingInfo info, int light, float rotationDegrees) {
        Identifier image = info.getImageIdentifier();
        if (!ImageManager.hasImage(image)) return;

        matrices.push();
        matrices.translate(0.5F, 0.5F, 0.5F);
        matrices.translate(info.offsetVec.x, info.offsetVec.y, info.offsetVec.z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationDegrees + info.rotationVec.y + (info.isFront ? 0 : 180)));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(info.rotationVec.z));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(info.rotationVec.x));
        info.cuboid.setupRendering(matrices);

        VertexConsumer imageVertexConsumer = vertexConsumers.getBuffer(model.getLayer(info.getImageIdentifier()));
        renderImage(imageVertexConsumer, info, light);

        if (info.getBackType() != BackType.Type.NONE) {
            Sprite sprite = info.getBackSprite();
            VertexConsumer backVertexConsumer = sprite.getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(model.getLayer(sprite.getAtlasId())));
            renderBack(backVertexConsumer, sprite, info, light);
        }

        matrices.pop();
    }

    private void renderImage(VertexConsumer vertexConsumer, PaintingInfo info, int light) {
        info.cuboid.renderFace(vertexConsumer, new Vector3f(0, 0, 1), false, 0, 1, 0, 1, light);
    }

    private void renderBack(VertexConsumer vertexConsumer, Sprite backSprite, PaintingInfo info, int light) {
        info.cuboid.renderFace(vertexConsumer, new Vector3f(0,  0,  -1), true, backSprite.getMinU(), backSprite.getMaxU(), backSprite.getMinV(), backSprite.getMaxV(), light);

        info.cuboid.renderFace(vertexConsumer, new Vector3f(1,  0,  0),  true, backSprite.getMinU(), backSprite.getMaxU(), backSprite.getMinV(), backSprite.getMaxV(), light);
        info.cuboid.renderFace(vertexConsumer, new Vector3f(-1, 0,  0),  true, backSprite.getMinU(), backSprite.getMaxU(), backSprite.getMinV(), backSprite.getMaxV(), light);

        info.cuboid.renderFace(vertexConsumer, new Vector3f(0,  1,  0),  true, backSprite.getMinU(), backSprite.getMaxU(), backSprite.getMinV(), backSprite.getMaxV(), light);
        info.cuboid.renderFace(vertexConsumer, new Vector3f(0,  -1, 0),  true, backSprite.getMinU(), backSprite.getMaxU(), backSprite.getMinV(), backSprite.getMaxV(), light);
    }

    public void renderImageOverlay(MatrixStack matrices, VertexConsumerProvider vertexConsumers, OverlayInfo info, ModelPart canvas, int light) {
        Identifier image = info.getImageIdentifier();
        if (!ImageManager.hasImage(image)) return;

        matrices.push();
        //these numbers are entirely trial and error, I have no idea how to derive them, Z:0.021 is more accurate but severely z-fights at long distances
        matrices.translate(0F, 3.335f, 0.025f);
        canvas.rotate(matrices);
        VertexConsumer imageVertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(image));
        info.cuboid.setupRendering(matrices);
        info.cuboid.renderFace(imageVertexConsumer, new Vector3f(0, 0, 1), false, 0, 1, 0, 1, light);
        matrices.pop();
    }

    public void renderItemOverlay(MatrixStack matrices, VertexConsumerProvider vertexConsumers, OverlayInfo info, int light) {
        Identifier image = info.getImageIdentifier();
        if (!ImageManager.hasImage(image)) return;

        matrices.push();
        //these are also trial and error
        matrices.scale(0.75f, -0.75f, -1f);
        matrices.translate(0F, 0.833f, 0.065f);
        VertexConsumer imageVertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(image));
        info.cuboid.setupRendering(matrices);
        info.cuboid.renderFace(imageVertexConsumer, new Vector3f(0, 0, 1), false, 0, 1, 0, 1, light);
        matrices.pop();
    }
}
