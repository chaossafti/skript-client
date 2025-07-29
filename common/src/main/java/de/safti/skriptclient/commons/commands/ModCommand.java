package de.safti.skriptclient.commons.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.bridge.Core;
import de.safti.skriptclient.logging.StoringLogRecipient;
import io.github.syst3ms.skriptparser.log.LogType;
import io.github.syst3ms.skriptparser.parsing.ScriptLoader;
import io.github.syst3ms.skriptparser.parsing.script.Script;
import io.github.syst3ms.skriptparser.parsing.script.ScriptLoadResult;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ModCommand {

    private static final Logger log = LoggerFactory.getLogger(ModCommand.class);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> base = Commands.literal("skriptclient")
                // INFO COMMAND
                .then(Commands.literal("info")
                        .executes(ModCommand::runInfo))

                // RELOAD COMMAND
                .then(Commands.literal("reload")
                        .then(Commands.argument("target", StringArgumentType.word())
                                .suggests(RELOAD_SUGGESTIONS)
                                .executes(ctx -> {
                                    try {
                                        return runReload(ctx, StringArgumentType.getString(ctx, "target"));
                                    } catch (Throwable t) {
                                        t.printStackTrace();
                                        throw t;
                                    }
                                })))

                // DISABLE COMMAND
                .then(Commands.literal("disable")
                        .then(Commands.argument("target", StringArgumentType.word())
                                .suggests(DISABLE_SUGGESTIONS)
                                .executes(ctx -> runDisable(ctx, StringArgumentType.getString(ctx, "target")))));

        dispatcher.register(base);
        dispatcher.register(Commands.literal("skclient").redirect(base.build()));
        dispatcher.register(Commands.literal("skc").redirect(base.build()));
    }

    // Subcommand logic methods
    private static int runInfo(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendSuccess(() -> Component.literal("SkriptClient is active."), false);
        return 1;
    }

    private static int runReload(CommandContext<CommandSourceStack> ctx, String target) {
        if(target.equals("scripts") || target.equals("all") || target.equals("config")) {
            // TODO: reload scripts, all or config.
            ctx.getSource().sendFailure(Component.literal("This feature has not been implemented yet!"));
            return 0;
        }

        // get and validate path existence
        Core core = SkriptClient.CORE;
        Path scriptsFolder = core.getScriptsFolder();
        Path path = scriptsFolder.resolve(target);
        if(Files.notExists(path)) {
            ctx.getSource().sendFailure(Component.literal("Could not find script " + target));
            return 0;
        }

        Optional<Script> scriptOpt = ScriptLoader.getScript(path);

        // reload or load, depending on if the script is already loaded.
        ScriptLoadResult loadResult;
        if(scriptOpt.isPresent()) {
            loadResult = scriptOpt.get().reload();
        } else {
            loadResult = ScriptLoader.loadScript(path, false);
        }

        // log
        StoringLogRecipient.INSTANCE.send(Set.of(loadResult));


        // send success
        ctx.getSource().sendSuccess(() -> Component.literal("§7[&6SK-Client§7] Reloaded: " + target), false);
        ctx.getSource().sendSuccess(() -> Component.literal("§7[&6SK-Client§7] Press o to see the logs!"), false);

        boolean successfullyParsed = loadResult.getLog().map(logEntries -> logEntries.stream().noneMatch(logEntry -> logEntry.getType() == LogType.ERROR)).orElse(true);
        if(successfullyParsed) ctx.getSource().sendSuccess(() -> Component.literal("§7[&6SK-Client§7] Reloaded without any errors!"), false);

        return 1;
    }

    private static int runDisable(CommandContext<CommandSourceStack> ctx, String target) {
        Core core = SkriptClient.CORE;
        Path scriptsFolder = core.getScriptsFolder();
        Path path = scriptsFolder.resolve(target);

        // validate path existence
        if(Files.notExists(path)) {
            ctx.getSource().sendFailure(Component.literal("Could not find script " + target));
            return 0;
        }

        // unload script
        Optional<Script> scriptOpt = ScriptLoader.getScript(path);
        scriptOpt.ifPresent(Script::unload);

        // get sibling and validate
        Path newPath = path.resolveSibling("renamedScript.skript");
        if(Files.exists(newPath)) {
            ctx.getSource().sendFailure(Component.literal("Could not disable script as %s is already present!".formatted(newPath)));
            ctx.getSource().sendFailure(Component.literal("The script was unloaded successfully."));
            return 0;
        }

        // rename file/disable file
        try {
            Files.move(path, newPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Failed to disable script!", e);
            ctx.getSource().sendFailure(Component.literal("An internal error happened. See logs for more information!"));
            return 0;
        }

        // TODO: formatting
        ctx.getSource().sendSuccess(() -> Component.literal("Disabled: " + target), false);
        return 1;
    }

    // Suggestion providers
    private static final SuggestionProvider<CommandSourceStack> RELOAD_SUGGESTIONS = ModCommand::suggestReload;
    private static final SuggestionProvider<CommandSourceStack> DISABLE_SUGGESTIONS = ModCommand::suggestDisable;

    private static CompletableFuture<Suggestions> suggestReload(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        Core core = SkriptClient.CORE;
        Path scriptsFolder = core.getScriptsFolder();
        Set<String> suggestions;

        try(var stream = Files.walk(scriptsFolder)) {

            suggestions = stream
                    .filter(Files::isRegularFile)
                    .map(path -> path.toFile().getName())
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.error("Could not collect all scripts!", e);
            return CompletableFuture.failedFuture(e);
        }

        suggestions.add("scripts");
        suggestions.add("config");
        suggestions.remove("..");

        return SharedSuggestionProvider.suggest(suggestions, builder);
    }

    // TODO: remove duplicate logic
    private static CompletableFuture<Suggestions> suggestDisable(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        Core core = SkriptClient.CORE;
        Path scriptsFolder = core.getScriptsFolder();
        Set<String> suggestions;
        try(var stream = Files.walk(scriptsFolder)) {

            suggestions = stream
                    .map(path -> path.relativize(scriptsFolder).toFile().getName())
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.error("Could not collect all scripts!", e);
            return CompletableFuture.failedFuture(e);
        }

        suggestions.add("scripts");
        suggestions.add("config");
        suggestions.remove("..");

        return SharedSuggestionProvider.suggest(suggestions, builder);
    }

}
