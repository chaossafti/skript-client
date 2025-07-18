package de.safti.skriptclient.logging;

import de.safti.skriptclient.SkriptClient;
import io.github.syst3ms.skriptparser.log.LogEntry;
import io.github.syst3ms.skriptparser.log.LogType;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

public class ConsoleLogRecipient implements LogRecipient {
	public static ConsoleLogRecipient INSTANCE = new ConsoleLogRecipient();
	private static final Logger log = LoggerFactory.getLogger(ConsoleLogRecipient.class);
	
	public ConsoleLogRecipient() {
	}
	
	
	@Override
	public void send(@NotNull Set<ScriptLoadInfo> loadInfoSet) {
		int errorCount = 0;
		int warnCount = 0;
		int failedLoads = 0;
		
		for (ScriptLoadInfo loadInfo : loadInfoSet) {
			if(!loadInfo.successful()) {
				failedLoads++;
				continue;
			}
			
			Path path = loadInfo.script();
			if(path == null) {
				failedLoads++;
				log.warn("script is null, but ScriptLoadInfo is marked as successful.");
				continue;
			}
			
			for (LogEntry logEntry : loadInfo.logs()) {
				switch(logEntry.getType()) {
					case WARNING -> warnCount++;
					case ERROR -> errorCount++;
				}
				
				logLogEntry(logEntry, path);
			}
		}
		
		// log final warn/error count
		if(errorCount == 0 && warnCount == 0) {
			sendSuccessfulReload();
			return;
		}
		
		log.info("Script parsing completed!");
		log.info("   - {} warnings", warnCount);
		log.info("   - {} errors", errorCount);
		log.info("   - {} scripts", loadInfoSet.size());
		
		if(failedLoads > 0) {
			log.warn("There was exceptions while reloading! Please report them at the github issue tracker.");
		}
		
	}
	
	private void logLogEntry(LogEntry logEntry, Path path) {
		String message = logEntry.getMessage();
		Optional<String> tipOpt = logEntry.getTip();
		int line = logEntry.getLine();
		LogType type = logEntry.getType();
		
		if(type == LogType.ERROR) {
			log.error("Error parsing Script: {}", message);
			log.error("   {}:{}: {}", path.relativize(SkriptClient.core.getScriptsFolder()), line, tipOpt.orElse("No tip provided."));
			log.error("");
		}
	}
	
	@Override
	public void sendNoScriptsFound() {
		log.warn("Skript-Client could not find any Scripts to load!");
		log.warn("Scripts should be located at {}", SkriptClient.core.getScriptsFolder().toFile().getAbsolutePath());
	}
	
	@Override
	public void sendSuccessfulReload() {
		log.info("Skript-Client successfully reloaded all Scripts without any errors!");
	}
}
