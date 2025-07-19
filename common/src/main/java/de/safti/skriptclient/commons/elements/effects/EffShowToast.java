package de.safti.skriptclient.commons.elements.effects;

import de.safti.skriptclient.api.AbstractEffect;
import de.safti.skriptclient.api.pattern.Pattern;
import de.safti.skriptclient.api.pattern.PatternArgument;
import de.safti.skriptclient.api.pattern.PatternBuilder;
import org.jetbrains.annotations.NotNull;

public abstract class EffShowToast extends AbstractEffect {
	public static final Pattern PATTERN =
			new PatternBuilder()
					.literal("show toast with title")
					.argument(new PatternArgument<>(String.class))
					.literal("and message")
					.argument(new PatternArgument<>(String.class))
					.build();
	
	protected final PatternArgument<String> titleArgument = getArgument(0);
	protected final PatternArgument<String> messageArgument = getArgument(1);
	
	
	public EffShowToast() {
	}
	
	@Override
	@NotNull
	protected Pattern getPattern() {
		return PATTERN;
	}
}
