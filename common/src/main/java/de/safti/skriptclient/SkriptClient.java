package de.safti.skriptclient;

import de.safti.skriptclient.bridge.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkriptClient {
	public static final String MOD_ID = "skript-client";
	
	private static final Logger log = LoggerFactory.getLogger(SkriptClient.class);
	public static boolean IS_INITIALIZED = false;
	public static Core core;
	
	
	@SuppressWarnings("LoggingSimilarMessage")
	public static void init(Core core) {
		if(IS_INITIALIZED)
			throw new IllegalStateException("SkriptClient already initialized!");
		
		SkriptClient.core = core;
		
		// load the skript parser
		// this includes loading scripts
		SkriptParserBootstrap.initSkript(core);
	}
	
}
