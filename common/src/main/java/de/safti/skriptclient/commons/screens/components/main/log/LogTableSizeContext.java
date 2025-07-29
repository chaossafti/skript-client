package de.safti.skriptclient.commons.screens.components.main.log;

import de.safti.skriptclient.logging.StoringLogRecipient.ScriptLoadLogEntry;
import io.github.syst3ms.skriptparser.log.LogEntry;
import io.github.syst3ms.skriptparser.parsing.script.ScriptLoadResult;
import io.wispforest.owo.ui.component.Components;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

public record LogTableSizeContext(int symbolWidth, int lineStringWidth,
                                  int messageWidth, int scriptWidth,
                                  int horizontalGap, int verticalGap) {


    public static LogTableSizeContext ofLoadLogEntry(ScriptLoadLogEntry logEntry, Font textRenderer) {
        int symbolWidth = 32; // default width
        int maxMessageWidth = textRenderer.width("Message");
        int maxLineStringWidth = textRenderer.width("Line");
        int maxScriptNameWidth = textRenderer.width("Script");

        for (ScriptLoadResult loadResult : logEntry.loadResults()) {
            Optional<List<LogEntry>> logOpt = loadResult.getLog();
            if(logOpt.isEmpty()) continue;

            for (LogEntry entry : logOpt.get()) {
                // message
                String message = entry.getMessage();
                int messageWidth = textRenderer.width(message);
                if(messageWidth > maxMessageWidth) maxMessageWidth = messageWidth;

                // line
                String line = String.valueOf(entry.getLine());
                int lineStringWidth = textRenderer.width(line);
                if(lineStringWidth > maxLineStringWidth) maxLineStringWidth = lineStringWidth;

                // line
                String scriptName = entry.getScript().getName();
                int scriptNameWidth = textRenderer.width(scriptName);
                if(scriptNameWidth > maxScriptNameWidth) maxScriptNameWidth = scriptNameWidth;
            }
        }

        return new LogTableSizeContext(symbolWidth, maxLineStringWidth, maxMessageWidth, maxScriptNameWidth, 10, 4);
    }

    public int totalWidth() {
        return symbolWidth + lineStringWidth + messageWidth + scriptWidth
                + horizontalGap*3;
    }


}
