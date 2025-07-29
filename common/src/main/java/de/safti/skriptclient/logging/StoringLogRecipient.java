package de.safti.skriptclient.logging;

import io.github.syst3ms.skriptparser.log.LogEntry;
import io.github.syst3ms.skriptparser.log.LogType;
import io.github.syst3ms.skriptparser.parsing.script.ScriptLoadResult;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class StoringLogRecipient implements LogRecipient {
    public static StoringLogRecipient INSTANCE = new StoringLogRecipient();
    private final List<ScriptLoadLogEntry> logs = new ArrayList<>();


    @Override
    public void send(@NotNull Set<ScriptLoadResult> loadInfo) {
        ScriptLoadLogEntry entry = new ScriptLoadLogEntry(loadInfo);
        logs.add(entry);
    }

    @Override
    public void sendNoScriptsFound() {

    }

    @Override
    public void sendSuccessfulReload(int totalScriptsLoaded) {

    }

    public List<ScriptLoadLogEntry> getLogs() {
        return logs;
    }

    public static final class ScriptLoadLogEntry {
        private final Set<ScriptLoadResult> loadResults;
        private boolean seen;

        public ScriptLoadLogEntry(Set<ScriptLoadResult> loadResults) {
            this.loadResults = loadResults;
            this.seen = false;
        }

        public Set<ScriptLoadResult> loadResults() {
            return loadResults;
        }

        public boolean seen() {
            return seen;
        }

        public void setSeen(boolean seen) {
            this.seen = seen;
        }

        public int countType(LogType type) {
            int count = 0;
            for (ScriptLoadResult loadResult : loadResults) {
                Optional<List<LogEntry>> logOpt = loadResult.getLog();
                if(logOpt.isEmpty()) continue;

                for (LogEntry logEntry : logOpt.get()) {
                    if(logEntry.getType() == type) {
                        count++;
                    }
                }
            }

            return count;
        }

        public int getTotalErrors() {
            return countType(LogType.ERROR);
        }

        public int getTotalWarnings() {
            return countType(LogType.WARNING);
        }

    }

}
