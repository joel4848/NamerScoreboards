package com.joel4848.nicknameseverywhere.pond;

import com.joel4848.nicknameseverywhere.impl.NickSuggestionData;

import java.util.List;

public interface CommandSourceDuck {
    default List<NickSuggestionData> nicknameseverywhere$getNickSuggestionData() {
        throw new UnsupportedOperationException("Implemented by Mixin");
    }
}