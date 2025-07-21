package de.safti.skriptclient.api.exceptions;

import de.safti.skriptclient.api.pattern.arguments.ExpressionPatternArgument;
import io.github.syst3ms.skriptparser.lang.TriggerContext;

public class SyntaxRuntimeException extends RuntimeException {
	public static final SyntaxRuntimeException IRRELEVANT = new SyntaxRuntimeException();
	
	public SyntaxRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public SyntaxRuntimeException(Throwable cause) {
		super(cause);
	}
	
	public SyntaxRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public SyntaxRuntimeException(String message) {
		super(message);
	}
	
	
	public SyntaxRuntimeException() {
	}
	/**
	 * Throwing an Exception that returns true on this method, won't get logged to console. It will simply exit the Execute, get or change method of a syntax.
	 * It's essentially equivalent to a return.
	 * @see ExpressionPatternArgument#resolveOrGo(TriggerContext)
	 */
	public boolean isRelevant() {
		return this == IRRELEVANT;
	}
}
