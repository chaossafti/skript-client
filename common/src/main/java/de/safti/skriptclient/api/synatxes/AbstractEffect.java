package de.safti.skriptclient.api.synatxes;

import de.safti.skriptclient.api.exceptions.SyntaxRuntimeException;
import de.safti.skriptclient.api.pattern.ResolvedPattern;
import de.safti.skriptclient.logging.runtime.RuntimeLogger;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractEffect extends Effect implements ArgumentDrivenSyntax {
	private final RuntimeLogger runtimeLogger = new RuntimeLogger();

	@Override
	public final boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull ParseContext parseContext) {
		personalPattern = getPatternBundle().resolve(matchedPattern, expressions, parseContext);
		return validate(matchedPattern, parseContext);
	}

	private ResolvedPattern personalPattern;


	@Override
	public ResolvedPattern getPersonalPattern() {
		return personalPattern;
	}

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

}
