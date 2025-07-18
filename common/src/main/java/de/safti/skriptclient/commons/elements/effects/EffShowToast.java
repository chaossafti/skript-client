package de.safti.skriptclient.commons.elements.effects;

import de.safti.skriptclient.api.AbstractEffect;
import de.safti.skriptclient.api.pattern.Pattern;
import de.safti.skriptclient.api.pattern.PatternArgument;
import de.safti.skriptclient.api.pattern.PatternBuilder;
import org.jetbrains.annotations.NotNull;

public abstract class EffShowToast extends AbstractEffect {
	protected final PatternArgument<String> titleArgument = new PatternArgument<>(String.class);
	protected final PatternArgument<String> messageArgument = new PatternArgument<>(String.class);
	
	private final Pattern pattern =
			new PatternBuilder()
					.literal("show toast with title")
					.argument(titleArgument)
					.literal("and message")
					.argument(messageArgument)
					.build();
	
	public EffShowToast() {
	}
	
	@Override
	@NotNull
	protected Pattern getPattern() {
		return pattern;
	}
}
