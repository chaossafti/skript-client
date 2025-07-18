package de.safti.skriptclient.api.pattern;

import de.safti.skriptclient.api.exceptions.SyntaxRuntimeException;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.types.Type;
import io.github.syst3ms.skriptparser.types.TypeManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Optional;

public class PatternArgument<T> {
	private final Type<T> type;
	@UnknownNullability
	private Expression<T> expression;
	private final boolean single;
	
	
	public PatternArgument(Class<T> clazz) {
		this(clazz, true);
	}
	
	public PatternArgument(Class<T> clazz, boolean single) {
		this(TypeManager.getByClassExact(clazz).orElseThrow(), single);
	}
	
	public PatternArgument(Type<T> type, boolean single) {
		this.type = type;
		this.single = single;
	}
	
	@ApiStatus.Internal
	public void setExpression(@NotNull Expression<T> expression) {
		this.expression = expression;
	}
	
	public Type<T> getType() {
		return type;
	}
	
	public String getPatternArgumentString() {
		return "%" +
				(single ? type.getBaseName() : type.getPluralForms()[0]) +
				"%";
	}
	
	public Optional<? extends T> resolveSingle(TriggerContext context) {
		return expression.getSingle(context);
	}
	
	public T resolveOrGo(TriggerContext context) {
		Optional<? extends T> optT = resolveSingle(context);
		return optT.orElseThrow(() -> SyntaxRuntimeException.IRRELEVANT);
	}
	
	public T[] resolveAll(TriggerContext context) {
		return expression.getValues(context);
	}
	
}
