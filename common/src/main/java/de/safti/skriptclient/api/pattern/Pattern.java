package de.safti.skriptclient.api.pattern;

import java.util.List;

public record Pattern(List<PatternArgument<?>> patternArguments, String pattern) {
}
