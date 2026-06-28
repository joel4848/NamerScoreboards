package com.joel4848.nicknameseverywhere.impl;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public record NickSuggestionData(Text name, @Nullable Text nick) {
}