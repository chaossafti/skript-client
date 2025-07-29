package de.safti.skriptclient.commons.screens.components.main.log;

import de.safti.skriptclient.commons.screens.ScriptManagementScreen;
import de.safti.skriptclient.commons.screens.components.main.AbstractMainArea;
import de.safti.skriptclient.commons.screens.components.sidebar.logs.ScriptLogEntryComponent;
import io.github.syst3ms.skriptparser.log.LogEntry;
import io.github.syst3ms.skriptparser.parsing.script.ScriptLoadResult;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LogMainArea extends AbstractMainArea {
    private final ScriptLogEntryComponent logComponent;
    private final LogTableSizeContext tableSize;


    public LogMainArea(ScriptManagementScreen screen, ScriptLogEntryComponent logComponent) {
        super(screen, Sizing.expand(), Sizing.expand(), Algorithm.VERTICAL);
        this.logComponent = logComponent;
        this.tableSize = LogTableSizeContext.ofLoadLogEntry(logComponent.getLogEntry(), Minecraft.getInstance().font);

        // config
        gap(tableSize.verticalGap());
        padding(Insets.left(4));

        // children
        List<Component> children = new ArrayList<>();
        children.add(new LogTableHead(tableSize, Minecraft.getInstance().font));

        for (ScriptLoadResult loadResult : logComponent.getLogEntry().loadResults()) {
            Optional<List<LogEntry>> logOpt = loadResult.getLog();
            if(logOpt.isEmpty()) continue;

            for (LogEntry logEntry : logOpt.get()) {
                children.add(new LogTableEntry(logEntry, tableSize));
            }
        }

        children(children);
    }


    public ScriptLogEntryComponent getLogComponent() {
        return logComponent;
    }

    public LogTableSizeContext getTableSize() {
        return tableSize;
    }
}
