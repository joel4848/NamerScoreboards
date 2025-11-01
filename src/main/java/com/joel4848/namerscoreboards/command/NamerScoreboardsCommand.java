package com.joel4848.namerscoreboards.command;

import com.joel4848.namerscoreboards.NamerScoreboards;
import com.joel4848.namerscoreboards.util.NickFormatter;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.joel4848.namerscoreboards.registry.CardinalComponentsRegistry.NICK_STORAGE;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NamerScoreboardsCommand {
    private static final DynamicCommandExceptionType SET_FAIL = new DynamicCommandExceptionType(o -> Text.translatable("command.namerscoreboards.nick.set.fail", o));
    private static final Dynamic2CommandExceptionType SET_FAIL_OTHER = new Dynamic2CommandExceptionType((a, b) -> Text.translatable("command.namerscoreboards.nick.set.fail.other", a, b));
    private static final SimpleCommandExceptionType NO_PERMISSION = new SimpleCommandExceptionType(Text.translatable("command.namerscoreboards.nick.no.permission"));

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    literal("namerscoreboards")
                            // Player commands
                            .then(literal("set")
                                    .then(argument("nickname", StringArgumentType.greedyString())
                                            .executes(context -> {
                                                if (!NamerScoreboards.CONFIG.allowSettingOwnNicknames() && !context.getSource().hasPermissionLevel(2)) {
                                                    throw NO_PERMISSION.create();
                                                }
                                                return setNick(
                                                        context.getSource(),
                                                        context.getSource().getPlayerOrThrow(),
                                                        StringArgumentType.getString(context, "nickname")
                                                );
                                            })
                                    )
                            )
                            .then(literal("clear")
                                    .executes(context -> {
                                        if (!NamerScoreboards.CONFIG.allowSettingOwnNicknames() && !context.getSource().hasPermissionLevel(2)) {
                                            throw NO_PERMISSION.create();
                                        }
                                        return setNick(
                                                context.getSource(),
                                                context.getSource().getPlayerOrThrow(),
                                                null
                                        );
                                    })
                            )
                            // Admin commands
                            .then(literal("setPlayerNick")
                                    .requires(source -> source.hasPermissionLevel(2))
                                    .then(argument("player", EntityArgumentType.player())
                                            .then(argument("nickname", StringArgumentType.greedyString())
                                                    .executes(context -> setNick(
                                                            context.getSource(),
                                                            EntityArgumentType.getPlayer(context, "player"),
                                                            StringArgumentType.getString(context, "nickname")
                                                    ))
                                            )
                                    )
                            )
                            .then(literal("clearPlayerNick")
                                    .requires(source -> source.hasPermissionLevel(2))
                                    .then(argument("player", EntityArgumentType.player())
                                            .executes(context -> setNick(
                                                    context.getSource(),
                                                    EntityArgumentType.getPlayer(context, "player"),
                                                    null
                                            ))
                                    )
                            )
                            // Config commands
                            .then(literal("allowSettingOwnNicknames")
                                    .requires(source -> source.hasPermissionLevel(2))
                                    .executes(context -> {
                                        boolean value = NamerScoreboards.CONFIG.allowSettingOwnNicknames();
                                        context.getSource().sendFeedback(
                                                () -> Text.translatable("command.namerscoreboards.allowSettingOwnNicknames.current", value ? "§aenabled" : "§cdisabled"),
                                                true
                                        );
                                        return 1;
                                    })
                                    .then(argument("value", BoolArgumentType.bool())
                                            .executes(context -> {
                                                boolean value = BoolArgumentType.getBool(context, "value");
                                                NamerScoreboards.CONFIG.setAllowSettingOwnNicknames(value);
                                                context.getSource().sendFeedback(
                                                        () -> Text.translatable("command.namerscoreboards.allowSettingOwnNicknames." + (value ? "enabled" : "disabled")),
                                                        true
                                                );
                                                return 1;
                                            })
                                    )
                            )
                            .then(literal("maxNickLength")
                                    .requires(source -> source.hasPermissionLevel(2))
                                    .executes(context -> {
                                        int value = NamerScoreboards.CONFIG.maxNickLength();
                                        context.getSource().sendFeedback(
                                                () -> Text.translatable("command.namerscoreboards.maxNickLength.current", "§a" + value),
                                                true
                                        );
                                        return 1;
                                    })
                                    .then(argument("value", IntegerArgumentType.integer(0, 256))
                                            .executes(context -> {
                                                int value = IntegerArgumentType.getInteger(context, "value");
                                                NamerScoreboards.CONFIG.setMaxNickLength(value);
                                                context.getSource().sendFeedback(
                                                        () -> Text.translatable("command.namerscoreboards.maxNickLength.set", "§a" + value),
                                                        true
                                                );
                                                return 1;
                                            })
                                    )
                            )
                            .then(literal("allowNickFormatting")
                                    .requires(source -> source.hasPermissionLevel(2))
                                    .executes(context -> {
                                        boolean value = NamerScoreboards.CONFIG.allowNickFormatting();
                                        context.getSource().sendFeedback(
                                                () -> Text.translatable("command.namerscoreboards.allowNickFormatting.current", value ? "§aenabled" : "§cdisabled"),
                                                true
                                        );
                                        return 1;
                                    })
                                    .then(argument("value", BoolArgumentType.bool())
                                            .executes(context -> {
                                                boolean value = BoolArgumentType.getBool(context, "value");
                                                NamerScoreboards.CONFIG.setAllowNickFormatting(value);
                                                context.getSource().sendFeedback(
                                                        () -> Text.translatable("command.namerscoreboards.allowNickFormatting." + (value ? "enabled" : "disabled")),
                                                        true
                                                );
                                                return 1;
                                            })
                                    )
                            )
            );
        });
    }

    public static int setNick(ServerCommandSource source, ServerPlayerEntity target, @Nullable String nick) throws CommandSyntaxException {
        var server = source.getServer();
        var storage = NICK_STORAGE.getNullable(server.getScoreboard());
        var self = Objects.equals(source.getEntity(), target);
        if (storage == null) {
            var message = Text.translatable("command.namerscoreboards.nick.set.fail.unknown");
            throw self ? SET_FAIL.create(message) : SET_FAIL_OTHER.create(
                    Text.literal(target.getName().getString()).formatted(Formatting.GOLD),
                    message
            );
        }

        var parsedNick = NickFormatter.parseNick(nick);
        var nickString = parsedNick.getString();

        // Clear nickname if null, blank, or same as username
        if (nick == null || nickString.isBlank() || parsedNick.equals(target.getName())) {
            storage.clearNick(target);
            source.sendFeedback(() -> self
                    ? Text.translatable("command.namerscoreboards.nick.clear.success")
                    : Text.translatable("command.namerscoreboards.nick.clear.success.other",
                    Text.literal(target.getName().getString()).formatted(Formatting.GOLD)
            ), true);
            return 1;
        }

        int maxLength = NamerScoreboards.CONFIG.maxNickLength();
        if (maxLength > 0 && nickString.length() > maxLength) {
            var message = Text.translatable("command.namerscoreboards.nick.set.fail.length",
                    Text.literal(parsedNick.getString()).formatted(Formatting.GOLD),
                    Text.literal(String.valueOf(maxLength)).formatted(Formatting.GREEN)
            );
            throw self ? SET_FAIL.create(message) : SET_FAIL_OTHER.create(
                    Text.literal(target.getName().getString()).formatted(Formatting.GOLD),
                    message
            );
        }

        storage.setNick(target, nick);

        source.sendFeedback(() -> self
                ? Text.translatable("command.namerscoreboards.nick.set.success",
                Text.literal(parsedNick.getString()).formatted(Formatting.GOLD)
        )
                : Text.translatable("command.namerscoreboards.nick.set.success.other",
                Text.literal(target.getName().getString()).formatted(Formatting.GOLD),
                Text.literal(parsedNick.getString()).formatted(Formatting.GOLD)
        ), true);

        return 1;
    }
}