package de.safti.skriptclient.logging;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface LogRecipient {
	// TODO: store all logs in a gui for later viewing
	
	void send(@NotNull Set<ScriptLoadInfo> loadInfo);
	
	void sendNoScriptsFound();
	
	void sendSuccessfulReload();
	
}
