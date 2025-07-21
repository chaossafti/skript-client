package de.safti.skriptclient.api.pattern;

import io.github.syst3ms.skriptparser.types.Type;
import io.github.syst3ms.skriptparser.types.TypeManager;
import org.intellij.lang.annotations.RegExp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PatternBundleBuilder {
	private final StringBuilder stringBuilder = new StringBuilder();
	private final Map<String, ExpressionArgumentInfo> expressionArguments = new HashMap<>();
	private final Map<String, RegexArgumentInfo> regexArguments = new HashMap<>();
	private final Set<PatternBuilder> incompletePatterns = new HashSet<>();


	public static PatternBundleBuilder builder() {
		return new PatternBundleBuilder();
	}

	
	public PatternBundleBuilder() {
	
	}

	public PatternBuilder newPattern() {
		return new PatternBuilder(this);
	}

	
	
	public PatternBundle build() {
		Set<PatternInfo> result = new HashSet<>();

		for (PatternBuilder patternBuilder : incompletePatterns) {
			PatternInfo info = new PatternInfo(patternBuilder.getExpressionArguments(), patternBuilder.getRegexArguments(), patternBuilder.toString());
			result.add(info);
		}
		return new PatternBundle(result.toArray(new PatternInfo[0]));
	}


	public PatternBundleBuilder registerExpressionArgument(String name, Class<?> typeClass, boolean plural) {
		Type<?> type = TypeManager.getByClass(typeClass).orElseThrow();

		expressionArguments.put(name, new ExpressionArgumentInfo(type, name, plural));
		return this;
	}

	public PatternBundleBuilder registerRegexArgument(String name, @RegExp String pattern) {
		regexArguments.put(name, new RegexArgumentInfo(name, pattern));
		return this;
	}

	void addIncompletePattern(PatternBuilder patternBuilder) {
		incompletePatterns.add(patternBuilder);
	}

	ExpressionArgumentInfo getExpressionArgumentInfo(String name) {
		return expressionArguments.get(name);
	}

	RegexArgumentInfo getRegexArgumentInfo(String name) {
		return regexArguments.get(name);
	}

	public record ExpressionArgumentInfo(Type<?> type, String name, boolean plural) {

		public String getPatternArgumentString() {
			return "%" + type.withIndefiniteArticle(plural) +  "%";
		}

	}

	public record RegexArgumentInfo(String name, String regex) {

	}

}
