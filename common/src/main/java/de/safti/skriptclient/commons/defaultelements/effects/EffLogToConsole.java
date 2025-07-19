package de.safti.skriptclient.commons.defaultelements.effects;

import io.github.syst3ms.skriptparser.Parser;
import io.github.syst3ms.skriptparser.lang.Effect;
import io.github.syst3ms.skriptparser.lang.Expression;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.parsing.ParseContext;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EffLogToConsole extends Effect {
	private Expression<String> stringExpression;
	
	static {
		Parser.getMainRegistration()
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
		Optional<String> messageOpt = (Optional<String>) stringExpression.getSingle(triggerContext);
		
		messageOpt.ifPresent(System.out::println);
	}
	
	@Override
	public String toString(@NotNull TriggerContext triggerContext, boolean b) {
		return "log to console";
	}
}
