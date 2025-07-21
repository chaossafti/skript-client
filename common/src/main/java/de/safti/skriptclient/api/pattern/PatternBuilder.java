package de.safti.skriptclient.api.pattern;

import org.intellij.lang.annotations.RegExp;

import java.util.ArrayList;
import java.util.List;

public class PatternBuilder {
	private final StringBuilder stringBuilder = new StringBuilder();
	private final List<PatternArgument<?>> arguments = new ArrayList<>();

	
	public PatternBuilder() {
	
	}
	
	public PatternBuilder literal(String str) {
		if(!stringBuilder.isEmpty()) stringBuilder.append(" ");

		stringBuilder.append(str);
		return this;
	}

	public PatternBuilder regex(@RegExp String regex) {
		if(!stringBuilder.isEmpty()) stringBuilder.append(" ");

		stringBuilder.append("<")
				.append(regex)
				.append(">");

		return this;
	}

	
	public <A extends PatternArgument<T>, T> PatternBuilder argument(A argument) {
		if(!stringBuilder.isEmpty()) stringBuilder.append(" ");
		
		stringBuilder.append(argument.getPatternArgumentString());
		arguments.add(argument);
		
		return this;
	}
	
	
	public Pattern build() {
		return new Pattern(arguments, stringBuilder.toString().strip());
	}
	
	
}
