package de.safti.skriptclient.commons.elements.effects;

import de.safti.skriptclient.api.pattern.PatternBundle;
import de.safti.skriptclient.api.synatxes.AbstractEffect;
import de.safti.skriptclient.api.pattern.arguments.ExpressionPatternArgument;
import de.safti.skriptclient.api.pattern.PatternBundleBuilder;
import org.jetbrains.annotations.NotNull;

public abstract class EffShowToast extends AbstractEffect {
	private static final String TITLE = "title";
	private static final String MESSAGE = "message";

	public static final PatternBundle PATTERNS =
			PatternBundleBuilder.builder()
					.registerExpressionArgument("title", String.class, false)
					.registerExpressionArgument("message", String.class, false)
					.newPattern()
						.literal("show toast with title")
						.argument(TITLE)
						.literal("and message")
						.argument(MESSAGE)
						.build()
					.build();
	
	protected final ExpressionPatternArgument<String> titleArgument = getArgument(TITLE);
	protected final ExpressionPatternArgument<String> messageArgument = getArgument(MESSAGE);
	


	public EffShowToast() {

	}


	
	@Override
	@NotNull
	public PatternBundle getPatternBundle() {
		return PATTERNS;
	}
}
