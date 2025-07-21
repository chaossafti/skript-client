package de.safti.skriptclient.api;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.api.pattern.PatternBundle;
import de.safti.skriptclient.api.pattern.PatternInfo;
import de.safti.skriptclient.api.synatxes.AbstractEffect;
import de.safti.skriptclient.api.synatxes.AbstractExpression;
import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.TriggerContext;

public class SkriptRegistry {
	
	public static void registerEffect(Class<? extends AbstractEffect> clazz, PatternBundle patternBundle) {
		SkriptClient.INSTANCE
				.getRegistry()
				.addEffect(clazz, patternBundle.extractPatternStrings());
	}


	public static <C extends AbstractExpression<T>, T> void registerExpression(Class<C> clazz, Class<T> returnType, boolean isSingle, PatternBundle patternBundle) {
		SkriptClient.INSTANCE
				.getRegistry()
				.newExpression(clazz, returnType, isSingle, patternBundle.extractPatternStrings())
				.register();
	}



	public static void registerEvent(Class<? extends SkriptEvent> clazz, Class<? extends TriggerContext> context, PatternBundle patternBundle) {
		SkriptClient.INSTANCE
				.getRegistry()
				.newEvent(clazz, patternBundle.extractPatternStrings())
				.setHandledContexts(context)
				.register();
	}


	
}
