package de.safti.skriptclient.commons.standalone.effects;

import de.safti.skriptclient.SkriptClient;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EffLogToConsole extends Effect {
	private Expression<String> stringExpression;
	
	static {
		SkriptClient.INSTANCE
				.getRegistry()
				.addEffect(EffLogToConsole.class, "log %string% [to console]");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] expressions, int i, @NotNull ParseContext parseContext) {
		this.stringExpression = (Expression<String>) expressions[0];
		return true;
	}
	
	@Override
	protected void execute(@NotNull TriggerContext triggerContext) {
		Optional<? extends String> messageOpt = stringExpression.getSingle(triggerContext);
		messageOpt.ifPresent(System.out::println);
	}
	
	@Override
	public String toString(@Nullable TriggerContext triggerContext, boolean b) {
		return "log to console";
	}
}
