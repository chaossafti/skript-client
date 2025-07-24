package de.safti.skriptclient.api.tests;

import io.github.syst3ms.skriptparser.parsing.script.ScriptLoadResult;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public record ScriptTestResult(ScriptLoadResult loadContext, @Nullable TestFailContext failContext) {

    public static ScriptTestResult exception(ScriptLoadResult loadResult) {
        TestFailContext testFailContext = new TestFailContext("Script failed to load! Most likely an exception occurred", -1, null);
        return new ScriptTestResult(loadResult, testFailContext);
    }

    public static ScriptTestResult parseError(ScriptLoadResult loadResult) {
        TestFailContext testFailContext = new TestFailContext("Script failed to parse! Overcheck your syntaxes.", -1, loadResult.getScript());
        return new ScriptTestResult(loadResult, testFailContext);
    }

    public static ScriptTestResult noTriggersFound(ScriptLoadResult loadResult) {
        TestFailContext testFailContext = new TestFailContext("The script had no triggers.", -1, loadResult.getScript());
        return new ScriptTestResult(loadResult, testFailContext);
    }

    public static ScriptTestResult unreachedPoints(ScriptLoadResult loadResult, Set<String> points) {
        TestFailContext testFailContext = new TestFailContext("The script did not reach following points: " + points, -1, loadResult.getScript());
        return new ScriptTestResult(loadResult, testFailContext);
    }

    public static ScriptTestResult success(ScriptLoadResult loadResult) {
        return new ScriptTestResult(loadResult, null);
    }

}
