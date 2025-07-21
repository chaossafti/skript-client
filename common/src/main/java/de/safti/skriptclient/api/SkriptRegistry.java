package de.safti.skriptclient.api;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.api.pattern.Pattern;
import de.safti.skriptclient.api.synatxes.AbstractEffect;
import de.safti.skriptclient.api.synatxes.AbstractExpression;
import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.TriggerContext;

public class SkriptRegistry {
	
	public static void registerEffect(Class<? extends AbstractEffect> clazz, Pattern pattern) {
		SkriptClient.INSTANCE
				.getRegistry()
				.addEffect(clazz, pattern.pattern());
	}


	public static <C extends AbstractExpression<T>, T> void registerExpression(Class<C> clazz, Class<T> returnType, boolean isSingle, Pattern pattern) {
		SkriptClient.INSTANCE
				.getRegistry()
				.newExpression(clazz, returnType, isSingle, pattern.pattern())
				.register();
	}



	public static void registerEvent(Class<? extends SkriptEvent> clazz, Class<? extends TriggerContext> context, Pattern pattern) {
		SkriptClient.INSTANCE
				.getRegistry()
				.newEvent(clazz, pattern.pattern())
				.setHandledContexts(context)
				.register();
	}


	
}
