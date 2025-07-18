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
	
	@Nullable
	public static Set<Class<? extends SyntaxElement>> NOT_LOADED_SYNTAXES;
	
	
	
	@SuppressWarnings("LoggingSimilarMessage")
	public static void init(Core core) {
		if(IS_INITIALIZED)
			throw new IllegalStateException("SkriptClient already initialized!");
		
		SkriptClient.core = core;
		
		// make sure all syntaxes have been implemented
		Reflections reflections = new Reflections(
				new ConfigurationBuilder()
						.forPackage("de.safti.skriptclient.commons.elements")
						.filterInputsBy(new FilterBuilder().includePackage("de.safti.skriptclient.commons.elements"))
						.setScanners(Scanners.values()));
		
		Set<Class<? extends SyntaxElement>> missingSyntaxes = new HashSet<>();
		
		SkriptRegistration registry = Parser.getMainRegistration();
		
		// effect, code section
		missingSyntaxes.addAll(getMissingImplementations(reflections, Effect.class, registry.getEffects()));
		missingSyntaxes.addAll(getMissingImplementations(reflections, CodeSection.class, registry.getSections()));
		
		// events
		List<SyntaxInfo<? extends SkriptEvent>> eventClasses = new ArrayList<>(registry.getEvents());
		missingSyntaxes.addAll(getMissingImplementations(reflections, SkriptEvent.class, eventClasses));
		
		// expressions are registered differently; use a separate helper method
		missingSyntaxes.addAll(getMissingImplementations(reflections));
		
		if(!missingSyntaxes.isEmpty()) {
			core.getClient().showErrorToast("Skript-Client had a critical error! Please check the logs for more information.", Duration.ofSeconds(10));
			
			log.error("Skript-Client has detected some Syntaxes to not be implemented.");
			log.error("Here is a full list of Syntaxes: ");
			for (Class<? extends SyntaxElement> missingSyntax : missingSyntaxes) {
				log.error("  - {}", missingSyntax.getName());
			}
			
			log.error("---------------------------------------------------------");
			log.error("Some syntaxes have not been implemented! This is a Skript-Client Bug.");
			log.error("Please report this at the Github Issue tracker at https://github.com/chaossafti/skript-client/issues");
			log.error("mod version: {} ({}); minecraft version: {}", core.getModVersionString(), core.getLoader(), core.getMinecraftVersionString());
			log.error("---------------------------------------------------------");
			
			log.error("Skript-client will continue working, but some Syntaxes might not work.");
			
			NOT_LOADED_SYNTAXES = missingSyntaxes;
		}
		
		// load skript aswell as all scripts inside the scripts folder
		
		try {
			loadScripts();
		}
		catch (IOException e) {
			log.error("Error whilst trying to load all scripts", e);
		}
		
	}
	
	
	public static void loadScripts() throws IOException {
		//noinspection resource
		Files.walk(core.getSkriptClientFolder())
				.filter(path -> path.toFile().getName().endsWith(".sk"))
				.forEach(path -> ScriptLoader.loadScript(path, false));
	}
	
	private static <T extends SyntaxElement> Set<Class<? extends T>> getMissingImplementations
			(Reflections reflections, Class<T> syntaxClass, List<SyntaxInfo<? extends T>> registered) {
		Set<Class<? extends T>> classes = reflections.getSubTypesOf(syntaxClass);
		
		Set<Class<? extends T>> registeredClasses =
				registered.stream()
						.map(SyntaxInfo::getSyntaxClass)
						.collect(Collectors.toSet());
		
		Set<Class<? extends T>> missing = new HashSet<>();
		for (Class<? extends T> requiredClass : classes) {
			if(!registeredClasses.contains(requiredClass)) {
				missing.add(requiredClass);
			}
		}
		
		return missing;
	}
	
	private static Set<Class<? extends SyntaxElement>> getMissingImplementations(Reflections reflections) {
		List<ExpressionInfo<?, ?>> registered = Parser.getMainRegistration().getExpressions().getAllValues();
		//noinspection rawtypes
		Set<Class<? extends Expression>> classes = reflections.getSubTypesOf(Expression.class);
		
		Set<Class<?>> registeredClasses =
				registered.stream()
						.map(SyntaxInfo::getSyntaxClass)
						.collect(Collectors.toSet());
		
		Set<Class<? extends SyntaxElement>> missing = new HashSet<>();
		for (Class<? extends Expression<?>> requiredClass : classes) {
			if(!registeredClasses.contains(requiredClass)) {
				missing.add(requiredClass);
			}
		}
		
		return missing;
	}
	
}
