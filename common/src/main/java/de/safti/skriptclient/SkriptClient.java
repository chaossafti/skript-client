package de.safti.skriptclient;

import de.safti.skriptclient.bridge.Core;
import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.*;
import io.github.syst3ms.skriptparser.parsing.ScriptLoader;
import io.github.syst3ms.skriptparser.registration.ExpressionInfo;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.registration.SyntaxInfo;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SkriptClient {
	public static final String MOD_ID = "skript-client";
	
	private static final Logger log = LoggerFactory.getLogger(SkriptClient.class);
	public static boolean IS_INITIALIZED = false;
	public static Core core;
	
	
	@SuppressWarnings("LoggingSimilarMessage")
	public static void init(Core core) {
		System.out.println("SkriptClient.init");
		if(IS_INITIALIZED)
			throw new IllegalStateException("SkriptClient already initialized!");
		
		SkriptClient.core = core;
		
		// load the skript parser
		// this includes loading scripts
		SkriptParserBootstrap.initSkript(core);
	}
	
}
