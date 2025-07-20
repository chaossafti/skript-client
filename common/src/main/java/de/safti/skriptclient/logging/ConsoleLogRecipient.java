package de.safti.skriptclient.logging;

import de.safti.skriptclient.SkriptClient;
import io.github.syst3ms.skriptparser.log.LogEntry;
import io.github.syst3ms.skriptparser.log.LogType;
import io.github.syst3ms.skriptparser.parsing.script.Script;
import io.github.syst3ms.skriptparser.parsing.script.ScriptLoadResult;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Set;

public class ConsoleLogRecipient implements LogRecipient {
	public static ConsoleLogRecipient INSTANCE = new ConsoleLogRecipient();
	private static final Logger log = LoggerFactory.getLogger(ConsoleLogRecipient.class);
	
	public ConsoleLogRecipient() {
	}
	
	
	@Override
	public void send(@NotNull Set<ScriptLoadResult> loadResults) {
		int errorCount = 0;
		int warnCount = 0;
		int failedLoads = 0;
		
		for (ScriptLoadResult loadResult : loadResults) {
			if(!loadResult.hasParsedSuccessfully()) {
				failedLoads++;
				continue;
			}

			Script script = loadResult.getScript();
			assert script != null;

            for (LogEntry logEntry : loadResult.getLog().orElseThrow()) {
				switch(logEntry.getType()) {
					case WARNING -> warnCount++;
					case ERROR -> errorCount++;
				}
				
				logLogEntry(logEntry);
			}
		}



		// log final warn/error count
		if(errorCount == 0 && warnCount == 0) {
			sendSuccessfulReload(loadResults.size());
			return;
		}

		log.info("Script parsing completed!");
		log.info("   - {} scripts", loadResults.size());
		log.info("   - {} warnings", warnCount);
		log.info("   - {} errors", errorCount);

		if(failedLoads > 0) {
			log.warn("There was exceptions while reloading! Please report them at the github issue tracker.");
		}
		
	}
	
	private void logLogEntry(LogEntry logEntry) {
		// TODO: make the skript parser fork hold reference to the unedited message without the bloated ending

		int line = logEntry.getLine() + 1;
		LogType type = logEntry.getType();
		Script script = logEntry.getScript();
		assert script != null;

		if(type == LogType.ERROR) {
			log.error("ERROR in line: {} ({})", line, script.getName());
			log.error("   {}", logEntry.getMessage());
			log.error("");
		}
	}
	
	@Override
	public void sendNoScriptsFound() {
		log.warn("Skript-Client could not find any Scripts to load!");
		log.warn("Scripts should be located at {}", SkriptClient.core.getScriptsFolder().toFile().getAbsolutePath());
	}
	
	@Override
	public void sendSuccessfulReload(int totalScriptsLoaded) {
		log.info("Script parsing completed without any errors!");
		log.info("   - {} scripts", totalScriptsLoaded);
	}
}
