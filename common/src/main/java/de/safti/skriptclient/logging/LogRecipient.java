package de.safti.skriptclient.logging;

import io.github.syst3ms.skriptparser.parsing.script.ScriptLoadResult;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface LogRecipient {
	// TODO: store all logs in a gui for later viewing
	
	void send(@NotNull Set<ScriptLoadResult> loadInfo);
	
	void sendNoScriptsFound();
	
	void sendSuccessfulReload(int totalScriptsLoaded);
	
}
