package de.safti.skriptclient.api.pattern.arguments;

import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.types.Type;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Optional;

public class ExpressionPatternArgument<T> implements PatternArgument<T> {
    private final String name;
    private final Type<T> type;
	@UnknownNullability
	private final Expression<T> expression;

	
	public ExpressionPatternArgument(String name, Type<T> type, Expression<T> expression) {
        this.name = name;
        this.type = type;
		this.expression = expression;
	}

	public String getName() {
		return name;
	}

	public Type<T> getType() {
		return type;
	}

	@Override
	public boolean isPlural() {
		return !expression.isSingle();
	}

	@Override
	public Optional<? extends T> resolveSingle(TriggerContext context) {
		return expression.getSingle(context);
	}

	@Override
	public T[] resolveAll(TriggerContext context) {
		return expression.getArray(context);
	}

	@Override
	public String toString(TriggerContext triggerContext, boolean b) {
		return expression.toString(triggerContext, b);
	}

	public @UnknownNullability Expression<T> getExpression() {
		return expression;
	}
}
