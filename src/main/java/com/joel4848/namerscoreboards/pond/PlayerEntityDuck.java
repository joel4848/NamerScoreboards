package com.joel4848.namerscoreboards.pond;

import net.minecraft.text.Text;

public interface PlayerEntityDuck {
    default Text namerscoreboards$getActualDisplayName() {
        throw new UnsupportedOperationException("Implemented by Mixin");
    }
}