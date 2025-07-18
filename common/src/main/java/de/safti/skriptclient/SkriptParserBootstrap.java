package de.safti.skriptclient;

import de.safti.skriptclient.bridge.Core;
import de.safti.skriptclient.bridge.Loader;
import de.safti.skriptclient.logging.ConsoleLogRecipient;
import de.safti.skriptclient.logging.ScriptLoadInfo;
import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.*;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.log.LogEntry;
import io.github.syst3ms.skriptparser.log.LogType;
import io.github.syst3ms.skriptparser.parsing.ScriptLoader;
import io.github.syst3ms.skriptparser.registration.ExpressionInfo;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.registration.SyntaxInfo;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SkriptParserBootstrap {
	
	private static final Logger log = LoggerFactory.getLogger(SkriptParserBootstrap.class);
	
	private SkriptParserBootstrap() {
	}
	
	public static void initSkript(Core core) {
		System.out.println("SkriptParserBootstrap.initSkript");
		// first step is to init the skript parser
		initParser(core);
		
		// before loading all scripts or mixins, make sure that all required syntaxes have been implemented
		Set<Class<? extends SyntaxElement>> missingSyntaxes = getNotImplementedSyntaxes(core);
		if(!missingSyntaxes.isEmpty()) {
			core.getClient().runOnStartup(() -> logMissingDependencies(core, missingSyntaxes));
			
		}
		
		// mixins will be loaded here, if ever supported
		
		// load all scripts while making sure to keep logs
		Set<ScriptLoadInfo> logs;
		try {
			logs = loadScripts(core);
		}
		catch (IOException e) {
			log.error("Error whilst loading scripts: ", e);
			LogEntry logEntry = new LogEntry(
					"Could not load Skripts!", LogType.ERROR,
					-1, List.of(), ErrorType.EXCEPTION,
					"Skript-Reflect had an IOException whilst loading scripts; Please report this on the github issue tracker!");
			logs = Set.of(new ScriptLoadInfo(Set.of(logEntry), null, false));
		}
		
		// log the info;
		// currently this only logs to console, but a custom screen will be implemented sometime.
		ConsoleLogRecipient.INSTANCE.send(logs);
	}
	
	private static void logMissingDependencies(Core core, Set<Class<? extends SyntaxElement>> missingSyntaxes) {
		core.getClient().showErrorToast( "Skript-Client Error", "Please check the logs for more information.", Duration.ofSeconds(10));
		
		// fixme: log.error doesn't log to console (?)
		// tested with fabric
		System.out.println("e");
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
	}
	
	private static void initParser(@NotNull Core core) {
		Loader loader = core.getLoader();
		if(!loader.isImplemented()) {
			log.warn("Skript-Loader is starting with a not officially supported loader!");
		}
		
		String loaderPackageName = loader.toString().toLowerCase();
		
		String[] mainPackages = new String[] {"de.safti.skriptclient." + loaderPackageName + ".elements"};
		String[] subPackages = core.getSubPackagesToLoad();
		String[] javaArgs = new String[] {};
		
		// make sure standalone is set to false; we will take care of registering skript addons in the future
		Parser.init(mainPackages, subPackages, javaArgs, false);
	}
	
	private static Set<ScriptLoadInfo> loadScripts(Core core) throws IOException {
		Set<ScriptLoadInfo> logs = new HashSet<>();
		
		//noinspection resource
		Files.walk(core.getSkriptClientFolder())
				.filter(path -> path.toFile().getName().endsWith(".sk"))
				.forEach(path -> logs.add(loadScript(path)));
		
		
		return logs;
	}
	
	private static ScriptLoadInfo loadScript(Path path) {
		Set<LogEntry> logs = ScriptLoader.loadScript(path, false)
				.stream().collect(Collectors.toUnmodifiableSet());
		
		return new ScriptLoadInfo(logs, path, true);
	}
	
	private static Set<Class<? extends SyntaxElement>> getNotImplementedSyntaxes(Core core) {
		Set<Class<? extends SyntaxElement>> missingSyntaxes = new HashSet<>();
		SkriptRegistration registry = Parser.getMainRegistration();
		Reflections reflections = new Reflections(
				new ConfigurationBuilder()
						.forPackage("de.safti.skriptclient.commons.elements")
						// the commons.elements package has all Syntaxes that must be implemented
						.filterInputsBy(new FilterBuilder().includePackage("de.safti.skriptclient.commons.elements"))
						.setScanners(Scanners.values()));
		
		// effect, code section
		missingSyntaxes.addAll(getMissingImplementations(reflections, Effect.class, registry.getEffects()));
		missingSyntaxes.addAll(getMissingImplementations(reflections, CodeSection.class, registry.getSections()));
		
		// events
		List<SyntaxInfo<? extends SkriptEvent>> eventClasses = new ArrayList<>(registry.getEvents());
		missingSyntaxes.addAll(getMissingImplementations(reflections, SkriptEvent.class, eventClasses));
		
		// expressions are registered differently; use a separate helper method
		missingSyntaxes.addAll(getMissingImplementations(reflections));
		
		return missingSyntaxes;
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
		for (var requiredClass : classes) {
			if(!registeredClasses.contains(requiredClass)) {
				missing.add(requiredClass);
			}
		}
		
		return missing;
	}
	
	
	
}
