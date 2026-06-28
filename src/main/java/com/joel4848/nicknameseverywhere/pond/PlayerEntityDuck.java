package com.joel4848.nicknameseverywhere.pond;

import net.minecraft.text.Text;

public interface PlayerEntityDuck {
    default Text nicknameseverywhere$getActualDisplayName() {
        throw new UnsupportedOperationException("Implemented by Mixin");
    }
}