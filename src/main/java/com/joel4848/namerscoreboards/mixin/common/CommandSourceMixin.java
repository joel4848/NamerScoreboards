package com.joel4848.namerscoreboards.mixin.common;

import com.joel4848.namerscoreboards.impl.NickSuggestionData;
import com.joel4848.namerscoreboards.pond.CommandSourceDuck;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.List;

@Mixin(CommandSource.class)
public interface CommandSourceMixin extends CommandSourceDuck {
    @Shadow Collection<String> getPlayerNames();

    @Override
    default List<NickSuggestionData> namerscoreboards$getNickSuggestionData() {
        return getPlayerNames().stream()
                .map(playerName -> new NickSuggestionData(Text.literal(playerName), null))
                .toList();
    }
}