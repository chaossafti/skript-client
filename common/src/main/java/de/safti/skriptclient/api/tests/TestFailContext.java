package de.safti.skriptclient.api.tests;

import io.github.syst3ms.skriptparser.parsing.script.Script;

public record TestFailContext(String reason, int line, Script script) {
}
