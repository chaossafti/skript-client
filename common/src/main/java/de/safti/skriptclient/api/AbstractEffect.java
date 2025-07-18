package de.safti.skriptclient.api;

import de.safti.skriptclient.api.exceptions.SyntaxRuntimeException;
import de.safti.skriptclient.api.pattern.Pattern;
import de.safti.skriptclient.api.pattern.PatternArgument;
import de.safti.skriptclient.logging.runtime.RuntimeLogger;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.log.ErrorType;
import io.github.syst3ms.skriptparser.log.SkriptLogger;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractEffect extends Effect {
	private final RuntimeLogger runtimeLogger = new RuntimeLogger();
	
	@Override
	public final boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
		SkriptLogger logger = parseContext.getLogger();
		List<PatternArgument<?>> patternArguments = getPattern().patternArguments();
		
		if(expressions.length != patternArguments.size()) {
			logger.error("Provided expression list unequal argument list; This is a Skript client error. Please report it to the Github issue tracker!", ErrorType.EXCEPTION);
			// TODO: better logging of this? it most likely wont happen anyway
			return false;
		}
		
		// init the pattern's argument's expressions
		for (int i = 0, expressionsLength = expressions.length; i < expressionsLength; i++) {
			Expression<?> expression = expressions[i];
			setArgumentExpression(i, expression);
		}
		
		return validate(matchedPattern, parseContext);
	}
	
	protected abstract boolean validate(int matchedPattern, @NotNull ParseContext parseContext);
	
	@Override
	protected final void execute(@NotNull TriggerContext triggerContext) {
		try {
			execute(triggerContext, runtimeLogger);
		}
		catch (SyntaxRuntimeException e) {
			// non-relevant exceptions are used to quick exit execute methods
			if(!e.isRelevant()) return;
			throw e;
		}
		
	}
	
	protected abstract void execute(@NotNull TriggerContext context, RuntimeLogger runtimeLogger);
	
	private <T> void setArgumentExpression(int index, Expression<T> expression) {
		//noinspection unchecked
		PatternArgument<T> patternArgument = (PatternArgument<T>) getPattern().patternArguments().get(index);
		patternArgument.setExpression(expression);
		
	}
	
	@NotNull
	protected abstract Pattern getPattern();
	
}
