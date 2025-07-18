package de.safti.skriptclient.logging;

import io.github.syst3ms.skriptparser.log.LogEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Set;

public record ScriptLoadInfo(@NotNull Set<LogEntry> logs, @Nullable Path script, boolean successful) {
}
