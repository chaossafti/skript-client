package de.safti.skriptclient;

import de.safti.skriptclient.bridge.Core;
import de.safti.skriptclient.commons.standalone.events.EvtLoad;
import de.safti.skriptclient.logging.ConsoleLogRecipient;
import io.github.syst3ms.skriptparser.lang.*;
import io.github.syst3ms.skriptparser.parsing.ScriptLoader;
import io.github.syst3ms.skriptparser.parsing.script.Script;
import io.github.syst3ms.skriptparser.parsing.script.ScriptLoadResult;
import io.github.syst3ms.skriptparser.registration.*;
import io.github.syst3ms.skriptparser.util.FileUtils;
import io.github.syst3ms.skriptparser.util.MultiMap;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class SkriptParserBootstrap {
    private static final MultiMap<SyntaxPackageType, PackageInfo> MAIN_PACKAGES;
    private static final Set<String> SUB_PACKAGES;

    private static final Logger log = LoggerFactory.getLogger(SkriptParserBootstrap.class);


    static {
        MAIN_PACKAGES = new MultiMap<>();
        SUB_PACKAGES = new HashSet<>();


        registerCommonsSyntaxPackage(SkriptParserBootstrap.class,"de.safti.skriptclient.commons", "expressions", "effects", "events", "types", "tags");
        registerSyntaxPackage(SkriptParserBootstrap.class, "de.safti.skriptclient." + SkriptClient.CORE.getLoader().toString().toLowerCase(), "structures", "properties");
        registerStandaloneSyntaxPackage(SkriptParserBootstrap.class, "de.safti.skriptclient.commons.standalone");
    }


    /**
     * Registering a syntax package will class load all classes within the package.
     * <p>
     * The way this exactly works, is that the main class will be concatenated with every EVER registered subpackage.
     * That includes sub packages that other addons have been registered, or default subpackages.
     * The result of the concatenated packages will have every class it contains loaded.
     * It will not have any packages inside these subpackages loaded.
     * <p>
     *
     *
     * <p>
     * Default sub packages include: expressions, effects, sections, types, properties, events, structures and tags.
     *
     * @param sampleClass A sample class from your project. This is required to load classes.
     * @param mainPackage The main package
     * @param subPackages The sub packages
     * @see #registerCommonsSyntaxPackage(Class, String, String...)
     * @see #registerStandaloneSyntaxPackage(Class, String, String...)
     */
    public static void registerSyntaxPackage(Class<?> sampleClass, String mainPackage, String... subPackages) {
        MAIN_PACKAGES.putOne(SyntaxPackageType.IMPL, new PackageInfo(mainPackage, sampleClass));

        SkriptParserBootstrap.SUB_PACKAGES.addAll(Arrays.asList(subPackages));
    }

    /**
     * A common syntax package works the same as when registering a normal syntax package.
     * The only difference being, any class loaded will have to be implemented externally.
     * If a class couldn't be found that implements a common class,
     * an error is logged to console and skript-client will continue to run normally.
     *
     * @param sampleClass A sample class from your project. This is required to load classes.
     * @param mainPackage The main package
     * @param subPackages The sub packages
     * @see FileUtils#loadClasses(File, String, String...)
     * @see #registerSyntaxPackage(Class, String, String...)
     * @see #registerStandaloneSyntaxPackage(Class, String, String...)
     */
    public static void registerCommonsSyntaxPackage(Class<?> sampleClass, String mainPackage, String... subPackages) {
        MAIN_PACKAGES.putOne(SyntaxPackageType.COMMONS, new PackageInfo(mainPackage, sampleClass));

        SkriptParserBootstrap.SUB_PACKAGES.addAll(Arrays.asList(subPackages));
    }

    /**
     * A standalone package will have
     * the classes loaded the same way explained in {@link #registerSyntaxPackage(Class, String, String...)}.
     * These packages will be loaded, without any other checks to them.
     *
     * @param sampleClass A sample class from your project. This is required to load classes.
     * @param mainPackage The main package
     * @param subPackages The sub packages
     * @see FileUtils#loadClasses(File, String, String...)
     * @see #registerSyntaxPackage(Class, String, String...)
     * @see #registerCommonsSyntaxPackage(Class, String, String...)
     */
    public static void registerStandaloneSyntaxPackage(Class<?> sampleClass, String mainPackage, String... subPackages) {
        MAIN_PACKAGES.putOne(SyntaxPackageType.STANDALONE, new PackageInfo(mainPackage, sampleClass));

        SkriptParserBootstrap.SUB_PACKAGES.addAll(Arrays.asList(subPackages));
    }

    private enum SyntaxPackageType {
        IMPL,
        COMMONS,
        STANDALONE,

    }

    private record PackageInfo(String name, Class<?> sampleClass) {
    }




    static void initSkript(Core core) {
        // Initialize types before anything else
        // types are required to load syntaxes
        DefaultRegistration.register(SkriptClient.INSTANCE.getRegistry());

        // before loading all scripts or mixins, make sure that all required syntaxes have been implemented
        // this, at the same time, also loads the Syntaxes for the first time.
        Set<Class<? extends SyntaxElement>> missingSyntaxes = getNotImplementedSyntaxes();
        if(!missingSyntaxes.isEmpty()) {
            core.getClient().runOnStartup(() -> logMissingDependencies(core, missingSyntaxes));
        }

        // load all standalone syntaxes
        loadSyntaxPackage(SyntaxPackageType.STANDALONE);

        // mixins will be loaded here, if ever supported

        // freeze the registry; nothing cna be registered anymore.
        // this also loads java Types.
        SkriptClient.INSTANCE.getRegistry()
                .register();


        // load all the scripts; make sure to keep the ScriptLoadResult's, because they contain the logs.
        Set<ScriptLoadResult> loadResults;

        try {
            if(SkriptClient.IS_TEST_ENV) {
                Path rootRelative = Paths.get("").toAbsolutePath().normalize();
                Path targetDir = rootRelative.resolve("test scripts");
                loadResults = loadScripts(targetDir);
            } else {
                loadResults = loadScripts(core.getScriptsFolder());
            }

        } catch (IOException e) {
            log.error("Error whilst loading scripts: ", e);
            return;
        }

        // log the info;
        // currently this only logs to console, but a custom screen will be implemented sometime.
        ConsoleLogRecipient.INSTANCE.send(loadResults);
    }

    private static void logMissingDependencies(Core core, Set<Class<? extends SyntaxElement>> missingSyntaxes) {
        core.getClient().showErrorToast("Skript-Client Error", "Please check the logs for more information.", Duration.ofSeconds(10));

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

    private static Set<ScriptLoadResult> loadScripts(Path scripts) throws IOException {
        Set<ScriptLoadResult> loadResult = new HashSet<>();

        //noinspection resource
        Files.walk(scripts)
                .filter(path -> path.toFile().getName().endsWith(".sk"))
                .forEach(path -> loadResult.add(loadScript(path)));


        return loadResult;
    }

    /**
     * Loads a script and also triggers {@link EvtLoad}, if available.
     *
     * @param path The path of the script
     * @return The ScriptLoadResult
     */
    @NotNull
    private static ScriptLoadResult loadScript(Path path) {
        // ScriptLoader handles parsing
        ScriptLoadResult scriptLoadResult = ScriptLoader.loadScript(path, false);

        // validate that the script parsed successfully
        if(!scriptLoadResult.hasParsedSuccessfully()) {
            log.error("Failed to parse Script: {}", path);
            return scriptLoadResult;
        }
        Script script = scriptLoadResult.getScript();

        // these should both be false in case the script loaded successfully, which we checked.
        assert script != null;
        assert script.getTriggers() != null;

        // skript-parser doesn't call the script load event by default.
        // we have to do it ourselves here

        EvtLoad.ScriptLoadContext context = new EvtLoad.ScriptLoadContext(scriptLoadResult.getScript());

        for (Trigger trigger : script.getTriggers()) {
            if(!(trigger.getEvent() instanceof EvtLoad)) {
                continue;
            }

            // .walk calls event.check internally already; we don't need to.
            Statement.runAll(trigger, context);
        }


        return scriptLoadResult;
    }

    private static Set<Class<? extends SyntaxElement>> getNotImplementedSyntaxes() {
        Set<Class<? extends SyntaxElement>> missingSyntaxes = new HashSet<>();
        SkriptRegistration registry = SkriptClient.INSTANCE.getRegistry();

        // scan commons package for required classes
        Set<Class<?>> allClasses = loadSyntaxPackage(SyntaxPackageType.COMMONS);

        // load all implementations - this *should* register them to the registry
        loadSyntaxPackage(SyntaxPackageType.IMPL);

        // effect, code section
        missingSyntaxes.addAll(getMissingImplementations(allClasses, Effect.class, registry.getEffects()));
        missingSyntaxes.addAll(getMissingImplementations(allClasses, CodeSection.class, registry.getSections()));

        // events
        List<SyntaxInfo<? extends SkriptEvent>> eventClasses = new ArrayList<>(registry.getEvents());
        missingSyntaxes.addAll(getMissingImplementations(allClasses, SkriptEvent.class, eventClasses));

        // expressions are registered differently; use a separate helper method
        missingSyntaxes.addAll(getMissingImplementations(allClasses));

        return missingSyntaxes;
    }

    private static @NotNull Set<Class<?>> loadSyntaxPackage(SyntaxPackageType type) {
        Set<Class<?>> result = new HashSet<>();

        try {

            for (PackageInfo packageInfo : MAIN_PACKAGES.get(type)) {

                String packageName = packageInfo.name;
                Class<?> sampleClass = packageInfo.sampleClass;
                File jarFile = FileUtils.getJarFile(sampleClass);
                // we have to make a new array every iteration due to FileUtils.loadClasses editing the array
                String[] subPackagesArr = SUB_PACKAGES.toArray(new String[0]);

                result.addAll(FileUtils.loadClasses(jarFile, packageName, subPackagesArr));
            }

            File jarFile = FileUtils.getJarFile(SkriptParserBootstrap.class);
            FileUtils.loadClasses(jarFile, "de.safti.skriptclient.commons.elements", "expressions", "effects");
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private static <T extends SyntaxElement> Set<Class<? extends T>> getMissingImplementations
            (Set<Class<?>> allClasses, Class<T> syntaxClass, List<SyntaxInfo<? extends T>> registered) {
        Set<Class<? extends T>> classes = new HashSet<>();
        for (Class<?> clazz : allClasses) {
            if(clazz.isAssignableFrom(syntaxClass)) {
                classes.add((Class<? extends T>) clazz);
            }
        }

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

    private static Set<Class<? extends SyntaxElement>> getMissingImplementations(Set<Class<?>> allClasses) {
        List<ExpressionInfo<?, ?>> registered = SkriptClient.INSTANCE.getRegistry().getExpressions().getAllValues();
        //noinspection rawtypes
        Set<Class<? extends Expression>> classes = new HashSet<>();
        for (Class<?> clazz : allClasses) {
            if(clazz.isAssignableFrom(Expression.class)) {
                classes.add((Class<? extends Expression>) clazz);
            }
        }

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



    private SkriptParserBootstrap() {
    }

}
