package de.safti.skriptclient.api.tests;

import de.safti.skriptclient.SkriptParserBootstrap;
import de.safti.skriptclient.api.tests.elements.events.EvtTestRequirements;
import de.safti.skriptclient.api.tests.elements.events.EvtTestRequirements.TestRequirementsContext;
import de.safti.skriptclient.api.tests.elements.events.EvtTestRun;
import de.safti.skriptclient.api.tests.elements.events.EvtTestRun.TestRunContext;
import de.safti.skriptclient.logging.ConsoleLogRecipient;
import io.github.syst3ms.skriptparser.lang.Statement;
import io.github.syst3ms.skriptparser.lang.Trigger;
import io.github.syst3ms.skriptparser.log.LogEntry;
import io.github.syst3ms.skriptparser.parsing.script.Script;
import io.github.syst3ms.skriptparser.parsing.script.ScriptLoadResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class SkriptTester {
    private static final Path TEST_DIR_PATH = Paths.get("").toAbsolutePath().getParent().getParent().resolve("test scripts");
    private static final Logger log = LoggerFactory.getLogger(SkriptTester.class);

    public static SkriptTester init() {
        SkriptParserBootstrap.registerStandaloneSyntaxPackage(SkriptTester.class, "de.safti.skriptclient.api.tests.elements");
        return new SkriptTester();
    }

    private int successes;
    private int failures;


    public void runAllTests() {
        log.info("");
        log.info("Running all tests");

        try(var pathStream = Files.walk(TEST_DIR_PATH)) {
            pathStream
                    .filter(path -> path.toFile().getName().endsWith(".sk"))
                    .forEach(path -> {
                        ScriptTestResult scriptTestResult = runTestScript(path);
                        handleTestResult(scriptTestResult);
                    });


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("Succeeded: {}", successes);
        log.info("failures: {}", failures);
        log.info("Ran all tests.");
        log.info("");

    }

    private ScriptTestResult runTestScript(Path path) {
        // load the script
        ScriptLoadResult scriptLoadResult = SkriptParserBootstrap.loadScript(path);

        if(!scriptLoadResult.hasParsedSuccessfully()) {
            return ScriptTestResult.exception(scriptLoadResult);
        }

        // parsing errors
        if(scriptLoadResult.getLog().isPresent() && !scriptLoadResult.getLog().get().isEmpty()) {
            return ScriptTestResult.parseError(scriptLoadResult);
        }

        // get variables and validate trigger presence
        Script script = scriptLoadResult.getScript();
        assert script != null;
        Set<Trigger> triggers = script.getTriggers();
        if(triggers == null || triggers.isEmpty()) {
            return ScriptTestResult.noTriggersFound(scriptLoadResult);
        }

        // call the test requirements "event"
        TestRequirementsContext requirementsContext = new TestRequirementsContext();

        for (Trigger trigger : triggers) {
            if(!(trigger.getEvent() instanceof EvtTestRequirements)) {
                continue;
            }

            Statement.runAll(trigger, requirementsContext);
        }


        // call the test run event
        TestRunContext runContext = new TestRunContext(new HashSet<>());

        for (Trigger trigger : triggers) {
            if(!(trigger.getEvent() instanceof EvtTestRun)) {
                continue;
            }

            Statement.runAll(trigger, runContext);
        }

        Set<String> requiredPoints = requirementsContext.requiredPoints();
        Set<String> reachedPoints = runContext.reachedPoints();

        requiredPoints.removeAll(reachedPoints);

        if(!requiredPoints.isEmpty()) {
            return ScriptTestResult.unreachedPoints(scriptLoadResult, requiredPoints);
        }

        return ScriptTestResult.success(scriptLoadResult);
    }

    private void handleTestResult(ScriptTestResult testResult) {
        Script script = testResult.loadContext().getScript();
        assert script != null;

        if(testResult.failContext() == null) {
            successes++;
            log.info("test {} passed", script.getPath().toFile().getName());
            return;
        }


        failures++;
        log.warn("test {} failed!", script.getPath().toFile().getName());
        if(!testResult.loadContext().hasParsedSuccessfully()) {
            log.warn("A loading error happened!");
            ConsoleLogRecipient.INSTANCE.send(Set.of(testResult.loadContext()));
            return;
        }

        TestFailContext failContext = testResult.failContext();

        log.warn("Reason: {}", failContext.reason());
        if(failContext.line() != -1) {
            log.warn("Line: {}", failContext.line());
        }


        if(testResult.loadContext().getLog().isPresent() && !testResult.loadContext().getLog().get().isEmpty()) {
            log.warn("A SkriptLog was found!");
            for (LogEntry entry : testResult.loadContext().getLog().get()) {
                log.info("Message: {}", entry.getMessage());
                log.info("LogType: {}", entry.getType());
                log.info("ErrorTYpe: {}", entry.getErrorType());
            }

        }


    }


}
