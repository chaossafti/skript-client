package de.safti.skriptclient.api;

import de.safti.skriptclient.SkriptClient;
import de.safti.skriptclient.api.event.EventContext;
import de.safti.skriptclient.api.event.GeneratedEvent;
import de.safti.skriptclient.api.pattern.PatternBundle;
import de.safti.skriptclient.api.synatxes.AbstractEffect;
import de.safti.skriptclient.api.synatxes.AbstractExpression;
import io.github.syst3ms.skriptparser.lang.SkriptEvent;
import io.github.syst3ms.skriptparser.lang.TriggerContext;
import io.github.syst3ms.skriptparser.registration.SkriptRegistration;
import io.github.syst3ms.skriptparser.registration.context.ContextValue;

import java.util.function.Function;
import java.util.function.Supplier;

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

	public static void registerEvent(Supplier<GeneratedEvent> supplier, String... patterns) {
		SkriptClient.INSTANCE
				.getRegistry()
				.newEvent(GeneratedEvent.class, patterns)
				.setSupplier(supplier)
				.register();
	}

    public static  <C extends TriggerContext, T> void registerEventValue(Class<C> context, Class<T> returnType, Function<C, T[]> function, boolean isSingle, String name) {
		SkriptClient.INSTANCE
				.getRegistry()
				.newContextValue(context, returnType, isSingle, name, function);
    }

    public static  <C extends TriggerContext, T> void registerEventValue(Class<C> context, Class<T> returnType, boolean isSingle, String name, Function<C, T[]> function) {
		SkriptClient.INSTANCE
				.getRegistry()
				.newContextValue(context, returnType, isSingle, name, function);
    }

    public static  <C extends TriggerContext, T> void registerEventValue(Class<C> context, Class<T> returnType, Function<C, T> function) {
        registerEventValue(context, returnType, function, ContextValue.Usage.EXPRESSION_OR_ALONE);
    }

    public static  <C extends TriggerContext, T> void registerEventValue(Class<C> context, Class<T> returnType, Function<C, T> function, ContextValue.Usage usage) {
        registerEventValue(context, returnType, function, ContextValue.State.PRESENT, usage);
    }

    public static  <C extends TriggerContext, T> void registerEventValue(Class<C> context, Class<T> returnType, Function<C, T> function, ContextValue.State state, ContextValue.Usage usage)  {
		SkriptClient.INSTANCE
				.getRegistry()
				.addContextType(context, returnType, function, state, usage);
	}

	public static <T> SkriptRegistration.TypeRegistrar<T> registerType(Class<T> clazz, String name, String pattern) {
		return SkriptClient.INSTANCE
				.getRegistry()
				.newType(clazz, name, pattern);
	}

}
