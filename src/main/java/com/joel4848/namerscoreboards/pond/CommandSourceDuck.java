package com.joel4848.namerscoreboards.pond;

import com.joel4848.namerscoreboards.impl.NickSuggestionData;

import java.util.List;

public interface CommandSourceDuck {
    default List<NickSuggestionData> namerscoreboards$getNickSuggestionData() {
        throw new UnsupportedOperationException("Implemented by Mixin");
    }
}