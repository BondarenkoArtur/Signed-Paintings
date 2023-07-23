package com.nettakrim.mixin;

import com.nettakrim.Cuboid;
import com.nettakrim.SignEditingInfo;
import com.nettakrim.SignedPaintingsClient;
import com.nettakrim.access.AbstractSignEditScreenAccessor;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin extends Screen implements AbstractSignEditScreenAccessor {
    @Shadow
    private SignText text;

    @Final
    @Shadow
    private String[] messages;

    @Final
    @Shadow
    private SignBlockEntity blockEntity;

    @Final
    @Shadow
    private boolean front;

    @Shadow
    private int currentRow;

    @Shadow
    private SelectionManager selectionManager;

    protected AbstractSignEditScreenMixin(Text title) {
        super(title);
    }

    @Shadow protected abstract void setCurrentRowMessage(String message);

    @Inject(at = @At("TAIL"), method = "init")
    private void init(CallbackInfo ci) {
        //🡤🡡🡥🡠◯🡢🡧🡣🡦
        String[] arrows = new String[] {"\uD83E\uDC64","\uD83E\uDC61","\uD83E\uDC65","\uD83E\uDC60","◯","\uD83E\uDC62","\uD83E\uDC67","\uD83E\uDC63","\uD83E\uDC66"};
        for (Cuboid.Centering xCentering : Cuboid.Centering.values()) {
            for (Cuboid.Centering yCentering : Cuboid.Centering.values()) {
                int arrowIndex = 2-xCentering.ordinal() + yCentering.ordinal()*3;
                createCenteringButton(75, 25, arrows[arrowIndex], xCentering, yCentering);
            }
        }
        SignedPaintingsClient.currentSignEdit.setSelectionManager(selectionManager);
    }

    @Unique
    private void createCenteringButton(int areaSize, int buttonSize, String text, Cuboid.Centering xCentering, Cuboid.Centering yCentering) {
        ButtonWidget widget = ButtonWidget.builder(Text.literal(text), button -> SignedPaintingsClient.currentSignEdit.updatePaintingCentering(front, xCentering, yCentering))
        .position(getCenteringButtonPosition(areaSize, xCentering, buttonSize, width)-(width/4), getCenteringButtonPosition(-areaSize, yCentering, buttonSize, height))
        .size(buttonSize, buttonSize)
        .build();

        addDrawableChild(widget);
        addSelectableChild(widget);
    }

    @Unique
    private int getCenteringButtonPosition(int size, Cuboid.Centering centering, int buttonSize, int screenSize) {
        return MathHelper.floor(Cuboid.getOffsetFromCentering(size, centering)) + screenSize/2 - buttonSize/2;
    }

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/block/entity/SignBlockEntity;ZZLnet/minecraft/text/Text;)V")
    private void onScreenOpen(SignBlockEntity blockEntity, boolean front, boolean filtered, Text title, CallbackInfo ci) {
        SignedPaintingsClient.currentSignEdit = new SignEditingInfo(blockEntity, this);
    }

    @Inject(at = @At("TAIL"), method = "finishEditing")
    private void onScreenClose(CallbackInfo ci) {
        SignedPaintingsClient.currentSignEdit = null;
    }

    @Override
    public void signedPaintings$clear() {
        for (int i = 0; i < messages.length; i++) {
            this.messages[i] = "";
            this.text = this.text.withMessage(i, Text.literal("message"));
        }
        this.blockEntity.setText(this.text, this.front);
        this.currentRow = 0;
    }

    @Override
    public int signedPaintings$paste(String pasteString, int selectionStart, int selectionEnd) {
        String[] newMessages = new String[messages.length];
        System.arraycopy(messages, 0, newMessages, 0, messages.length);

        int maxWidthPerLine = this.blockEntity.getMaxTextWidth();
        TextRenderer textRenderer = SignedPaintingsClient.client.textRenderer;

        selectionStart = MathHelper.clamp(selectionStart, 0, newMessages[currentRow].length());
        selectionEnd = MathHelper.clamp(selectionEnd, 0, newMessages[currentRow].length());
        newMessages[currentRow] = newMessages[currentRow].substring(0,selectionStart)+pasteString+newMessages[currentRow].substring(selectionEnd);
        int currentWidth = textRenderer.getWidth(newMessages[currentRow]);

        int cursor = selectionStart+pasteString.length();

        if (currentWidth < maxWidthPerLine) {
            setCurrentRowMessage(newMessages[currentRow]);
            return cursor;
        }

        int cursorRow = currentRow;

        while (true) {
            String line = newMessages[currentRow];
            int index = SignedPaintingsClient.getMaxFittingIndex(line, maxWidthPerLine, textRenderer);
            newMessages[currentRow] = line.substring(0, index);
            if (currentRow == messages.length-1 || line.length() <= index) {
                break;
            }
            if (currentRow == cursorRow && cursor > index) {
                cursorRow++;
                cursor -= index;
            }
            currentRow++;
            newMessages[currentRow] = line.substring(index)+newMessages[currentRow];
        }
        cursor = MathHelper.clamp(cursor, 0, newMessages[cursorRow].length());

        for (int i = 0; i < messages.length; i++) {
            this.messages[i] = newMessages[i];
            this.text = this.text.withMessage(i, Text.literal(this.messages[i]));
        }
        this.blockEntity.setText(this.text, this.front);

        currentRow = cursorRow;
        return cursor;
    }
}
